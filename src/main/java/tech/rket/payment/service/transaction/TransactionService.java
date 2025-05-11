package tech.rket.payment.service.transaction;

import tech.rket.payment.domain.attempt.Attempt;
import tech.rket.payment.domain.attempt.AttemptNotifyStatus;
import tech.rket.payment.domain.attempt.AttemptRepository;
import tech.rket.payment.domain.attempt.AttemptStatus;
import tech.rket.payment.domain.owner.Owner;
import tech.rket.payment.domain.shared.IPG;
import tech.rket.payment.domain.transaction.OwnerSelfConfig;
import tech.rket.payment.domain.transaction.Transaction;
import tech.rket.payment.domain.transaction.TransactionRepository;
import tech.rket.payment.domain.transaction.TransactionStatus;
import tech.rket.payment.infrastructure.config.PaymentConfig;
import tech.rket.payment.infrastructure.dto.Redirect;
import tech.rket.payment.infrastructure.dto.SingableData;
import tech.rket.payment.infrastructure.dto.attempt.AttemptCallback;
import tech.rket.payment.infrastructure.dto.attempt.AttemptSettlement;
import tech.rket.payment.infrastructure.dto.attempt.AttemptVerify;
import tech.rket.payment.infrastructure.dto.attempt.AttemptWebhook;
import tech.rket.payment.infrastructure.dto.attempt.enums.AttemptCallbackStatus;
import tech.rket.payment.infrastructure.dto.attempt.enums.AttemptSettlementStatus;
import tech.rket.payment.infrastructure.dto.attempt.enums.AttemptVerifyStatus;
import tech.rket.payment.infrastructure.dto.transaction.TransactionDTO;
import tech.rket.payment.infrastructure.error.ActiveAttemptIsAlreadyExists;
import tech.rket.payment.infrastructure.error.TransactionDoesNotExistException;
import tech.rket.payment.infrastructure.error.TransactionIsFinalizedCurrentlyException;
import tech.rket.payment.infrastructure.feign.ApiClientBean;
import tech.rket.payment.infrastructure.feign.webhook.WebhookFeign;
import tech.rket.payment.infrastructure.mapper.TransactionMapper;
import tech.rket.payment.service.ipg.IpgFactory;
import tech.rket.payment.service.ipg.IpgService;
import tech.rket.payment.service.owner.OwnerService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionService {
    @Getter
    private final TransactionRepository repository;
    private final TransactionMapper mapper;
    private final OwnerService ownerService;
    private final IpgFactory ipgFactory;
    private final AttemptRepository attemptRepository;
    private final PaymentConfig config;
    private final ApiClientBean apiClientBean;

    protected Transaction classifiedId(BigInteger id) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        return transaction;
    }


    @Transactional(readOnly = true)
    public Transaction get(String ownerIdentifier, String referenceId) {
        return repository.findByOwner_IdentifierAndReference(ownerIdentifier, referenceId)
                .orElseThrow(() -> new TransactionDoesNotExistException(ownerIdentifier, referenceId));
    }

    @Transactional(readOnly = true)
    public Transaction getActive(String ownerIdentifier, String referenceId) {
        return repository.findActiveTransaction(ownerIdentifier, referenceId)
                .orElseThrow(() -> new TransactionDoesNotExistException(ownerIdentifier, referenceId));
    }

    @Transactional(readOnly = true)
    public Transaction get(Long referenceAttempt) {
        return getByReferenceAttempt(referenceAttempt);
    }

    private Transaction getByReferenceAttempt(Long referenceAttempt) {
        return attemptRepository.findByReference(referenceAttempt)
                .map(Attempt::getTransaction)
                .orElseThrow(() -> new TransactionDoesNotExistException(null, referenceAttempt + ""));
    }

    @Transactional
    public Transaction create(String ownerIdentifier, TransactionDTO dto) {
        Owner owner = ownerService.get(ownerIdentifier);
        Transaction transaction = mapper.convert(dto, owner, TransactionStatus.INITIATED);
        transaction.setStatus(TransactionStatus.INITIATED);
        return repository.save(transaction);
    }

    @Transactional
    public Redirect settlement(String ownerIdentifier, String referenceId) {
        Transaction transaction = getActive(ownerIdentifier, referenceId);
        if (transaction.getIpg() == null) {
            transaction.setIpg(transaction.getOwner().getSelfConfig().getSuitableIPG());
        }
        Attempt attempt = attempt(transaction);
        AttemptSettlement attemptSettlement = settlement(attempt);
        Redirect redirect;
        if (attemptSettlement.getStatus().isFinal()) {
            setAttemptFinalStatus(attempt, attemptSettlement.getStatus().convertToAttemptStatus().orElseThrow());
            handleFirstWebhookCall(attempt);
            saveAttempt(attempt);
            redirect = getAttemptRedirect(attempt);
        } else {
            attempt.setStatus(AttemptStatus.VERIFYING);
            redirect = new Redirect(attemptSettlement.getUrl());
        }
        saveAttempt(attempt);
        return redirect;
    }

    private AttemptSettlement settlement(Attempt attempt) {
        AttemptSettlement attemptSettlement = ipgFactory.find(attempt.getTransaction().getIpg())
                .settlementSafe(attempt.getTransaction().getOwner(), attempt.getReference(), attempt.getTransaction().getAmount(), attempt.getAdditionalInfo());

        attempt.setDescription(attemptSettlement.getDescription());

        if (attemptSettlement.getStatus().isFinal()) {
            setAttemptFinalStatus(attempt, attemptSettlement.getStatus().convertToAttemptStatus().orElseThrow());
        } else if (attemptSettlement.getStatus() == AttemptSettlementStatus.REDIRECTABLE) {
            attempt.setStatus(AttemptStatus.REDIRECTED);
        }

        saveAttempt(attempt);
        return attemptSettlement;
    }

    @Transactional
    public Redirect callBack(IPG ipg, Object body) {
        IpgService ipgService = ipgFactory.find(ipg);

        Long referenceId = ipgService.referenceFromCallback(body);
        Transaction transaction = getByReferenceAttempt(referenceId);

        return findActiveAttempt(transaction.getId())
                .map(attempt -> processActiveAttempt(attempt, body))
                .orElseGet(() -> getProcessedAttemptRedirect(referenceId));
    }

    private Redirect getProcessedAttemptRedirect(Long referenceId) {
        return attemptRepository.findByReference(referenceId)
                .map(this::getAttemptRedirect)
                .orElseThrow();
    }

    private Redirect processActiveAttempt(Attempt attempt, Object body) {
        AttemptCallback attemptCallback = ipgFactory.find(attempt.getTransaction().getIpg())
                .callbackSafe(body, attempt.getAdditionalInfo());
        attempt.setDescription(attemptCallback.getDescription());
        if (attemptCallback.getStatus().isFinal()) {
            setAttemptFinalStatus(attempt, attemptCallback.getStatus().convertToAttemptStatus().orElseThrow());
            handleFirstWebhookCall(attempt);
        } else if (attemptCallback.getStatus() == AttemptCallbackStatus.VERIFIABLE) {
            setAttemptFinalStatus(attempt, AttemptStatus.VERIFYING);
            verify(attempt);
        }
        attempt = saveAttempt(attempt);
        return getAttemptRedirect(attempt);
    }

    private void handleFirstWebhookCall(Attempt attempt) {
        if (attempt.getNotifyStatus() != AttemptNotifyStatus.NOTIFYING) {
            return;
        }
        OwnerSelfConfig ownerSelfConfig = attempt.getTransaction().getOwner().getSelfConfig();
        if (ownerSelfConfig.isWebhookCallAfterCallback()) {
            notify(attempt);
        }
    }

    private void verify(Attempt attempt) {
        IpgService ipgService = ipgFactory.find(attempt.getTransaction().getIpg());
        AttemptVerify verify = ipgService.verifySafe(attempt.getTransaction().getOwner(), attempt.getReference(), attempt.getAdditionalInfo());
        if (verify != null) {
            if (verify.getStatus().isFinal()) {
                setAttemptFinalStatus(attempt, verify.getStatus().convertToAttemptStatus().orElseThrow());
            } else if (verify.getStatus() == AttemptVerifyStatus.VERIFYING) {
                attempt.setNotifyStatus(AttemptNotifyStatus.CURRENTLY_NO_NEED_TO_NOTIFY);
            }
        }
    }


    private void setAttemptFinalStatus(Attempt attempt, AttemptStatus attemptStatus) {
        if (attempt.getStatus() != null && attempt.getStatus() != attemptStatus) {
            attempt.setStatus(attemptStatus);
            attempt.getStatus().convertToTransactionStatus().ifPresent(e -> attempt.getTransaction().setStatus(e));
            attempt.setNotifyStatus(getNotifiableStatus(attempt));
        }
    }

    private AttemptNotifyStatus getNotifiableStatus(Attempt attempt) {
        OwnerSelfConfig ownerSelfConfig = attempt.getTransaction().getOwner().getSelfConfig();
        if (ownerSelfConfig.getWebhookUrl() != null) {
            return AttemptNotifyStatus.NOTIFYING;
        } else {
            return AttemptNotifyStatus.NOT_NOTIFIABLE;
        }
    }


    private Attempt saveAttempt(Attempt attempt) {
        repository.save(attempt.getTransaction());
        return attemptRepository.save(attempt);
    }

    protected Attempt attempt(Transaction transaction) {
        if (transaction.getStatus().isFinal() || transaction.getStatus() == TransactionStatus.ATTEMPTED) {
            throw new ActiveAttemptIsAlreadyExists(transaction.getReference());
        }

        transaction.setStatus(TransactionStatus.ATTEMPTED);
        Attempt attempt = new Attempt();
        attempt.setReference(Math.abs(UUID.randomUUID().getLeastSignificantBits()));
        attempt.setTransaction(transaction);
        attempt.setStatus(AttemptStatus.INITIATED);
        attempt = attemptRepository.save(attempt);
        repository.save(transaction);
        return attempt;
    }

    private Redirect getAttemptRedirect(Attempt attempt) {
        OwnerSelfConfig ownerSelfConfig = attempt.getTransaction().getOwner().getSelfConfig();
        return new Redirect(ownerSelfConfig.getCallbackUrl())
                .putForm("sign", sign(attempt, ownerSelfConfig.getSignKey()))
                .putForm("reference", attempt.getTransaction().getReference())
                .putForm("description", attempt.getDescription())
                .putForm("status", attempt.getStatus())
                .putData("callWithData", !ownerSelfConfig.isWebhookCallAfterCallback());
    }

    private String sign(Attempt attempt, String key) {
        String data = String.format("%s/%s", attempt.getTransaction().getReference(), attempt.getStatus());
        SingableData singableData = SingableData.Impl.builder().data(data).build();
        singableData.sign(key);
        return singableData.getSignData();
    }

    @Transactional(readOnly = true)
    public Optional<Attempt> findActiveAttempt(BigInteger transactionId) {
        return attemptRepository.findActiveAttempt(transactionId);
    }

    @Transactional
    public void makeVerifying(Attempt attempt) {
        attempt.setStatus(AttemptStatus.VERIFYING);
        attemptRepository.save(attempt);
    }

    @Transactional
    public void verifyingStaged() {
        Page<Attempt> attempts;
        int page = 0;
        Instant instant = Instant.now().minusMillis(config.getMinimumTimeWaitForMakingVerifyStaged().toMillis());
        do {
            Pageable pageable = PageRequest.of(page++, 20, Sort.by(Sort.Direction.ASC, "updatedDate"));
            attempts = attemptRepository.findAbandonedAttempts(instant, pageable);
            attempts.forEach(this::makeVerifying);
        } while (!attempts.isLast());
    }

    @Transactional
    public void verifyingAttempt() {
        Page<Attempt> attempts;
        int page = 0;
        do {
            Pageable pageable = PageRequest.of(page++, 20, Sort.by(Sort.Direction.ASC, "updatedDate"));
            attempts = attemptRepository.findActiveAttempts(pageable);
            attempts.forEach(this::verify);
        } while (!attempts.isLast());
    }

    @Transactional
    public void notifyingAttempts() {
        Page<Attempt> attempts;
        int page = 0;
        do {
            Pageable pageable = PageRequest.of(page++, 20, Sort.by(Sort.Direction.ASC, "updatedDate"));
            attempts = attemptRepository.findNotifiableAttempts(pageable);
            attempts.forEach(this::notify);
        } while (!attempts.isLast());
    }

    private void notify(Attempt attempt) {
        boolean success = callWebhook(attempt);
        if (success) {
            attempt.setNotifyStatus(AttemptNotifyStatus.NOTIFIED);
            attemptRepository.save(attempt);
        }
    }

    private boolean callWebhook(Attempt attempt) {
        if (attempt.getTransaction().getOwner().getSelfConfig().getWebhookUrl() == null) {
            return false;
        }
        var ownerSelfConfig = attempt.getTransaction().getOwner().getSelfConfig();
        var attemptWebhook = AttemptWebhook.builder()
                .description(attempt.getDescription())
                .reference(attempt.getTransaction().getReference())
                .status(attempt.getStatus())
                .sign(sign(attempt, ownerSelfConfig.getSignKey()))
                .build();

        try {
            apiClientBean.apiClient(WebhookFeign.class, attempt.getTransaction().getOwner().getSelfConfig().getWebhookUrl())
                    .call(attemptWebhook);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Transaction map(TransactionDTO transactionDTO) {
        return mapper.convert(transactionDTO);
    }


    public TransactionDTO map(Transaction transactionDTO) {
        return mapper.convert(transactionDTO);
    }

    @Transactional
    public Transaction cancel(String ownerIdentifier, String transactionReference) {
        Transaction transaction = get(ownerIdentifier, transactionReference);
        if (transaction.getStatus().isFinal() || transaction.getStatus() == TransactionStatus.ATTEMPTED) {
            throw new TransactionIsFinalizedCurrentlyException();
        }
        transaction.setStatus(TransactionStatus.CANCELED);
        return repository.save(transaction);
    }
}

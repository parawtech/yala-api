package tech.rket.payment.service.owner;

import tech.rket.payment.domain.owner.Owner;
import tech.rket.payment.domain.owner.OwnerRepository;
import tech.rket.payment.infrastructure.dto.owner.OwnerDTO;
import tech.rket.payment.infrastructure.error.OwnerDoesNotExistException;
import tech.rket.payment.infrastructure.mapper.OwnerMapper;
import tech.rket.shared.infrastructure.model.domain.CrudService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OwnerService extends CrudService<Owner, OwnerDTO, OwnerRepository> {
    @Getter
    private final OwnerRepository repository;
    private final OwnerMapper ownerMapper;

    @Transactional(readOnly = true)
    public Owner get(String identifier) {
        return repository.findByIdentifier(identifier).orElseThrow(() -> new OwnerDoesNotExistException(identifier));
    }

    @Override
    public OwnerDTO map(Owner d) {
        return ownerMapper.convert(d);
    }

    @Override
    public Owner map(OwnerDTO owner) {
        return ownerMapper.convert(owner);
    }
}

package tech.rket.payment.infrastructure.mapper;

import tech.rket.payment.domain.owner.Owner;
import tech.rket.payment.domain.transaction.Transaction;
import tech.rket.payment.domain.transaction.TransactionStatus;
import tech.rket.payment.infrastructure.dto.transaction.TransactionDTO;
import tech.rket.shared.infrastructure.mapstruct.MapstructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapstructConfig.class)
public interface TransactionMapper {
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "status", target = "status")
    Transaction convert(TransactionDTO transactionDTO, Owner owner, TransactionStatus status);

    @Mapping(ignore = true, target = "owner")
    Transaction convert(TransactionDTO transactionDT);

    TransactionDTO convert(Transaction transactionDTO);
}

package tech.rket.shared.contract.storage.command;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import lombok.Data;
import tech.rket.shared.contract.storage.enums.ContractStoredFileAuthType;
import tech.rket.shared.contract.storage.enums.ContractStoredFileStatus;
import tech.rket.shared.infrastructure.restfilter.filter.Filter;
import tech.rket.shared.infrastructure.restfilter.filter.LongFilter;
import tech.rket.shared.infrastructure.restfilter.filter.StringFilter;

@Data
public class ContractStoredFileSearchCriteria {
    @Null
    private LongFilter tenant;
    @Nullable
    private StringFilter characteristic;
    @Nullable
    private StringFilter key;
    @Nullable
    private Filter<ContractStoredFileStatus> status;
    @Nullable
    private LongFilter user;
    @Nullable
    private LongFilter parent;
    @Nullable
    private StringFilter variantKey;
    @Nullable
    private Filter<ContractStoredFileAuthType> authType;
    @Null
    private StringFilter authValues;
}

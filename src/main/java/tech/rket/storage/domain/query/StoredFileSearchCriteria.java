package tech.rket.storage.domain.query;

import tech.rket.shared.infrastructure.restfilter.filter.LongFilter;
import tech.rket.shared.infrastructure.restfilter.filter.StringFilter;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
public class StoredFileSearchCriteria {
    @Null
    private LongFilter tenant;
    @Nullable
    private StringFilter characteristic;
    @Nullable
    private StringFilter key;
    @Nullable
    private StoredFileSearchStatusFilter status;
    @Nullable
    private LongFilter user;
    @Nullable
    private LongFilter parent;
    @Nullable
    private StringFilter variantKey;
    @Nullable
    private StoredFileSearchAuthTypeFilter authType;
    @Null
    private StringFilter authValues;
}

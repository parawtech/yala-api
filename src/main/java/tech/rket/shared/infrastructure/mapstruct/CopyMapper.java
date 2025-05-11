package tech.rket.shared.infrastructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.control.DeepClone;

public interface CopyMapper<A> {
    @BeanMapping(mappingControl = DeepClone.class, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    A copy(A instance);
}

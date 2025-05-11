package tech.rket.shared.infrastructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.control.DeepClone;

public interface EntityCopyMapper<A> extends CopyMapper<A> {
    @BeanMapping(mappingControl = DeepClone.class, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "id", expression = "java(null)")
    A entityCopy(A instance);
}

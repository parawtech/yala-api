package tech.rket.shared.infrastructure.object_mapper;

import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;

class PatchedValueInstantiator extends StdValueInstantiator {
    protected PatchedValueInstantiator(
            StdValueInstantiator src, SettableBeanProperty[] constructorArguments) {
        super(src);
        _constructorArguments = constructorArguments;
    }
}

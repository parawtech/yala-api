package tech.rket.shared.infrastructure.model.filter;


import tech.rket.shared.infrastructure.restfilter.filter.RangeFilter;

import java.math.BigInteger;

public class BigIntegerFilter extends RangeFilter<BigInteger> {
    @Override
    public Class<BigInteger> obtainGenericClass() throws IllegalStateException, ClassCastException {
        return BigInteger.class;
    }
}
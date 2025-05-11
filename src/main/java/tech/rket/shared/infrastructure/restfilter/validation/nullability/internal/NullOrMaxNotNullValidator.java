/*
 * MIT License
 *
 * Copyright (c) 2018-2020 Yoann CAPLAIN
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package tech.rket.shared.infrastructure.restfilter.validation.nullability.internal;

import tech.rket.shared.infrastructure.restfilter.validation.nullability.NullOrMaxNotNull;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * <p>Created on 2019/12/22.</p>
 *
 * @author Yoann CAPLAIN
 */
public class NullOrMaxNotNullValidator implements ConstraintValidator<NullOrMaxNotNull, Object> {

    private String[] fieldNames = new String[]{};
    private int maxNotNull = 0;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(NullOrMaxNotNullValidator.class);

    @Override
    public void initialize(NullOrMaxNotNull constraintAnnotation) {
        fieldNames = constraintAnnotation.value();
        if (constraintAnnotation.maxNotNull() < 1) {
            throw new IllegalArgumentException("MaxNotNull cannot be less than 1");
        }
        maxNotNull = constraintAnnotation.maxNotNull();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (fieldNames.length < 2) {
            throw new IllegalArgumentException("Require at least 2 field names passed");
        }
        int countNotNull = 0;
        for (String fieldName : fieldNames) {
            java.lang.reflect.Field field = ReflectionUtils.findField(value.getClass(), fieldName);
            if (field == null) {
                throw new IllegalArgumentException("Could not find field: " + fieldName);
            }
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(value);
                if (fieldValue != null) {
                    countNotNull++;
                    if (countNotNull > maxNotNull) return false;
                }
            } catch (IllegalAccessException e) {
                log.error("Failed to get field", e);
                throw new IllegalStateException(e);
            }
        }

        return true;
    }
}

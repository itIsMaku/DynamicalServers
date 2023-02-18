package cz.maku.dynamical;

import cz.maku.mommons.ef.converter.TypeConverter;

public class BooleanConverter implements TypeConverter<Boolean, Integer> {

    @Override
    public Integer convertToColumn(Boolean value) {
        return value ? 1 : 0;
    }

    @Override
    public Boolean convertToEntityField(Integer value) {
        return value == 1;
    }
}

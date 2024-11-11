package dido.data.generic;

import dido.data.DataFactory;
import dido.data.FieldSetter;

public interface GenericDataFactory<F> extends DataFactory {

    @Override
    GenericWriteSchema<F> getSchema();

    @Deprecated
    default FieldSetter getSetter(F field) {
        return getSchema().getFieldSetter(field);
    }

    @Override
    GenericWritableData<F> getWritableData();

    @Override
    GenericData<F> toData();
}

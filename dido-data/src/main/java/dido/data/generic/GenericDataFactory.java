package dido.data.generic;

import dido.data.DataFactory;
import dido.data.FieldSetter;

public interface GenericDataFactory<F, D extends GenericData<F>> extends DataFactory<D> {

    @Override
    GenericReadWriteSchema<F> getSchema();

    @Deprecated
    default FieldSetter getSetter(F field) {
        return getSchema().getFieldSetter(field);
    }

    @Override
    GenericWritableData<F> getSetter();
}

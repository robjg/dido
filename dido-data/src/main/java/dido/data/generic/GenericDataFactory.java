package dido.data.generic;

import dido.data.DataFactory;
import dido.data.FieldSetter;

public interface GenericDataFactory<F, D extends GenericData<F>> extends DataFactory<D> {

    FieldSetter getSetter(F field);
}

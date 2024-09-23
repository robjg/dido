package dido.data.generic;

import dido.data.DataFactory;
import dido.data.Setter;

public interface GenericDataFactory<F, D extends GenericData<F>> extends DataFactory<D> {

    Setter getSetter(F field);
}

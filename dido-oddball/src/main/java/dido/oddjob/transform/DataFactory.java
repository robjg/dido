package dido.oddjob.transform;

import dido.data.GenericData;

public interface DataFactory<F> {

    DataSetter<F> getSetter();

    GenericData<F> toData();
}

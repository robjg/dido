package dido.oddjob.transpose;

import dido.data.GenericData;

public interface DataFactory<F> {

    DataSetter<F> getSetter();

    GenericData<F> toData();
}

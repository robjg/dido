package dido.oddjob.transpose;

import dido.data.GenericData;

@FunctionalInterface
public interface Transposer<F, T> {

    void transpose(GenericData<F> from, DataSetter<T> into);
}

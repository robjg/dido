package dido.oddjob.transpose;

import dido.data.DataSchema;

public interface TransposerFactory<F, T> {

    Transposer<F, T> create(int position, DataSchema<F> fromSchema, SchemaSetter<T> schemaSetter);


}

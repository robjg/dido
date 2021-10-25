package dido.oddjob.transpose;

import dido.data.DataSchema;

public interface SetterProvider<F> {

    DataFactory<F> provideSetter(DataSchema<F> schema);

}

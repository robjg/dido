package dido.oddjob.transform;

import dido.data.DataSchema;

public interface SetterProvider {

    DataFactory provideSetter(DataSchema schema);

}

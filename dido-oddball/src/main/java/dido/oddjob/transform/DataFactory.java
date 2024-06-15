package dido.oddjob.transform;

import dido.data.DidoData;

public interface DataFactory {

    DataSetter getSetter();

    DidoData toData();
}

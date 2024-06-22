package dido.data;

import java.util.Map;

public interface DataFactory<D extends DidoData> {

    Class<D> getDataType();

    DataSetter getSetter();

    /**
     * Convenience function to create data from a map.
     *
     * @param map The map.
     * @return The data.
     */
    D mapToData(Map<? extends String, ?> map);

    D valuesToData(Object... values);

    D toData();

}

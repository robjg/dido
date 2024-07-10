package dido.data;

/**
 * Provides the ability to create Dido Data.
 *
 * @param <D> The type of Dido Data.
 */
public interface DataFactory<D extends DidoData> {

    Class<D> getDataType();

    Setter getSetterAt(int index);

    Setter getSetterNamed(String name);

    DataSetter getSetter();

    /**
     * Copy the same sort of data this is a factory of.

     * @param data The data.
     */
     void copy(D data);

    D valuesToData(Object... values);

    D toData();

}

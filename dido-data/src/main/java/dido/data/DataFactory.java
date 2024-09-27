package dido.data;

/**
 * Provides the ability to create Dido Data.
 *
 * @param <D> The type of Dido Data.
 */
public interface DataFactory<D extends DidoData> {

    WritableSchema<D> getSchema();

    Class<D> getDataType();

    Setter getSetterAt(int index);

    Setter getSetterNamed(String name);

    DataSetter getSetter();

    D toData();

}

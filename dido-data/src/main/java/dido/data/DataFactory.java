package dido.data;

/**
 * Provides the ability to create Dido Data.
 *
 * @param <D> The type of Dido Data.
 */
public interface DataFactory<D extends DidoData> {

    WritableSchema<D> getSchema();

    Class<D> getDataType();

    FieldSetter getSetterAt(int index);

    FieldSetter getSetterNamed(String name);

    WritableData getSetter();

    D toData();

}

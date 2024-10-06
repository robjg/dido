package dido.data;

/**
 * A {@link DataSchema} that is able to support a transformation of the {@link DidoData} this is a schema for.
 */
public interface WriteSchema extends DataSchema {

    FieldSetter getFieldSetterAt(int index);

    FieldSetter getFieldSetterNamed(String name);

}

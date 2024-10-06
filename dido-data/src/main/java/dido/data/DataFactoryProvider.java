package dido.data;

import java.lang.reflect.Type;

/**
 * Something that is able to provide a {@link DataFactory}.
 *
 * @param <D> The Type of DidoData being built.
 */
public interface DataFactoryProvider<D extends DidoData> {

    Type getDataType();

    WriteSchemaFactory getSchemaFactory();

    DataFactory<D> provideFactory(DataSchema schema);
}

package dido.data;

/**
 * Something that is able to provide a {@link DataFactory}.
 *
 * @param <D> The Type of DidoData being built.
 */
public interface DataFactoryProvider<D extends DidoData> {

    Class<D> getDataType();

    DataFactory<D> provideFactory(DataSchema schema);

}

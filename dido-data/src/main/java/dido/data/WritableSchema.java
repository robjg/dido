package dido.data;

/**
 * A {@link DataSchema} that is able to support a transformation of the {@link DidoData} this is a schema for.
 *
 * @param <D> The type of {@link DidoData} this is for.
 */
public interface WritableSchema<D extends DidoData> extends ReadableSchema {

    WritableSchemaFactory<D> newSchemaFactory();

    DataFactory<D> newDataFactory();
}

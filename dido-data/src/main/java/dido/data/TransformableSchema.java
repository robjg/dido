package dido.data;

/**
 * A {@link DataSchema} that is able to support a transformation of the {@link DidoData} this is a schema for.
 *
 * @param <D> The type of {@link DidoData} this is for.
 * @param <S> The type of {@link TransformableSchema} the then transformation will be, which is normally
 *           the schemas own type.
 */
public interface TransformableSchema<D extends DidoData, S extends TransformableSchema<D, S>> extends DataSchema {

    SchemaFactory<S> newSchemaFactory();

    SchemaFactory<S> newSchemaFactoryAsCopy();

    DataFactory<D> newDataFactory();
}

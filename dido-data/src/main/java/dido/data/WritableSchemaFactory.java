package dido.data;

public interface WritableSchemaFactory<D extends DidoData> extends SchemaFactory {

    @Override
    WritableSchema<D> toSchema();
}

package dido.data;

public interface WriteSchemaFactory extends SchemaFactory {

    @Override
    WriteSchema toSchema();
}

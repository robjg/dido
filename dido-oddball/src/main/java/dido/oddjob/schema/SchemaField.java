package dido.oddjob.schema;

public interface SchemaField {

    String getFieldName();

    int getIndex();

    Class<?> getType();

    SchemaWrapper getNested();
}

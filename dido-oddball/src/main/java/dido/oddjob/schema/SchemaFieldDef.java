package dido.oddjob.schema;

public interface SchemaFieldDef {

    String getFieldName();

    int getIndex();

    Class<?> getType();

    SchemaWrapper getNested();
}

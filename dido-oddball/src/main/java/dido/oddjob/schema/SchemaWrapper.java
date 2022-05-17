package dido.oddjob.schema;

import java.util.List;

public interface SchemaWrapper {

    String getSchemaName();

    String getSchemaRefName();

    List<SchemaFieldDef> getSchemaFields();
}

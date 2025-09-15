package dido.oddjob.schema;

import dido.data.schema.SchemaDefs;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.types.ValueFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * @oddjob.description Define a Schema Definitions that will be referenced
 * elsewhere.
 * The schemas defined here as {@link SchemaBean} values must have the name property set and
 * can then be reference elsewhere using the Ref property of a {@link SchemaFieldBean}.
 *
 */
public class SchemaDefsBean implements ValueFactory<SchemaDefs> {

    /**
     * @oddjob.description Nested Schemas.
     * @oddjob.required No.
     */
    private final List<NestedSchema> list = new LinkedList<>();

    @Override
    public SchemaDefs toValue() throws ArooaConversionException {
        SchemaDefs schemaDefs = SchemaDefs.newInstance();
        for (NestedSchema nestedSchema : list) {
            nestedSchema.setDefs(schemaDefs);
            nestedSchema.toSchema();
        }
        return schemaDefs;
    }

    public void setSchemas(int index, NestedSchema schema) {
        list.add(index, schema);
    }

    public NestedSchema getSchemas(int index) {
        return list.get(index);
    }
}

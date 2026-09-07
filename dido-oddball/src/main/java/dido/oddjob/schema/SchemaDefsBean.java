package dido.oddjob.schema;

import dido.data.schema.SchemaDefs;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @oddjob.description Define Schema Definitions that will be referenced
 * elsewhere.
 * The schemas defined here as {@link SchemaBean} values must have the name property set and
 * can then be reference elsewhere using the Ref property of a {@link SchemaFieldBean}.
 *
 * @oddjob.example Used to define a nested schema. Here we define a single Schema Definition
 * using the {@code defs} property of {@link SchemaBean}. Our definition is then
 * referenced elsewhere in the schema definition.
 * {@oddjob.xml.resource dido/oddjob/schema/NestedSchema.xml}
 */
public class SchemaDefsBean implements ValueFactory<SchemaDefs> {

    /**
     * @oddjob.description Nested Schemas. Generally defined as a {@link SchemaBean}.
     * @oddjob.required No, but pointless if missing
     */
    private final List<NestedSchema> list = new LinkedList<>();

    @Override
    public SchemaDefs toValue() throws ArooaConversionException {
        SchemaDefs schemaDefs = SchemaDefs.newInstance();
        for (NestedSchema nestedSchema : list) {
            nestedSchema.toSchema(schemaDefs);
        }
        return schemaDefs;
    }

    public void setSchemas(int index, NestedSchema schema) {
        new ListSetterHelper<>(this.list).set(index, schema);
    }

    public NestedSchema getSchemas(int index) {
        return list.get(index);
    }
}

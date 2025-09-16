package dido.oddjob.schema;

import dido.data.DataSchema;
import dido.data.SchemaFactory;
import dido.data.schema.DataSchemaFactory;
import dido.data.schema.SchemaDefs;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @oddjob.description Define a Schema.
 * @oddjob.example Define a simple schema.
 * {@oddjob.xml.resource dido/oddjob/schema/SimpleSchema.xml}
 * @oddjob.example Define a nested schema.
 * {@oddjob.xml.resource dido/oddjob/schema/NestedSchema.xml}
 * @oddjob.example Define a repeating schema.
 * {@oddjob.xml.resource dido/oddjob/schema/RepeatingSchema.xml}
 */
public class SchemaBean implements NestedSchema {

    /**
     * @oddjob.description The name of the schema. This is so this schema may be
     * referenced elsewhere in the definition. If set then SchemaDefs must also be set, either directly
     * or because this is nested within another Schema.
     *
     * @oddjob.required No.
     */
    private String name;

    /**
     * @oddjob.description The fields. These are generally defined with {@link SchemaFieldBean}s.
     * @oddjob.required No.
     */
    private final List<SchemaFactoryConsumer> of = new LinkedList<>();

    /**
     * @oddjob.description Schema definitions for nested schema references. These are generally defined with {@link SchemaDefsBean}s.
     * These will be set automatically if this is a nested schema.
     * @oddjob.required No.
     */
    private SchemaDefs defs;

    public static class Conversions implements ConversionProvider {

        @Override
        public void registerWith(ConversionRegistry registry) {
            registry.register(SchemaBean.class, DataSchema.class,
                    SchemaBean::toSchema);
        }
    }

    public DataSchema toSchema() throws ArooaConversionException {
        return toSchema(defs);
    }

    @Override
    public DataSchema toSchema(SchemaDefs defs) throws ArooaConversionException {

        SchemaFactory schemaFactory = DataSchemaFactory.newInstance();
        schemaFactory.setSchemaName(name);
        schemaFactory.setSchemaDefs(defs);

        for (SchemaFactoryConsumer field : of) {
            field.acceptSchemaFactory(schemaFactory);
        }

        return schemaFactory.toSchema();
    }

    public void setDefs(SchemaDefs defs) {
        this.defs = defs;
    }

    public SchemaDefs getDefs() {
        return this.defs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SchemaFactoryConsumer getOf(int index) {
        return of.get(index);
    }

    public void setOf(int index, SchemaFactoryConsumer of) {
        new ListSetterHelper<>(this.of).set(index, of);
    }

    @Override
    public String toString() {
        return "SchemaBean{" +
                "name='" + name + '\'' +
                ", of=" + of +
                '}';
    }
}

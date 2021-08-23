package dido.oddjob.schema;

import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import dido.data.SchemaManager;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SchemaBean implements ArooaValue {

    private String name;

    private String named;

    private List<SchemaField> of = new LinkedList<>();

    private List<SchemaWrapper> list = new LinkedList<>();

    public static class Conversions implements ConversionProvider {

        @Override
        public void registerWith(ConversionRegistry registry) {
            registry.register(SchemaBean.class, DataSchema.class,
                    from -> from.toSchema());

            registry.register(SchemaBean.class, SchemaWrapper.class,
                    from -> from.toSchemaWrapper());
        }

    }

    DataSchema<String> toSchema() throws ArooaConversionException {

        if (list.isEmpty()) {
            SchemaBuilder<String> builder = SchemaBuilder.forStringFields();
            for (SchemaField field : of) {
                builder.addIndexedField(field.getIndex(), field.getFieldName(), field.getType());
            }
            return builder.build();
        } else {
            if (!of.isEmpty()) {
                throw new ArooaConversionException("Can't have both 'list' and 'of' set.");
            }
            SchemaManager schemaManager = new SchemaManager();
            for (SchemaWrapper wrapper : list) {
                if (wrapper.getSchemaRefName() != null) {
                    throw new ArooaConversionException("Schema Reference is only for nested schemas.");
                }
                SchemaManager.NewTopLevelSchema<String> builder =
                        schemaManager.newSchema(wrapper.getSchemaName(), String.class);
                addNested(builder, wrapper);
            }

            DataSchema<?> schema = Optional.ofNullable(name)
                    .map(n -> schemaManager.getSchema(n))
                    .orElseGet(() -> schemaManager.getDefaultSchema());

            if (schema == null) {
                throw new ArooaConversionException("No such schema.");
            }
            else {
                return (DataSchema<String>) schema;
            }
        }
    }

    <B extends SchemaManager.NewSchema<String, B>> void addNested(SchemaManager.NewSchema<String, B> builder,
                                                                  SchemaWrapper wrapper ) {
        for (SchemaField field : wrapper.getSchemaFields()) {
            int index = field.getIndex();
            String fieldName = field.getFieldName();

            SchemaWrapper nested = field.getNested();

            if (nested == null) {
                builder.addIndexedField(index, fieldName, field.getType());
            }
            else {
                String ref = nested.getSchemaRefName();
                if (ref == null) {
                    SchemaManager.NewSchema<String, ? extends SchemaManager.NewSchema<String, ?>> next =
                            builder.addNestedIndexedField(index, fieldName, String.class);

                    addNested(next, nested);
                }
                else {
                    builder.addNestedIndexedField(field.getIndex(), field.getFieldName(), ref);
                }
            }
        }
        builder.add();
    }

    SchemaWrapper toSchemaWrapper() throws ArooaConversionException {

        if (!list.isEmpty()) {
            throw new ArooaConversionException("Schema " + this +
                    " is being used in a list but contains a list, we can't handle this at the moment.");
        }

        return new SchemaWrapperImpl(name, named, of);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamed() {
        return named;
    }

    public void setNamed(String named) {
        this.named = named;
    }

    public SchemaField getOf(int index) {
        return of.get(index);
    }

    public void setOf(int index, SchemaField of) {
        new ListSetterHelper<>(this.of).set(index, of);
    }

    public SchemaWrapper getList(int index) {
        return list.get(index);
    }

    public void setList(int index, SchemaWrapper wrapper) {
        new ListSetterHelper<>(this.list).set(index, wrapper);
    }

    static class SchemaWrapperImpl implements SchemaWrapper {

        private final String schemaName;

        private final String schemaRefName;

        private final List<SchemaField> schemaFields;

        SchemaWrapperImpl(String schemaName, String schemaRefName, List<SchemaField> schemaFields) {
            this.schemaName = schemaName;
            this.schemaRefName = schemaRefName;
            this.schemaFields = new ArrayList<>(schemaFields);
        }

        @Override
        public String getSchemaName() {
            return this.schemaName;
        }

        @Override
        public String getSchemaRefName() {
            return this.schemaRefName;
        }

        @Override
        public List<SchemaField> getSchemaFields() {
            return schemaFields;
        }

        @Override
        public String toString() {
            return "SchemaWrapperImpl{" +
                    "schemaName='" + schemaName + '\'' +
                    ", schemaRefName='" + schemaRefName + '\'' +
                    ", schemaFields=" + schemaFields +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SchemaBean{" +
                "name='" + name + '\'' +
                ", named='" + named + '\'' +
                ", of=" + of +
                ", list=" + list +
                '}';
    }
}

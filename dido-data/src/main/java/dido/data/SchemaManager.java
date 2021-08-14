package dido.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Manage schemas by name so that nested schemas can easily be constructed.
 */
public class SchemaManager {

    public static final String DEFAULT_SCHEMA_NAME = "default";

    private final Map<String, DataSchema<?>> schemaMap = new HashMap<>();

    public static SchemaManager newInstance() {
        return new SchemaManager();
    }

    public <F> NewSchema<F> newSchema(String name, Class<F> fieldType) {
        return new NewSchema<>(name, fieldType);
    }

    public <F> NewSchema<F> newDefaultSchema(Class<F> fieldType) {
        return new NewSchema<>(DEFAULT_SCHEMA_NAME, fieldType);
    }

    public class NewSchema<F> {

        private final String name;

        private final SchemaBuilder<F> schemaBuilder;

        public NewSchema(String name, Class<F> fieldType) {
            this.name = name;
            this.schemaBuilder = SchemaBuilder.forFieldType(fieldType);
        }

        public NewSchema<F> addField(F field, Class<?> type) {
            this.schemaBuilder.addField(field, type);
            return this;
        }

        public <N> NewSchema<F> addNestedField(F field, String schemaName) {
            DataSchema<N> nestedSchema = (DataSchema<N>)  SchemaManager.this.schemaMap.get(field);
            return addNestedField(field, nestedSchema);
        }

        public <N> NewSchema<F> addNestedField(F field, DataSchema<N> nestedSchema) {
            this.schemaBuilder.addNestedField(field, nestedSchema);
            return this;
        }

        public <N> NewSchema<F> addNestedRepeatingField(F field, String schemaName) {
            DataSchema<N> nestedSchema = (DataSchema<N>)  SchemaManager.this.schemaMap.get(field);
            this.schemaBuilder.addNestedRepeatingField(field, nestedSchema);
            return this;
        }

        public SchemaManager add() {
            SchemaManager.this.schemaMap.put(this.name, this.schemaBuilder.build());
            return SchemaManager.this;
        }
    }
}

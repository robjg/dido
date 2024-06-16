package dido.data;

import java.util.*;

/**
 * Manage schemas by name so that nested schemas can easily be constructed.
 */
public class SchemaManager {

    public static final String DEFAULT_SCHEMA_NAME = "default";

    private final Map<String, DataSchema> schemaMap = new HashMap<>();

    private final Map<String, List<SchemaReference>> schemaRefs = new HashMap<>();

    public static SchemaManager newInstance() {
        return new SchemaManager();
    }

    public NewTopLevelSchema newSchema(String name) {
        return new NewTopLevelSchema(name);
    }

    public NewTopLevelSchema newDefaultSchema() {
        return new NewTopLevelSchema(null);
    }

    public abstract class NewSchema<B extends NewSchema<B>> {

        protected final SchemaBuilder schemaBuilder;

        public NewSchema() {
            this.schemaBuilder = SchemaBuilder.newInstance();
        }

        //

        public B addAt(int index, Class<?> type) {
            return addFieldAt(index, null, type);
        }

        public B addField(String field, Class<?> type) {
            return addFieldAt(0, field, type);
        }

        public B addFieldAt(int index, String field, Class<?> type) {
            this.schemaBuilder.addFieldAt(index, field, type);
            return self();
        }

        // Nested Schemas from references.

        public B addNestedAt(int index, String schemaName) {
            return addNestedFieldAt(index, null, schemaName);
        }

        public B addNestedField(String field, String schemaName) {
            return addNestedFieldAt(0, field, schemaName);
        }

        public B addNestedFieldAt(int index, String field, String schemaName) {

            DataSchema schema = schemaMap.get(schemaName);
            if (schema == null) {
                SchemaReference schemaRef = SchemaReference.named(schemaName);
                schemaRefs.computeIfAbsent(schemaName, k -> new ArrayList<>())
                        .add(schemaRef);
                schemaBuilder.addNestedFieldAt(index, field, schemaRef);
            }
            else {
                schemaBuilder.addNestedFieldAt(index, field, schema);
            }
            return self();
        }

        // Nested Schemas from previously defined schemas.

        public  B addNestedAt(int index, DataSchema nestedSchema) {
            return this.addNestedFieldAt(index,null , nestedSchema);
        }

        public  B addNestedField(String field, DataSchema nestedSchema) {
            return this.addNestedFieldAt(0, field, nestedSchema);
        }

        public  B addNestedFieldAt(int index, String field, DataSchema nestedSchema) {
            this.schemaBuilder.addNestedFieldAt(index, field, nestedSchema);
            return self();
        }

        // Nested Schemas from new inline schema definitions.

        public  NewNestedSchema<B> addNestedField(String field) {

            return addNestedIndexedField(0, field);
        }

        public  NewNestedSchema<B> addNestedIndex(int index) {

            return addNestedIndexedField(index, null);
        }

        public  NewNestedSchema<B> addNestedIndexedField(int index, String field) {

            return new NewNestedSchema<>(this, index, field, false);
        }

        // Repeating Nested Schema from references.

        public  B addRepeatingAt(int index, String schemaName) {
            return this.addRepeatingFieldAt(index,null , schemaName);
        }

        public  B addRepeatingField(String field, String schemaName) {
            return this.addRepeatingFieldAt(0, field, schemaName);
        }

        public  B addRepeatingFieldAt(int index, String field, String schemaName) {

            DataSchema schema = schemaMap.get(schemaName);
            if (schema == null) {
                SchemaReference schemaRef = SchemaReference.named(schemaName);
                schemaRefs.computeIfAbsent(schemaName, k -> new ArrayList<>())
                        .add(schemaRef);
                schemaBuilder.addRepeatingFieldAt(index, field, schemaRef);
            }
            else {
                schemaBuilder.addRepeatingFieldAt(index, field, schema);
            }
            return self();
        }

        // Repeating Nested schemas from previously defined schemas.

        public  B addRepeatingAt(int index, DataSchema nestedSchema) {
            return this.addRepeatingFieldAt(index,null , nestedSchema);
        }

        public  B addRepeatingField(String field, DataSchema nestedSchema) {
            return this.addRepeatingFieldAt(0, field, nestedSchema);
        }

        public  B addRepeatingFieldAt(int index, String field, DataSchema nestedSchema) {
            schemaBuilder.addRepeatingFieldAt(index, field, nestedSchema);
            return self();
        }

        // Repeating Nested Schemas from new inline schema definitions.

        public  NewNestedSchema<B> addRepeatingField(String field) {

            return addRepeatingIndexedField(0, field);
        }

        public  NewNestedSchema<B> addRepeatingIndex(int index) {

            return addRepeatingIndexedField(index, null);
        }

        public  NewNestedSchema<B> addRepeatingIndexedField(int index, String field) {

            return new NewNestedSchema<>( this, index, field, true);
        }

        protected abstract B self();

        abstract public void add();
    }

    public class NewTopLevelSchema extends NewSchema<NewTopLevelSchema> {

        private final String name;

        public NewTopLevelSchema(String name) {
            this.name = Optional.ofNullable(name).orElse(DEFAULT_SCHEMA_NAME);
        }

        public SchemaManager addToManager() {
            add();
            return SchemaManager.this;
        }

        @Override
        protected NewTopLevelSchema self() {
            return this;
        }

        @Override
        public void add() {
            DataSchema schema = this.schemaBuilder.build();
            List<SchemaReference> schemaReferences =
                    SchemaManager.this.schemaRefs.remove(name);
            if (schemaReferences != null) {
                schemaReferences.forEach(ref -> ref.set(schema));
            }
            SchemaManager.this.schemaMap.put(name, schema);
        }
    }


    public class NewNestedSchema<B extends NewSchema<B>> extends NewSchema<NewNestedSchema<B>> {

        private final NewSchema<B> parentSchema;

        private final int index;

        private final String field;

        private final boolean repeating;

        public NewNestedSchema(NewSchema<B> parentSchema,
                               int index,
                               String field,
                               boolean repeating) {
            this.parentSchema = parentSchema;
            this.index = index;
            this.field = field;
            this.repeating = repeating;
        }

        public NewSchema<B> addNested() {
            if (parentSchema instanceof NewTopLevelSchema) {
                throw new UnsupportedOperationException("Use addBack()");
            }
            else {
                add();
                return parentSchema;
            }
        }

        public NewTopLevelSchema addBack() {
            if (parentSchema instanceof NewTopLevelSchema) {
                add();
                return (NewTopLevelSchema) parentSchema;
            }
            else {
                throw new UnsupportedOperationException("Use addNested");
            }
        }

        public void add() {
            if (repeating) {
                this.parentSchema.schemaBuilder.addRepeatingFieldAt(
                        index, field, this.schemaBuilder.build());
            }
            else {
                this.parentSchema.schemaBuilder.addNestedFieldAt(
                        index, field, this.schemaBuilder.build());
            }
        }

        @Override
        protected NewNestedSchema<B> self() {
            return this;
        }
    }

    public  DataSchema getDefaultSchema() {
        //noinspection unchecked
        return (DataSchema) schemaMap.get(DEFAULT_SCHEMA_NAME);
    }

    public  DataSchema getSchema(String schemaName) {
        //noinspection unchecked
        return (DataSchema) schemaMap.get(schemaName);
    }
}

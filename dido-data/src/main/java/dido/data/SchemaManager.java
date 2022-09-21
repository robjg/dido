package dido.data;

import java.util.*;

/**
 * Manage schemas by name so that nested schemas can easily be constructed.
 */
public class SchemaManager {

    public static final String DEFAULT_SCHEMA_NAME = "default";

    private final Map<String, DataSchema<?>> schemaMap = new HashMap<>();

    private final Map<String, List<SchemaReference<?>>> schemaRefs = new HashMap<>();

    public static SchemaManager newInstance() {
        return new SchemaManager();
    }

    public <F> NewTopLevelSchema<F> newSchema(String name, Class<F> fieldType) {
        return new NewTopLevelSchema<>(fieldType, name);
    }

    public <F> NewTopLevelSchema<F> newDefaultSchema(Class<F> fieldType) {
        return new NewTopLevelSchema<>(fieldType, null);
    }

    public abstract class NewSchema<F, B extends NewSchema<F, B>> {

        protected final SchemaBuilder<F> schemaBuilder;

        public NewSchema(Class<F> fieldType) {
            this.schemaBuilder = SchemaBuilder.forFieldType(fieldType);
        }

        //

        public B addAt(int index, Class<?> type) {
            return addFieldAt(index, null, type);
        }

        public B addField(F field, Class<?> type) {
            return addFieldAt(0, field, type);
        }

        public B addFieldAt(int index, F field, Class<?> type) {
            this.schemaBuilder.addFieldAt(index, field, type);
            return self();
        }

        // Nested Schemas from references.

        public B addNestedAt(int index, String schemaName) {
            return addNestedFieldAt(index, null, schemaName);
        }

        public B addNestedField(F field, String schemaName) {
            return addNestedFieldAt(0, field, schemaName);
        }

        public <N> B addNestedFieldAt(int index, F field, String schemaName) {

            @SuppressWarnings("unchecked")
            DataSchema<N> schema = (DataSchema<N>) schemaMap.get(schemaName);
            if (schema == null) {
                SchemaReference<N> schemaRef = SchemaReference.named(schemaName);
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

        public <N> B addNestedAt(int index, DataSchema<N> nestedSchema) {
            return this.addNestedFieldAt(index,null , nestedSchema);
        }

        public <N> B addNestedField(F field, DataSchema<N> nestedSchema) {
            return this.addNestedFieldAt(0, field, nestedSchema);
        }

        public <N> B addNestedFieldAt(int index, F field, DataSchema<N> nestedSchema) {
            this.schemaBuilder.addNestedFieldAt(index, field, nestedSchema);
            return self();
        }

        // Nested Schemas from new inline schema definitions.

        public <N> NewNestedSchema<F, B, N> addNestedField(F field, Class<N> fieldType) {

            return addNestedIndexedField(0, field, fieldType);
        }

        public <N> NewNestedSchema<F, B, N> addNestedIndex(int index, Class<N> fieldType) {

            return addNestedIndexedField(index, null, fieldType);
        }

        public <N> NewNestedSchema<F, B, N> addNestedIndexedField(int index, F field, Class<N> fieldType) {

            return new NewNestedSchema<>(fieldType, this, index, field, false);
        }

        // Repeating Nested Schema from references.

        public <N> B addRepeatingAt(int index, String schemaName) {
            return this.addRepeatingFieldAt(index,null , schemaName);
        }

        public <N> B addRepeatingField(F field, String schemaName) {
            return this.addRepeatingFieldAt(0, field, schemaName);
        }

        public <N> B addRepeatingFieldAt(int index, F field, String schemaName) {

            @SuppressWarnings("unchecked")
            DataSchema<N> schema = (DataSchema<N>) schemaMap.get(schemaName);
            if (schema == null) {
                SchemaReference<N> schemaRef = SchemaReference.named(schemaName);
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

        public <N> B addRepeatingAt(int index, DataSchema<N> nestedSchema) {
            return this.addRepeatingFieldAt(index,null , nestedSchema);
        }

        public <N> B addRepeatingField(F field, DataSchema<N> nestedSchema) {
            return this.addRepeatingFieldAt(0, field, nestedSchema);
        }

        public <N> B addRepeatingFieldAt(int index, F field, DataSchema<N> nestedSchema) {
            schemaBuilder.addRepeatingFieldAt(index, field, nestedSchema);
            return self();
        }

        // Repeating Nested Schemas from new inline schema definitions.

        public <N> NewNestedSchema<F, B, N> addRepeatingField(F field, Class<N> fieldType) {

            return addRepeatingIndexedField(0, field, fieldType);
        }

        public <N> NewNestedSchema<F, B, N> addRepeatingIndex(int index, Class<N> fieldType) {

            return addRepeatingIndexedField(index, null, fieldType);
        }

        public <N> NewNestedSchema<F, B, N> addRepeatingIndexedField(int index, F field, Class<N> fieldType) {

            return new NewNestedSchema<>(fieldType, this, index, field, true);
        }



        protected abstract B self();

        abstract public void add();
    }

    public class NewTopLevelSchema<F> extends NewSchema<F, NewTopLevelSchema<F>> {

        private final String name;

        public NewTopLevelSchema(Class<F> fieldType, String name) {
            super(fieldType);
            this.name = Optional.ofNullable(name).orElse(DEFAULT_SCHEMA_NAME);
        }

        public SchemaManager addToManager() {
            add();
            return SchemaManager.this;
        }

        @Override
        protected NewTopLevelSchema<F> self() {
            return this;
        }

        @Override
        public void add() {
            DataSchema<F> schema = this.schemaBuilder.build();
            List<SchemaReference<?>> schemaReferences =
                    SchemaManager.this.schemaRefs.remove(name);
            if (schemaReferences != null) {
                //noinspection unchecked
                schemaReferences.forEach(ref -> ((SchemaReference<F>) ref).set(schema));
            }
            SchemaManager.this.schemaMap.put(name, schema);
        }
    }


    public class NewNestedSchema<P, B extends NewSchema<P, B>, F> extends NewSchema<F, NewNestedSchema<P, B, F>> {

        private final NewSchema<P, B> parentSchema;

        private final int index;

        private final P field;

        private final boolean repeating;

        public NewNestedSchema(Class<F> fieldType,
                               NewSchema<P, B> parentSchema,
                               int index,
                               P field,
                               boolean repeating) {
            super(fieldType);
            this.parentSchema = parentSchema;
            this.index = index;
            this.field = field;
            this.repeating = repeating;
        }

        public NewSchema<P, B> addNested() {
            if (parentSchema instanceof NewTopLevelSchema) {
                throw new UnsupportedOperationException("Use addBack()");
            }
            else {
                add();
                return parentSchema;
            }
        }

        public NewTopLevelSchema<P> addBack() {
            if (parentSchema instanceof NewTopLevelSchema) {
                add();
                return (NewTopLevelSchema<P>) parentSchema;
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
        protected NewNestedSchema<P, B, F> self() {
            return this;
        }
    }

    public <F> DataSchema<F> getDefaultSchema() {
        //noinspection unchecked
        return (DataSchema<F>) schemaMap.get(DEFAULT_SCHEMA_NAME);
    }

    public <F> DataSchema<F> getSchema(String schemaName) {
        //noinspection unchecked
        return (DataSchema<F>) schemaMap.get(schemaName);
    }
}

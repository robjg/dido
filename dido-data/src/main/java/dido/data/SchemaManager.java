package dido.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Manage schemas by name so that nested schemas can easily be constructed.
 */
public class SchemaManager {

    public static final String DEFAULT_SCHEMA_NAME = "default";

    private final Map<String, DataSchema<?>> schemaMap = new HashMap<>();

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

        public B addField(F field, Class<?> type) {
            return addIndexedField(0, field, type);
        }

        public B addIndex(int index, Class<?> type) {
            return addIndexedField(index, null, type);
        }

        public B addIndexedField(int index, F field, Class<?> type) {
            this.schemaBuilder.addIndexedField(index, field, type);
            return self();
        }

        //

        public B addNestedField(F field, String schemaName) {
            return addNestedIndexedField(0, field, schemaName);
        }

        public B addNestedIndex(int index, String schemaName) {
            return addNestedIndexedField(index, null, schemaName);
        }

        public <N> B addNestedIndexedField(int index, F field, String schemaName) {
            Supplier<DataSchema<N>> nestedSchema = () -> (DataSchema<N>) Objects.requireNonNull(
                    SchemaManager.this.schemaMap.get(schemaName), "No Schema " + schemaName);
            return addNestedIndexedField(index, field, nestedSchema);
        }

        //

        public <N> B addNestedField(F field, DataSchema<N> nestedSchema) {
            return this.addNestedIndexedField(0, field, nestedSchema);
        }

        public <N> B addNestedIndex(int index, DataSchema<N> nestedSchema) {
            return this.addNestedIndexedField(index,null , nestedSchema);
        }

        public <N> B addNestedIndexedField(int index, F field, DataSchema<N> nestedSchema) {
            this.schemaBuilder.addNestedIndexedField(index, field, nestedSchema);
            return self();
        }

        //

        public <N> B addNestedField(F field, Supplier<DataSchema<N>> nestedSchemaRef) {
            return this.addNestedIndexedField(0, field, nestedSchemaRef);
        }

        public <N> B addNestedIndex(int index, Supplier<DataSchema<N>> nestedSchemaRef) {
            return this.addNestedIndexedField(index,null , nestedSchemaRef);
        }

        public <N> B addNestedIndexedField(int index, F field, Supplier<DataSchema<N>> nestedSchemaRef) {
            this.schemaBuilder.addNestedIndexedField(index, field, nestedSchemaRef);
            return self();
        }

        //

        public <N> NewNestedSchema<F, B, N> addNestedField(F field, Class<N> fieldType) {

            return addNestedIndexedField(0, field, fieldType);
        }

        public <N> NewNestedSchema<F, B, N> addNestedIndex(int index, Class<N> fieldType) {

            return addNestedIndexedField(index, null, fieldType);
        }

        public <N> NewNestedSchema<F, B, N> addNestedIndexedField(int index, F field, Class<N> fieldType) {

            return new NewNestedSchema<>(fieldType, this, index, field);
        }

        //

        public <N> B addNestedRepeatingField(F field, String schemaName) {
            Supplier<DataSchema<N>> nestedSchema = () -> (DataSchema<N>)  Objects.requireNonNull(
                    SchemaManager.this.schemaMap.get(schemaName), "No Schema " + schemaName);
            this.schemaBuilder.addNestedRepeatingField(field, nestedSchema);
            return self();
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
            SchemaManager.this.schemaMap.put(name, this.schemaBuilder.build());
        }
    }


    public class NewNestedSchema<P, B extends NewSchema<P, B>, F> extends NewSchema<F, NewNestedSchema<P, B, F>> {

        private final NewSchema<P, B> parentSchema;

        private final int index;

        private final P field;

        public NewNestedSchema(Class<F> fieldType, NewSchema<P, B> parentSchema, int index, P field) {
            super(fieldType);
            this.parentSchema = parentSchema;
            this.index = index;
            this.field = field;
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
            this.parentSchema.schemaBuilder.addNestedIndexedField(
                    index, field, this.schemaBuilder.build());
        }

        @Override
        protected NewNestedSchema<P, B, F> self() {
            return this;
        }
    }

    public <F> DataSchema<F> getDefaultSchema() {
        return (DataSchema<F>) schemaMap.get(DEFAULT_SCHEMA_NAME);
    }

    public <F> DataSchema<F> getSchema(String schemaName) {
        return (DataSchema<F>) schemaMap.get(schemaName);
    }
}

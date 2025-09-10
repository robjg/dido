package dido.data.generic;

import dido.data.DataSchema;
import dido.data.SchemaField;
import dido.data.schema.SchemaRefImpl;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Function;

class GenericSchemaFields<F> implements GenericSchemaField.Of<F> {

    private final Function<? super String, ? extends F> fieldMappingFunc;

    GenericSchemaFields(Function<? super String, ? extends F> fieldMappingFunc) {
        this.fieldMappingFunc = fieldMappingFunc;
    }

    @Override
    public GenericSchemaField<F> of(int index, String name, Class<?> type) {
        return of(index, fieldMappingFunc.apply(name), type);
    }

    @Override
    public GenericSchemaField<F> of(int index, F field, Class<?> type) {
        return new Extension(
                SchemaField.of(
                        index,
                        Objects.requireNonNull(field, "Field can not be null").toString(),
                        type),
                field);
    }

    @Override
    public GenericSchemaField<F> ofNested(int index, String name, SchemaRefImpl nestedRef) {
        return ofNested(index, fieldMappingFunc.apply(name), nestedRef);
    }

    @Override
    public GenericSchemaField<F> ofNested(int index, F field, SchemaRefImpl nestedRef) {
        return new Extension(
                SchemaField.ofNested(
                        index,
                        Objects.requireNonNull(field, "Field can not be null").toString(),
                        nestedRef),
                field);
    }

    @Override
    public GenericSchemaField<F> ofNested(int index, String name, DataSchema nested) {
        return ofNested(index, fieldMappingFunc.apply(name), nested);
    }

    @Override
    public GenericSchemaField<F> ofNested(int index, F field, DataSchema nested) {
        return new Extension(
                SchemaField.ofNested(
                        index,
                        Objects.requireNonNull(field, "Field can not be null").toString(),
                        nested),
                field);
    }

    @Override
    public GenericSchemaField<F> ofRepeating(int index, String name, DataSchema nested) {
        return ofRepeating(index, fieldMappingFunc.apply(name), nested);
    }

    @Override
    public GenericSchemaField<F> ofRepeating(int index, F field, DataSchema nested) {
        return new Extension(
                SchemaField.ofRepeating(
                        index,
                        Objects.requireNonNull(field, "Field can not be null").toString(),
                        nested),
                field);
    }

    @Override
    public GenericSchemaField<F> ofRepeating(int index, String name, SchemaRefImpl nestedRef) {
        return ofRepeating(index, fieldMappingFunc.apply(name), nestedRef);
    }

    @Override
    public GenericSchemaField<F> ofRepeating(int index, F field, SchemaRefImpl nestedRef) {
        return new Extension(
                SchemaField.ofRepeating(
                        index,
                        Objects.requireNonNull(field, "Field can not be null").toString(),
                        nestedRef),
                field);
    }

    @Override
    public GenericSchemaField<F> from(SchemaField schemaField) {
        return new Extension(schemaField, fieldMappingFunc.apply(schemaField.getName()));
    }

    private final class Extension implements GenericSchemaField<F> {

        private final SchemaField delegate;

        private final F field;

        private Extension(SchemaField delegate, F field) {
            this.delegate = delegate;
            this.field = field;
        }

        @Override
        public int getIndex() {
            return delegate.getIndex();
        }

        @Override
        public Type getType() {
            return delegate.getType();
        }

        @Override
        public boolean isNested() {
            return delegate.isNested();
        }

        @Override
        public boolean isRepeating() {
            return delegate.isRepeating();
        }

        @Override
        public F getField() {
            return field;
        }

        @Override
        public DataSchema getNestedSchema() {
            return delegate.getNestedSchema();
        }

        @Override
        public SchemaField mapTo(int toIndex, String toName) {


            F toField = toName == null ? null : fieldMappingFunc.apply(toName);

            return new Extension(delegate.mapTo(toIndex, toName),
                    toField);
        }

        @Override
        public GenericSchemaField<F> mapTo(int toIndex, F toField) {

            String name = toField == null ? null : toField.toString();

            return new Extension(delegate.mapTo(toIndex, name),
                    toField);
        }

        @Override
        public int hashCode() {
            return SchemaField.hash(this);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof SchemaField) {
                return SchemaField.equals(this, (SchemaField) obj);
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return delegate.toString();
        }
    }

}

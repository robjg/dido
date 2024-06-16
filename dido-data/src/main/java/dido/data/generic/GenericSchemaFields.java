package dido.data.generic;

import dido.data.DataSchema;
import dido.data.SchemaField;
import dido.data.SchemaReference;

class GenericSchemaFields {

    public static <F> GenericSchemaField<F> of(int index, F field, Class<?> type) {
        return new Extension<>(
                SchemaField.of(index, field == null ? null : field.toString(), type),
                field);
    }

    public static <F> GenericSchemaField<F> ofNested(int index, F field, SchemaReference nestedRef) {
        return new Extension<>(
                SchemaField.ofNested(index, field == null ? null : field.toString(), nestedRef),
                field);
    }

    public static <F> GenericSchemaField<F> ofNested(int index, F field, DataSchema nested) {
        return new Extension<>(SchemaField.ofNested(
                index, field == null ? null : field.toString(), nested),
                field);
    }

    public static <F> GenericSchemaField<F> ofRepeating(int index, F field, DataSchema nested) {
        return new Extension<>(SchemaField.ofRepeating(
                index, field == null ? null : field.toString(), nested), field);
    }

    public static <F> GenericSchemaField<F> ofRepeating(int index, F field, SchemaReference nestedRef) {
        return new Extension<>(SchemaField.ofRepeating(
                index, field == null ? null : field.toString(), nestedRef),
                field);
    }

    private static final class Extension<F> implements GenericSchemaField<F> {

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
        public Class<?> getType() {
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

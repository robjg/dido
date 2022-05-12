package dido.data;

import java.util.Objects;

class SchemaFields {

    public static <F> SchemaField<F> of(int index, Class<?> type) {
        return new Indexed<>(index, type);
    }

    public static <F> SchemaField<F> of(int index, F field, Class<?> type) {
        if (field == null) {
            return of(index, type);
        } else {
            return new Simple<>(index, field, type);
        }
    }

    public static <F, N> SchemaField<F> ofNested(int index, DataSchema<N> nested) {
        return new Nested<>(new Indexed<>(index, IndexedData.class),
                nested, SchemaField.Is.NESTED);
    }

    public static <F, N> SchemaField<F> ofNested(int index, SchemaReference<N> nested) {
        return new NestedRef<>(new Indexed<>(index, IndexedData.class),
                nested, SchemaField.Is.NESTED);
    }

    public static <F, N> SchemaField<F> ofNested(int index, F field, DataSchema<N> nested) {
        if (field == null) {
            return ofNested(index, nested);
        } else {
            return new Nested<>(new Simple<>(index, field, IndexedData.class),
                    nested, SchemaField.Is.NESTED);
        }
    }

    public static <F, N> SchemaField<F> ofRepeating(int index, DataSchema<N> nested) {
        return new Nested<>(new Indexed<>(index, IndexedData.class),
                nested, SchemaField.Is.REPEATING);
    }

    public static <F, N> SchemaField<F> ofRepeating(int index, SchemaReference<N> nested) {
        return new NestedRef<>(new Indexed<>(index, IndexedData.class),
                nested, SchemaField.Is.REPEATING);
    }

    public static <F, N> SchemaField<F> ofRepeating(int index, F field, DataSchema<N> nested) {
        if (field == null) {
            return ofRepeating(index, nested);
        } else {
            return new Nested<>(new Simple<>(index, field, IndexedData.class),
                    nested, SchemaField.Is.REPEATING);
        }
    }

    public static <F, N> SchemaField<F> ofRepeating(int index, F field, SchemaReference<N> nestedRef) {
        if (field == null) {
            return ofRepeating(index, nestedRef);
        } else {
            return new NestedRef<>(new Simple<>(index, field, IndexedData.class),
                    nestedRef, SchemaField.Is.REPEATING);
        }
    }
    private static final class Indexed<F> implements SchemaField<F> {

        private final int index;

        private final Class<?> type;

        private Indexed(int index, Class<?> type) {
            if (index < 1) {
                throw new IllegalArgumentException("Index must be > 0");
            }
            this.index = index;
            this.type = Objects.requireNonNull(type);
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public Class<?> getType() {
            return type;
        }

        @Override
        public Is getIs() {
            return Is.SIMPLE;
        }

        @Override
        public F getField() {
            return null;
        }

        @Override
        public <N> DataSchema<N> getNestedSchema() {
            return null;
        }

        @Override
        public int hashCode() {
            return index + 31 * type.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Indexed) {
                Indexed<?> other = (Indexed<?>) obj;
                return other.index == this.index
                        && other.type == this.type;
            }
            if (obj instanceof SchemaField) {
                SchemaField<?> other = (SchemaField<?>) obj;
                return other.getIs() == Is.SIMPLE
                        && other.getIndex() == this.index
                        && other.getType() == this.type;
            }
            return false;
        }

        @Override
        public String toString() {
            return '[' + index + "]=" + type;
        }
    }

    private static final class Simple<F> implements SchemaField<F> {

        private final Indexed<F> indexed;

        private final F field;

        private Simple(int index, F field, Class<?> type) {
            this.indexed = new Indexed<>(index, type);
            this.field = Objects.requireNonNull(field);
        }

        @Override
        public int getIndex() {
            return indexed.index;
        }

        @Override
        public Class<?> getType() {
            return indexed.type;
        }

        @Override
        public Is getIs() {
            return Is.SIMPLE;
        }

        @Override
        public F getField() {
            return field;
        }

        @Override
        public <N> DataSchema<N> getNestedSchema() {
            return null;
        }

        @Override
        public int hashCode() {
            return Objects.hash(indexed, field);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Simple) {
                Simple<?> other = (Simple<?>) obj;
                return other.indexed.equals(this.indexed)
                        && other.field.equals(this.field);
            }
            if (obj instanceof SchemaField) {
                SchemaField<?> other = (SchemaField<?>) obj;
                return other.getIs() == Is.SIMPLE
                        && other.getIndex() == this.indexed.index
                        && other.getType() == this.indexed.type
                        && this.field.equals(other.getField());
            }
            return false;
        }

        @Override
        public String toString() {
            return '[' + indexed.index + ':' + field.toString() + "]=" + indexed.type;
        }
    }

    private static final class Nested<F> implements SchemaField<F> {

        private final SchemaField<F> simple;

        private final DataSchema<?> nested;

        private final Is is;

        private Nested(SchemaField<F> simple, DataSchema<?> nested, Is is) {
            this.simple = simple;
            this.nested = Objects.requireNonNull(nested);
            this.is = Objects.requireNonNull(is);
        }


        @Override
        public int getIndex() {
            return simple.getIndex();
        }

        @Override
        public Class<?> getType() {
            return simple.getType();
        }

        @Override
        public Is getIs() {
            return is;
        }

        @Override
        public F getField() {
            return simple.getField();
        }

        @Override
        public <N> DataSchema<N> getNestedSchema() {
            //noinspection unchecked
            return (DataSchema<N>) nested;
        }

        @Override
        public int hashCode() {
            return Objects.hash(simple, is, nested);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof SchemaField) {
                SchemaField<?> other = (SchemaField<?>) obj;
                return other.getIs() == this.is
                        && other.getIndex() == this.simple.getIndex()
                        && Objects.equals(other.getField(), this.simple.getField())
                        && this.nested.equals(other.getNestedSchema());
            }
            return false;
        }

        @Override
        public String toString() {
            String field;
            F f = simple.getField();
            if (f == null) {
                field = "";
            } else {
                field = ": " + f;
            }
            return '[' + simple.getIndex() + field +
                    "]=" + nested;
        }
    }

    private static final class NestedRef<F> implements SchemaField<F> {

        private final SchemaField<F> simple;

        private final SchemaReference<?> nested;

        private final Is is;

        private NestedRef(SchemaField<F> simple, SchemaReference<?> nested, Is is) {
            this.simple = simple;
            this.nested = Objects.requireNonNull(nested);
            this.is = Objects.requireNonNull(is);
        }


        @Override
        public int getIndex() {
            return simple.getIndex();
        }

        @Override
        public Class<?> getType() {
            return simple.getType();
        }

        @Override
        public Is getIs() {
            return is;
        }

        @Override
        public F getField() {
            return simple.getField();
        }

        @Override
        public <N> DataSchema<N> getNestedSchema() {
            //noinspection unchecked
            return (DataSchema<N>) nested.get();
        }

        @Override
        public int hashCode() {
            return Objects.hash(simple, is, nested);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof SchemaField) {
                SchemaField<?> other = (SchemaField<?>) obj;
                return other.getIs() == this.is
                        && other.getIndex() == this.simple.getIndex()
                        && Objects.equals(other.getField(), this.simple.getField())
                        && this.nested.equals(other.getNestedSchema());
            }
            return false;
        }

        @Override
        public String toString() {
            String field;
            F f = simple.getField();
            if (f == null) {
                field = "";
            } else {
                field = ": " + f;
            }
            return '[' + simple.getIndex() + field +
                    "]=" + nested;
        }
    }
}

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
        return new Nested<>(new Indexed<>(index, SchemaField.NESTED_TYPE),
                nested, false);
    }

    public static <F, N> SchemaField<F> ofNested(int index, SchemaReference<N> nestedRef) {
        return new NestedRef<>(new Indexed<>(index, SchemaField.NESTED_TYPE),
                nestedRef, false);
    }

    public static <F, N> SchemaField<F> ofNested(int index, F field, SchemaReference<N> nestedRef) {
        if (field == null) {
            return ofNested(index, nestedRef);
        } else {
            return new NestedRef<>(new Simple<>(index, field, SchemaField.NESTED_TYPE),
                    nestedRef, false);
        }
    }

    public static <F, N> SchemaField<F> ofNested(int index, F field, DataSchema<N> nested) {
        if (field == null) {
            return ofNested(index, nested);
        } else {
            return new Nested<>(new Simple<>(index, field, SchemaField.NESTED_TYPE),
                    nested, false);
        }
    }

    public static <F, N> SchemaField<F> ofRepeating(int index, DataSchema<N> nested) {
        return new Nested<>(new Indexed<>(index, SchemaField.NESTED_REPEATING_TYPE),
                nested, true);
    }

    public static <F, N> SchemaField<F> ofRepeating(int index, SchemaReference<N> nestedRef) {
        return new NestedRef<>(new Indexed<>(index, SchemaField.NESTED_REPEATING_TYPE),
                nestedRef, true);
    }

    public static <F, N> SchemaField<F> ofRepeating(int index, F field, DataSchema<N> nested) {
        if (field == null) {
            return ofRepeating(index, nested);
        } else {
            return new Nested<>(new Simple<>(index, field, SchemaField.NESTED_REPEATING_TYPE),
                    nested, true);
        }
    }

    public static <F, N> SchemaField<F> ofRepeating(int index, F field, SchemaReference<N> nestedRef) {
        if (field == null) {
            return ofRepeating(index, nestedRef);
        } else {
            return new NestedRef<>(new Simple<>(index, field, SchemaField.NESTED_REPEATING_TYPE),
                    nestedRef, true);
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
        public boolean isNested() {
            return false;
        }

        @Override
        public boolean isRepeating() {
            return false;
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
                return !other.isNested()
                        && !other.isRepeating()
                        && other.getIndex() == this.index
                        && other.getType() == this.type;
            }
            return false;
        }

        @Override
        public String toString() {
            return "[" + index + "]=" + type.getName();
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
        public boolean isNested() {
            return false;
        }

        @Override
        public boolean isRepeating() {
            return false;
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
                return !other.isNested()
                        && !other.isRepeating()
                        && other.getIndex() == this.indexed.index
                        && other.getType() == this.indexed.type
                        && this.field.equals(other.getField());
            }
            return false;
        }

        @Override
        public String toString() {
            return "[" + indexed.index + ':' + field + "]=" + indexed.type.getName();
        }
    }

    private static final class Nested<F> implements SchemaField<F> {

        private final SchemaField<F> simple;

        private final DataSchema<?> nested;

        private final boolean repeating;

        private Nested(SchemaField<F> simple, DataSchema<?> nested, boolean repeating) {
            this.simple = simple;
            this.nested = Objects.requireNonNull(nested);
            this.repeating = repeating;
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
        public boolean isNested() {
            return true;
        }

        @Override
        public boolean isRepeating() {
            return repeating;
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
            return Objects.hash(simple, repeating, nested);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof SchemaField) {
                SchemaField<?> other = (SchemaField<?>) obj;
                return other.isNested()
                        && other.isRepeating() == repeating
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
                field = ":" + f;
            }
            String nested;
            if (repeating) {
                nested = "[" + this.nested + "]";
            }
            else {
                nested = this.nested.toString();
            }
            return "[" + simple.getIndex() + field +
                    "]=" + nested;
        }
    }

    private static final class NestedRef<F> implements SchemaField<F> {

        private final SchemaField<F> simple;

        private final SchemaReference<?> nestedRef;

        private final boolean repeating;

        private NestedRef(SchemaField<F> simple,
                          SchemaReference<?> nestedRef,
                          boolean repeating) {
            this.simple = simple;
            this.nestedRef = Objects.requireNonNull(nestedRef);
            this.repeating = repeating;
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
        public boolean isNested() {
            return true;
        }

        @Override
        public boolean isRepeating() {
            return repeating;
        }

        @Override
        public F getField() {
            return simple.getField();
        }

        @Override
        public <N> DataSchema<N> getNestedSchema() {
            //noinspection unchecked
            return (DataSchema<N>) nestedRef.get();
        }

        @Override
        public int hashCode() {
            return Objects.hash(simple, repeating, nestedRef);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (nestedRef.get() == null) {
                return false;
            }
            if (obj instanceof SchemaField) {
                SchemaField<?> other = (SchemaField<?>) obj;
                return other.isNested()
                        && other.isRepeating() == repeating
                        && other.getIndex() == this.simple.getIndex()
                        && Objects.equals(other.getField(), this.simple.getField())
                        && this.nestedRef.get().equals(other.getNestedSchema());
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
                field = ":" + f;
            }
            String nested;
            if (repeating) {
                nested = "[" + this.nestedRef + "]";
            }
            else {
                nested = this.nestedRef.toString();
            }
            return "[" + simple.getIndex() + field +
                    "]=" + nested;
        }
    }
}

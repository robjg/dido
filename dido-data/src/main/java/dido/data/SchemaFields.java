package dido.data;

import java.util.Objects;

class SchemaFields {

    public static SchemaField of(int index, Class<?> type) {
        return new Indexed(index, type);
    }

    public static SchemaField of(int index, String field, Class<?> type) {
        if (field == null) {
            return of(index, type);
        } else {
            return new Simple(index, field, type);
        }
    }

    public static SchemaField ofNested(int index, DataSchema nested) {
        return new Nested(new Indexed(index, GenericSchemaField.NESTED_TYPE),
                nested, false);
    }

    public static SchemaField ofNested(int index, SchemaReference<?> nestedRef) {
        return new NestedRef(new Indexed(index, GenericSchemaField.NESTED_TYPE),
                nestedRef, false);
    }

    public static SchemaField ofNested(int index, String field, SchemaReference<?> nestedRef) {
        if (field == null) {
            return ofNested(index, nestedRef);
        } else {
            return new NestedRef(new Simple(index, field, GenericSchemaField.NESTED_TYPE),
                    nestedRef, false);
        }
    }

    public static SchemaField ofNested(int index, String field, DataSchema nested) {
        if (field == null) {
            return ofNested(index, nested);
        } else {
            return new Nested(new Simple(index, field, GenericSchemaField.NESTED_TYPE),
                    nested, false);
        }
    }

    public static SchemaField ofRepeating(int index, DataSchema nested) {
        return new Nested(new Indexed(index, GenericSchemaField.NESTED_REPEATING_TYPE),
                nested, true);
    }

    public static SchemaField ofRepeating(int index, SchemaReference<?> nestedRef) {
        return new NestedRef(new Indexed(index, GenericSchemaField.NESTED_REPEATING_TYPE),
                nestedRef, true);
    }

    public static SchemaField ofRepeating(int index, String field, DataSchema nested) {
        if (field == null) {
            return ofRepeating(index, nested);
        } else {
            return new Nested(new Simple(index, field, GenericSchemaField.NESTED_REPEATING_TYPE),
                    nested, true);
        }
    }

    public static SchemaField ofRepeating(int index, String field, SchemaReference<?> nestedRef) {
        if (field == null) {
            return ofRepeating(index, nestedRef);
        } else {
            return new NestedRef(new Simple(index, field, GenericSchemaField.NESTED_REPEATING_TYPE),
                    nestedRef, true);
        }
    }

    private static final class Indexed implements SchemaField {

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
        public String getName() {
            return null;
        }

        @Override
        public DataSchema getNestedSchema() {
            return null;
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
            }
            else {
                return false;
            }
        }

        @Override
        public String toString() {
            return "[" + index + "]=" + type.getName();
        }
    }

    private static final class Simple implements SchemaField {

        private final Indexed indexed;

        private final String field;

        private Simple(int index, String field, Class<?> type) {
            this.indexed = new Indexed(index, type);
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
        public String getName() {
            return field;
        }

        @Override
        public DataSchema getNestedSchema() {
            return null;
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
            }
            else {
                return false;
            }
        }

        @Override
        public String toString() {
            return "[" + indexed.index + ':' + field + "]=" + indexed.type.getName();
        }
    }

    private static final class Nested implements SchemaField {

        private final SchemaField simple;

        private final DataSchema nested;

        private final boolean repeating;

        private Nested(SchemaField simple, DataSchema nested, boolean repeating) {
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
        public String getName() {
            return simple.getName();
        }

        @Override
        public DataSchema getNestedSchema() {
            return nested;
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
            }
            else {
                return false;
            }
        }

        @Override
        public String toString() {
            String field;
            String f = simple.getName();
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

    private static final class NestedRef implements SchemaField {

        private final SchemaField simple;

        private final SchemaReference<?> nestedRef;

        private final boolean repeating;

        private NestedRef(SchemaField simple,
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
        public String getName() {
            return simple.getName();
        }

        @Override
        public DataSchema getNestedSchema() {
            return nestedRef.get();
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
            }
            else {
                return false;
            }
        }

        @Override
        public String toString() {
            String field;
            String f = simple.getName();
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

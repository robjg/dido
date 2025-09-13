package dido.data.schema;

import dido.data.DataSchema;
import dido.data.SchemaField;

import java.lang.reflect.Type;
import java.util.Objects;

public class SchemaFields {

    public static SchemaField of(int index, String field, Type type) {
        return new Simple(index, field, type);
    }

    public static SchemaField.RefFactory refOf(int index, String field, String schemaName) {
        return new NestedRefFactory(new Simple(index, field, SchemaField.NESTED_TYPE),
                schemaName, false);
    }

    public static SchemaField ofNested(int index, String field, DataSchema nested) {
        return new Nested(new Simple(index, field, SchemaField.NESTED_TYPE),
                nested, false);
    }

    public static SchemaField.RefFactory repeatingRefOf(int index, String field, String schemaName) {
        return new NestedRefFactory(new Simple(index, field, SchemaField.NESTED_REPEATING_TYPE),
                schemaName, true);
    }

    public static SchemaField ofRepeating(int index, String field, DataSchema nested) {
        return new Nested(new Simple(index, field, SchemaField.NESTED_REPEATING_TYPE),
                nested, true);
    }

    private static final class Simple implements SchemaField {

        private final int index;

        private final String name;

        private final Type type;

        private Simple(int index, String name, Type type) {
            this.index = index;
            this.name = name;
            this.type = Objects.requireNonNull(type);
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public Type getType() {
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
            return name;
        }

        @Override
        public DataSchema getNestedSchema() {
            return null;
        }

        @Override
        public SchemaField mapTo(int toIndex, String toName) {

            return new Simple(toIndex, toName, getType());
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
            return "[" + index + ':' + name + "]=" + type.getTypeName();
        }
    }

    private static final class Nested implements SchemaField {

        private final SchemaField simple;

        private final DataSchema nested;

        private final boolean repeating;

        private Nested(SchemaField simple, DataSchema nested, boolean repeating) {
            this.simple = simple;
            this.nested = Objects.requireNonNull(nested, "Null nested schema for " + simple);
            this.repeating = repeating;
        }


        @Override
        public int getIndex() {
            return simple.getIndex();
        }

        @Override
        public Type getType() {
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
        public SchemaField mapTo(int toIndex, String toName) {

            toIndex = toIndex == 0 ? getIndex() : toIndex;
            toName = toName == null ? getName() : toName;

            return new Nested(new Simple(toIndex, toName, this.simple.getType()),
                    this.nested, this.repeating);
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
            } else {
                nested = this.nested.toString();
            }
            return "[" + simple.getIndex() + field +
                    "]=" + nested;
        }
    }

    private static final class NestedRefFactory implements SchemaField.RefFactory {

        private final SchemaField simple;

        private final String schemaName;

        private final boolean repeating;

        private NestedRefFactory(SchemaField simple, String schemaName, boolean repeating) {
            this.simple = simple;
            this.schemaName = Objects.requireNonNull(schemaName, "No Ref Shema Name");
            this.repeating = repeating;
        }

        public SchemaField.Ref toSchemaField(SchemaDefs defs) {
            return new NestedRef(simple,
                    defs.getSchemaRef(schemaName),
                    repeating);
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
                nested = "[" + schemaName + "]";
            } else {
                nested = schemaName;
            }
            return "RefFactory{" + simple.getIndex() + field +
                    "]=" + nested + "}";
        }
    }

    private static final class NestedRef implements SchemaField.Ref {

        private final SchemaField simple;

        private final SchemaRef nestedRef;

        private final boolean repeating;

        private NestedRef(SchemaField simple,
                          SchemaRef nestedRef,
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
        public Type getType() {
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
        public SchemaField mapTo(int toIndex, String toName) {

            toIndex = toIndex == 0 ? getIndex() : toIndex;
            toName = toName == null ? getName() : toName;

            return new NestedRef(new Simple(toIndex, toName, this.simple.getType()),
                    this.nestedRef, this.repeating);
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
        public String getSchemaName() {
            return nestedRef.getSchemaName();
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
            } else {
                nested = this.nestedRef.toString();
            }
            return "[" + simple.getIndex() + field +
                    "]=" + nested;
        }
    }
}

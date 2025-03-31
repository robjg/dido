package dido.operators.transform;

import dido.data.*;
import dido.data.NoSuchFieldException;
import dido.data.useful.AbstractData;
import dido.data.useful.DataSchemaImpl;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Provides a Builder for creating {@link DidoTransform}s that is
 * a view of underlying data define with {@link FieldTransform}s.
 */
public class ViewTransformBuilder {

    private final ReadSchema incomingSchema;

    private final NavigableMap<Integer, FieldGetter> opsByIndex = new TreeMap<>(); ;

    private final SchemaFactory schemaFactory;

    private final boolean reIndex;

    private ViewTransformBuilder(DataSchema incomingSchema,
                                 boolean reIndex) {
        this.incomingSchema = ReadSchema.from(incomingSchema);
        this.schemaFactory = DataSchemaFactory.newInstance();
        this.reIndex = reIndex;
    }

    public static class Settings {

        private boolean copy;

        private boolean reIndex;

        public Settings copy(boolean copy) {
            this.copy = copy;
            return this;
        }

        public Settings reIndex(boolean reIndex) {
            this.reIndex = reIndex;
            return this;
        }

        public ViewTransformBuilder forSchema(DataSchema incomingSchema) {

            ViewTransformBuilder builder = new ViewTransformBuilder(
                    incomingSchema,
                    reIndex);

            if (copy) {
                for (SchemaField schemaField: incomingSchema.getSchemaFields()) {

                    builder.addOp(FieldOps.copy().index(schemaField.getIndex()).with().view());
                }
            }

            return builder;
        }
    }

    public static Settings with() {
        return new Settings();
    }

    public static ViewTransformBuilder forSchema(DataSchema incomingSchema) {
        return with().forSchema(incomingSchema);
    }

    class SchemaSetterImpl implements SchemaSetter {

        @Override
        public SchemaField addField(SchemaField schemaField) {
            SchemaField existing = null;

            String newName = schemaField.getName();
            int newIndex = schemaField.getIndex();

            if (newName != null) {
                existing = schemaFactory.removeNamed(newName);
            }
            if (existing == null && newIndex > 0) {
                existing = schemaFactory.removeAt(newIndex);
            }
            if (existing == null) {
                return schemaFactory.addSchemaField(schemaField);
            }
            else {
                if (newName == null) {
                    newName = existing.getName();
                }
                if (newIndex == 0) {
                    newIndex = existing.getIndex();
                }
                return schemaFactory.addSchemaField(schemaField
                        .mapToIndex(newIndex).mapToFieldName(newName));
            }
        }

        @Override
        public SchemaField removeField(SchemaField schemaField) {
            SchemaField removedField = schemaFactory.removeNamed(schemaField.getName());
            if (removedField != null) {
                opsByIndex.remove(removedField.getIndex());
            }
            return removedField;
        }
    }

    class ViewDefinitionImpl implements FieldView.Definition {

        @Override
        public void addField(SchemaField schemaField, FieldGetter fieldGetter) {
            SchemaField existing = null;

            String newName = schemaField.getName();
            int newIndex = schemaField.getIndex();

            if (newName != null) {
                existing = schemaFactory.removeNamed(newName);
            }
            if (existing == null && newIndex > 0) {
                existing = schemaFactory.removeAt(newIndex);
            }

            SchemaField newField;
            if (existing == null) {
                newField =  schemaFactory.addSchemaField(schemaField);
            }
            else {
                if (newName == null) {
                    newName = existing.getName();
                }
                if (newIndex == 0) {
                    newIndex = existing.getIndex();
                }
                newField =  schemaFactory.addSchemaField(schemaField
                        .mapToIndex(newIndex).mapToFieldName(newName));
            }
            opsByIndex.put(newField.getIndex(), fieldGetter);
        }

        @Override
        public void removeField(SchemaField schemaField) {
            SchemaField removedField = schemaFactory.removeNamed(schemaField.getName());
            if (removedField != null) {
                opsByIndex.remove(removedField.getIndex());
            }
        }
    }

    public ViewTransformBuilder addOp(FieldView opDef) {
        ViewDefinitionImpl schemaSetter = new ViewDefinitionImpl();
        opDef.define(incomingSchema, schemaSetter);
        return this;
    }

    public DidoTransform build() {

        SchemaFactory schemaFactory ;
        NavigableMap<Integer, FieldGetter> opsByIndex;

        if (reIndex) {
            opsByIndex = new TreeMap<>();
            schemaFactory = DataSchemaFactory.newInstance();
            for (SchemaField schemaField : this.schemaFactory.getSchemaFields()) {
                FieldGetter fieldGetter = this.opsByIndex.get(schemaField.getIndex());
                schemaField = schemaFactory.addSchemaField(schemaField.mapToIndex(0));
                opsByIndex.put(schemaField.getIndex(), fieldGetter);
            }
        } else {
            schemaFactory = this.schemaFactory;
            opsByIndex = this.opsByIndex;
        }

        FieldGetter[] getters = new FieldGetter[schemaFactory.lastIndex()];

        for (Map.Entry<Integer, FieldGetter> transforms : opsByIndex.entrySet()) {
            getters[transforms.getKey() - 1] = new FieldGetterDelegate(transforms.getValue());
        }

        ViewDataSchema schema = new ViewDataSchema(schemaFactory, getters);

        return new TransformImpl(schema);
    }

    static class FieldGetterDelegate implements FieldGetter {

        private final FieldGetter delegate;

        FieldGetterDelegate(FieldGetter delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean has(DidoData data) {
            return delegate.has(((ViewData) data).original);
        }

        @Override
        public Object get(DidoData data) {
            return delegate.get(((ViewData) data).original);
        }

        @Override
        public boolean getBoolean(DidoData data) {
            return delegate.getBoolean(((ViewData) data).original);
        }

        @Override
        public char getChar(DidoData data) {
            return delegate.getChar(((ViewData) data).original);
        }

        @Override
        public byte getByte(DidoData data) {
            return delegate.getByte(((ViewData) data).original);
        }

        @Override
        public short getShort(DidoData data) {
            return delegate.getShort(((ViewData) data).original);
        }

        @Override
        public int getInt(DidoData data) {
            return delegate.getInt(((ViewData) data).original);
        }

        @Override
        public long getLong(DidoData data) {
            return delegate.getLong(((ViewData) data).original);
        }

        @Override
        public float getFloat(DidoData data) {
            return delegate.getFloat(((ViewData) data).original);
        }

        @Override
        public double getDouble(DidoData data) {
            return delegate.getDouble(((ViewData) data).original);
        }

        @Override
        public String getString(DidoData data) {
            return delegate.getString(((ViewData) data).original);
        }
    }

    static class TransformImpl implements DidoTransform {

        private final ViewDataSchema schema;

        TransformImpl(ViewDataSchema schema) {
            this.schema = schema;
        }

        @Override
        public DataSchema getResultantSchema() {
            return schema;
        }

        @Override
        public DidoData apply(DidoData data) {
            return new ViewData(schema, data);
        }
    }

    public static class ViewDataSchema extends DataSchemaImpl
            implements ReadSchema {

        private final FieldGetter[] getters;

        ViewDataSchema(DataSchema from, FieldGetter[] getters) {
            super(from.getSchemaFields(), from.firstIndex(), from.lastIndex());
            this.getters = getters;
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            try {
                return getters[index - 1];
            }
            catch (IndexOutOfBoundsException e) {
                throw new NoSuchFieldException(index, this);
            }
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            int index = getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, this);
            }

            return getFieldGetterAt(index);
        }
    }

    static class ViewData extends AbstractData {

        private final ViewDataSchema schema;

        private final DidoData original;

        ViewData(ViewDataSchema schema, DidoData original) {
            this.schema = schema;
            this.original = original;
        }

        @Override
        public ReadSchema getSchema() {
            return schema;
        }

        @Override
        public Object getAt(int index) {
            return schema.getFieldGetterAt(index).get(this);
        }
    }


}

package dido.operators.transform;

import dido.data.*;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Provides a Builder for creating {@link DidoTransform}s from {@link OpDef}s.
 */
public class OpTransformBuilder {

    private final DataFactoryProvider dataFactoryProvider;

    private final ReadSchema incomingSchema;

    private final NavigableMap<Integer, OpDef.Prepare> opsByIndex = new TreeMap<>(); ;

    private final SchemaFactory schemaFactory;

    private final boolean reIndex;

    private OpTransformBuilder(DataFactoryProvider dataFactoryProvider,
                               DataSchema incomingSchema,
                               boolean reIndex) {
        this.dataFactoryProvider = dataFactoryProvider;
        this.incomingSchema = ReadSchema.from(incomingSchema);
        this.schemaFactory = dataFactoryProvider.getSchemaFactory();
        this.reIndex = reIndex;
    }

    public static class Settings {

        private DataFactoryProvider dataFactoryProvider;

        private boolean copy;

        private boolean reIndex;

        public Settings dataFactoryProvider(DataFactoryProvider dataFactoryProvider) {
            this.dataFactoryProvider = dataFactoryProvider;
            return this;
        }

        public Settings copy(boolean copy) {
            this.copy = copy;
            return this;
        }

        public Settings reIndex(boolean reIndex) {
            this.reIndex = reIndex;
            return this;
        }

        public OpTransformBuilder forSchema(DataSchema incomingSchema) {

            DataFactoryProvider dataFactoryProvider = Objects.requireNonNullElse(
                    this.dataFactoryProvider, DataFactoryProvider.newInstance());

            OpTransformBuilder builder = new OpTransformBuilder(dataFactoryProvider,
                    incomingSchema,
                    reIndex);

            if (copy) {
                for (SchemaField schemaField: incomingSchema.getSchemaFields()) {

                    builder.addOp(FieldOps.copyAt(schemaField.getIndex()));
                }
            }

            return builder;
        }
    }

    public static Settings with() {
        return new Settings();
    }

    public static OpTransformBuilder forSchema(DataSchema incomingSchema) {
        return with().forSchema(incomingSchema);
    }

    class SchemaSetterImpl implements SchemaSetter {

        SchemaField lastField;

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
                lastField = schemaFactory.addSchemaField(schemaField);
            }
            else {
                if (newName == null) {
                    newName = existing.getName();
                }
                if (newIndex == 0) {
                    newIndex = existing.getIndex();
                }
                lastField = schemaFactory.addSchemaField(schemaField
                        .mapToIndex(newIndex).mapToFieldName(newName));
            }
            return lastField;
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



    public OpTransformBuilder addOp(OpDef opDef) {
        SchemaSetterImpl schemaSetter = new SchemaSetterImpl();
        OpDef.Prepare prepare = opDef.prepare(incomingSchema, schemaSetter);
        if (schemaSetter.lastField != null) {
            opsByIndex.put(schemaSetter.lastField.getIndex(), prepare);
        }
        return this;
    }

    public DidoTransform build() {

        SchemaFactory schemaFactory ;
        NavigableMap<Integer, OpDef.Prepare> opsByIndex;

        if (reIndex) {
            opsByIndex = new TreeMap<>();
            schemaFactory = dataFactoryProvider.getSchemaFactory();
            for (SchemaField schemaField : this.schemaFactory.getSchemaFields()) {
                OpDef.Prepare prepare = this.opsByIndex.get(schemaField.getIndex());
                schemaField = schemaFactory.addSchemaField(schemaField.mapToIndex(0));
                // If an Op is registered that creates multiple fields only the last field
                // will have been linked to the op.
                if (prepare != null) {
                    opsByIndex.put(schemaField.getIndex(), prepare);
                }
            }
        } else {
            schemaFactory = this.schemaFactory;
            opsByIndex = this.opsByIndex;
        }

        WriteSchema schema = WriteSchema.from(schemaFactory.toSchema());

        DataFactory dataFactory = dataFactoryProvider.factoryFor(schema);

        List<BiConsumer<DidoData, WritableData>> ops = new ArrayList<>(schema.lastIndex());

        for (OpDef.Prepare prepare : opsByIndex.values()) {
            ops.add(prepare.create(schema));
        }

        return new TransformImpl(schema, dataFactory, ops);
    }

    static class TransformImpl implements DidoTransform {

        private final DataSchema schema;

        private final DataFactory dataFactory;

        private final List<BiConsumer<DidoData, WritableData>> ops;

        TransformImpl(DataSchema schema, DataFactory dataFactory, List<BiConsumer<DidoData, WritableData>> ops) {
            this.schema = schema;
            this.dataFactory = dataFactory;
            this.ops = ops;
        }

        @Override
        public DataSchema getResultantSchema() {
            return schema;
        }

        @Override
        public DidoData apply(DidoData data) {
            WritableData writableData = dataFactory.getWritableData();
            for (BiConsumer<DidoData, WritableData> op : ops) {
                op.accept(data, writableData);
            }
            return dataFactory.toData();
        }
    }
}

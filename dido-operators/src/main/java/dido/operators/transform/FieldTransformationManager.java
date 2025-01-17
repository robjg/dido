package dido.operators.transform;

import dido.data.NoSuchFieldException;
import dido.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class FieldTransformationManager {

    private final NavigableMap<Integer, SchemaField> indexFields = new TreeMap<>();

    private final NavigableMap<Integer, OpDef.Prepare> indexFactories = new TreeMap<>();

    private final List<SchemaField> extraFields = new ArrayList<>();

    private final List<OpDef.Prepare> extraFactories = new ArrayList<>();

    private final DataSchema incomingSchema;

    private Consumer<OpDef.Prepare> factoryConsumer;

    private final SchemaSetter schemaSetter = new SchemaSetter() {
        @Override
        public void addField(SchemaField schemaField) {
            int oldIndex = incomingSchema.getIndexNamed(schemaField.getName());

            if (oldIndex != 0) {
                indexFields.remove(oldIndex);
                indexFactories.remove(oldIndex);
            }

            int newIndex = schemaField.getIndex();

            if (newIndex == 0) {
                extraFields.add(schemaField);
                factoryConsumer = extraFactories::add;
            }
            else {
                indexFields.put(newIndex, schemaField);
                factoryConsumer = factory -> {
                    indexFactories.put(newIndex, factory);
                    // reset the consumer in case the next operation doesn't set
                    // a schema field.
                    factoryConsumer = extraFactories::add;
                };
            }
        }

        @Override
        public void removeField(SchemaField schemaField) {

            int oldIndex = incomingSchema.getIndexNamed(schemaField.getName());

            if (oldIndex == 0) {
                throw new NoSuchFieldException(schemaField.getName(), incomingSchema);
            }

            indexFields.remove(oldIndex);
            indexFactories.remove(oldIndex);

            factoryConsumer = factory-> {
                // Ignore this factory and reset the consumer in case the next operation
                // doesn't set a schema field.
                factoryConsumer = extraFactories::add;
            };
        }
    };

    protected FieldTransformationManager(DataSchema incomingSchema) {
        this.incomingSchema = incomingSchema;
    }

    public static <D extends DidoData>
    FieldTransformationManager forSchema(DataSchema incomingSchema) {
        return new FieldTransformationManager(incomingSchema);
    }

    public static <D extends DidoData>
    FieldTransformationManager forSchemaWithCopy(DataSchema incomingSchema) {

        FieldTransformationManager transformationManager =
                new FieldTransformationManager(incomingSchema);

        for (SchemaField field : incomingSchema.getSchemaFields()) {

            transformationManager.addOperation(FieldOps.copyAt(field.getIndex()));
        }

        return transformationManager;
    }

    public void addOperation(OpDef operation) {

        OpDef.Prepare factory = operation.prepare(incomingSchema, schemaSetter);
        factoryConsumer.accept(factory);
    }

    public DidoTransform createTransformation(DataFactoryProvider dataFactoryProvider) {

        SchemaFactory schemaFactory = dataFactoryProvider.getSchemaFactory();

        int index = 0;
        for (SchemaField schemaField : indexFields.values()) {
            schemaFactory.addSchemaField(schemaField.mapToIndex(++index));
        }
        for (SchemaField schemaField : extraFields) {
            SchemaField newField = schemaField.mapToIndex(++index);
            schemaFactory.addSchemaField(newField);
        }

        WriteSchema newSchema = WriteSchema.from(schemaFactory.toSchema());

        DataFactory dataFactory = dataFactoryProvider.factoryFor(newSchema);

        List<BiConsumer<DidoData, WritableData>> fieldOperations = new ArrayList<>(newSchema.lastIndex());
        for (OpDef.Prepare operationFactory : indexFactories.values()) {
            fieldOperations.add(operationFactory.create(newSchema));
        }
        for (OpDef.Prepare operationFactory : extraFactories) {
            fieldOperations.add(operationFactory.create(newSchema));
        }

        return new TransformImpl(fieldOperations, newSchema, dataFactory);
    }

    static class TransformImpl implements DidoTransform {

        private final List<BiConsumer<DidoData, WritableData>> operationList;

        private final WriteSchema schema;

        private final DataFactory dataFactory;

        TransformImpl(List<BiConsumer<DidoData, WritableData>> operationList,
                      WriteSchema schema,
                      DataFactory dataFactory) {
            this.operationList = operationList;
            this.schema = schema;
            this.dataFactory = dataFactory;
        }

        @Override
        public WriteSchema getResultantSchema() {
            return schema;
        }

        @Override
        public DidoData apply(DidoData data) {

            WritableData writableData = dataFactory.getWritableData();

            operationList.forEach(consumer -> consumer.accept(data, writableData));

            return dataFactory.toData();
        }
    }
}

package dido.operators.transform;

import dido.data.NoSuchFieldException;
import dido.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Consumer;

public class FieldTransformationManager<D extends DidoData> {

    private final NavigableMap<Integer, SchemaField> indexFields = new TreeMap<>();

    private final NavigableMap<Integer, TransformerFactory> indexFactories = new TreeMap<>();

    private final List<SchemaField> extraFields = new ArrayList<>();

    private final List<TransformerFactory> extraFactories = new ArrayList<>();

    private final ReadableSchema incomingSchema;

    private final WritableSchemaFactory<D> schemaFactory;

    private Consumer<TransformerFactory> factoryConsumer;

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

    protected FieldTransformationManager(ReadableSchema incomingSchema,
                                         WritableSchemaFactory<D> schemaFactory) {
        this.incomingSchema = incomingSchema;
        this.schemaFactory = schemaFactory;
    }

    public static <D extends DidoData>
    FieldTransformationManager<D> forWriteableSchema(WritableSchema<D> incomingSchema) {
        return new FieldTransformationManager<>(incomingSchema, incomingSchema.newSchemaFactory());
    }

    public static <D extends DidoData>
    FieldTransformationManager<D> forSchema(ReadableSchema incomingSchema,
                                            WritableSchemaFactory<D> schemaFactory) {
        return new FieldTransformationManager<>(incomingSchema, schemaFactory);
    }

    public static <D extends DidoData>
    FieldTransformationManager<D> forWritableSchemaWithCopy(WritableSchema<D> incomingSchema) {
        return forSchemaWithCopy(incomingSchema, incomingSchema.newSchemaFactory());
    }

    public static <D extends DidoData>
    FieldTransformationManager<D> forSchemaWithCopy(ReadableSchema incomingSchema,
                                            WritableSchemaFactory<D> schemaFactory) {

        FieldTransformationManager<D> transformationManager =
                new FieldTransformationManager<>(incomingSchema, schemaFactory);

        for (SchemaField field : incomingSchema.getSchemaFields()) {

            transformationManager.addOperation(FieldOperations.copyAt(field.getIndex()));
        }

        return transformationManager;
    }

    public void addOperation(TransformerDefinition operation) {

        TransformerFactory factory = operation.define(incomingSchema, schemaSetter);
        factoryConsumer.accept(factory);
    }

    public Transformation<D> createTransformation() {

        int index = 0;
        for (SchemaField schemaField : indexFields.values()) {
            schemaFactory.addSchemaField(schemaField.mapToIndex(++index));
        }
        for (SchemaField schemaField : extraFields) {
            SchemaField newField = schemaField.mapToIndex(++index);
            schemaFactory.addSchemaField(newField);
        }

        WritableSchema<D> newSchema = schemaFactory.toSchema();

        DataFactory<D> dataFactory = newSchema.newDataFactory();

        List<Consumer<DidoData>> fieldOperations = new ArrayList<>(newSchema.lastIndex());
        for (TransformerFactory operationFactory : indexFactories.values()) {
            fieldOperations.add(operationFactory.create(dataFactory));
        }
        for (TransformerFactory operationFactory : extraFactories) {
            fieldOperations.add(operationFactory.create(dataFactory));
        }

        return new TransformImpl(fieldOperations, newSchema, dataFactory);
    }

    class TransformImpl implements Transformation<D> {

        private final List<Consumer<DidoData>> operationList;

        private final WritableSchema<D> schema;

        private final DataFactory<D> dataFactory;

        TransformImpl(List<Consumer<DidoData>> operationList, WritableSchema<D> schema, DataFactory<D> dataFactory) {
            this.operationList = operationList;
            this.schema = schema;
            this.dataFactory = dataFactory;
        }

        @Override
        public WritableSchema<D> getResultantSchema() {
            return schema;
        }

        @Override
        public D apply(DidoData data) {

            operationList.forEach(consumer -> consumer.accept(data));

            return dataFactory.toData();
        }
    }
}

package dido.data.operators;

import dido.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class FieldTransformationManager<D extends DidoData> {

    private final NavigableMap<Integer, FieldAndOpFactory> operationsByIndex = new TreeMap<>();

    private final DataSchema incomingSchema;

    private final WritableSchemaFactory<D> schemaFactory;

    protected FieldTransformationManager(DataSchema incomingSchema,
                                         WritableSchemaFactory<D> schemaFactory) {
        this.incomingSchema = incomingSchema;
        this.schemaFactory = schemaFactory;
    }

    public static <D extends DidoData>
    FieldTransformationManager<D> forTransformableSchema(WritableSchema<D> writableSchema) {
        return new FieldTransformationManager<>(writableSchema, writableSchema.newSchemaFactory());
    }

    public static <D extends DidoData, S extends WritableSchema<D>>
    FieldTransformationManager<D> forSchema(DataSchema transformableSchema,
                                               WritableSchemaFactory<D> schemaFactory) {
        return new FieldTransformationManager<>(transformableSchema, schemaFactory);
    }

    public static <D extends DidoData, S extends WritableSchema<D>>
    FieldTransformationManager<D> forSchemaWithCopy(WritableSchema<D> writableSchema) {
        FieldTransformationManager<D> manager = new FieldTransformationManager<>(writableSchema,
                writableSchema.newSchemaFactory());
        for (int index = writableSchema.firstIndex(); index > 0; index = writableSchema.nextIndex(index)) {

            manager.addOperation(FieldOperations.copyAt(index));
        }

        return manager;
    }

    static class FieldAndOpFactory {

        private final SchemaField schemaField;

        private final FieldOperationFactory operationFactory;

        FieldAndOpFactory(SchemaField schemaField, FieldOperationFactory operationFactory) {
            this.schemaField = schemaField;
            this.operationFactory = operationFactory;
        }
    }

    class Manager implements FieldOperationManager {

        @Override
        public void addOperation(SchemaField schemaField, FieldOperationFactory fieldOperationFactory) {
            int index = schemaField.getIndex();
            if (index == 0) {
                index = operationsByIndex.lastKey() + 1;
            }
            operationsByIndex.put(index, new FieldAndOpFactory(schemaField, fieldOperationFactory));
        }

        @Override
        public void removeField(SchemaField schemaField) {
            operationsByIndex.remove(schemaField.getIndex());
        }
    }

    public void addOperation(FieldOperationDefinition operation) {
        operation.define(incomingSchema, new Manager());
    }

    public Transformation<D> createTransformation() {

        int index = 0;
        for (FieldAndOpFactory fieldAndOpFactory : operationsByIndex.values()) {
            SchemaField newField = fieldAndOpFactory.schemaField.mapToIndex(++index);
            schemaFactory.addSchemaField(newField);
        }

        WritableSchema<D> newSchema = schemaFactory.toSchema();

        DataFactory<D> dataFactory = newSchema.newDataFactory();

        List<FieldOperation> fieldOperations = new ArrayList<>(operationsByIndex.size());
        for (FieldAndOpFactory fieldAndOpFactory : operationsByIndex.values()) {
            fieldOperations.add(fieldAndOpFactory.operationFactory.create(dataFactory));
        }

        return new TransformImpl(fieldOperations, newSchema, dataFactory);
    }


    class TransformImpl implements  Transformation<D> {

        private final List<FieldOperation> operationList;

        private final WritableSchema<D> schema;

        private final DataFactory<D> dataFactory;

        TransformImpl(List<FieldOperation> operationList, WritableSchema<D> schema, DataFactory<D> dataFactory) {
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

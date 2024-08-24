package dido.operators.transform;

import dido.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class FieldTransformationManager<D extends DidoData> {

    private final NavigableMap<Integer, FieldAndOpFactory> copyOperations;

    private final List<SchemaField> schemaFields = new ArrayList<>();

    private final List<FieldOperationFactory> operationFactories = new ArrayList<>();

    private final DataSchema incomingSchema;

    private final WritableSchemaFactory<D> schemaFactory;

    protected FieldTransformationManager(DataSchema incomingSchema,
                                         WritableSchemaFactory<D> schemaFactory) {
        this(incomingSchema, schemaFactory, new TreeMap<>());
    }

    protected FieldTransformationManager(DataSchema incomingSchema,
                                         WritableSchemaFactory<D> schemaFactory,
                                         NavigableMap<Integer, FieldAndOpFactory> copyOperations) {
        this.incomingSchema = incomingSchema;
        this.schemaFactory = schemaFactory;
        this.copyOperations = copyOperations;
    }

    public static <D extends DidoData>
    FieldTransformationManager<D> forTransformableSchema(WritableSchema<D> incomingSchema) {
        return new FieldTransformationManager<>(incomingSchema, incomingSchema.newSchemaFactory());
    }

    public static <D extends DidoData>
    FieldTransformationManager<D> forSchema(DataSchema incomingSchema,
                                               WritableSchemaFactory<D> schemaFactory) {
        return new FieldTransformationManager<>(incomingSchema, schemaFactory);
    }

    public static <D extends DidoData>
    FieldTransformationManager<D> forSchemaWithCopy(WritableSchema<D> incomingSchema) {

        WritableSchemaFactory<D> schemaFactory = incomingSchema.newSchemaFactory();

        SchemaSetter  schemaSetter = new SchemaSetter() {

            @Override
            public void addField(SchemaField schemaField) {
                // Ignored
            }

            @Override
            public void removeField(SchemaField schemaField) {
                throw new IllegalStateException("Should not be called for copy");
            }
        };

        NavigableMap<Integer, FieldAndOpFactory> copyOperations = new TreeMap<>();

        for (SchemaField field: incomingSchema.getSchemaFields()) {

            FieldOperationFactory copy = FieldOperations.copyAt(field.getIndex())
                    .define(incomingSchema, schemaSetter);

            copyOperations.put(field.getIndex(), new FieldAndOpFactory(field, copy));
        }

        return new FieldTransformationManager<>(incomingSchema, schemaFactory, copyOperations);
    }

    protected static class FieldAndOpFactory {

        private final SchemaField schemaField;

        private final FieldOperationFactory operationFactory;

        FieldAndOpFactory(SchemaField schemaField, FieldOperationFactory operationFactory) {
            this.schemaField = schemaField;
            this.operationFactory = operationFactory;
        }
    }

    public void addOperation(FieldOperationDefinition operation) {

        SchemaSetter  schemaFactory = new SchemaSetter() {
            @Override
            public void addField(SchemaField schemaField) {
                copyOperations.remove(incomingSchema.getIndexNamed(schemaField.getName()));
                schemaFields.add(schemaField);
            }

            @Override
            public void removeField(SchemaField schemaField) {
                copyOperations.remove(incomingSchema.getIndexNamed(schemaField.getName()));
            }
        };

        FieldOperationFactory factory = operation.define(incomingSchema, schemaFactory);
        operationFactories.add(factory);
    }

    public Transformation<D> createTransformation() {

        int index = 0;
        for (FieldAndOpFactory fieldAndOpFactory : copyOperations.values()) {
            SchemaField newField = fieldAndOpFactory.schemaField.mapToIndex(++index);
            schemaFactory.addSchemaField(newField);
        }
        for (SchemaField schemaField : schemaFields) {
            SchemaField newField = schemaField.mapToIndex(++index);
            schemaFactory.addSchemaField(newField);
        }

        WritableSchema<D> newSchema = schemaFactory.toSchema();

        DataFactory<D> dataFactory = newSchema.newDataFactory();

        List<FieldOperation> fieldOperations = new ArrayList<>(newSchema.lastIndex());
        for (FieldAndOpFactory fieldAndOpFactory : copyOperations.values()) {
            fieldOperations.add(fieldAndOpFactory.operationFactory.create(dataFactory));
        }
        for (FieldOperationFactory operationFactory : operationFactories) {
            fieldOperations.add(operationFactory.create(dataFactory));
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

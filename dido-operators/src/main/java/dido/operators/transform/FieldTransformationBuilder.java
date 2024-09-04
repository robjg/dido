package dido.operators.transform;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.WritableSchema;
import dido.data.WritableSchemaFactory;

public class FieldTransformationBuilder<D extends DidoData> {

    private final FieldTransformationManager<D> fieldOperationTransformation;

    private FieldTransformationBuilder(FieldTransformationManager<D> fieldTransformationManager) {
        this.fieldOperationTransformation = fieldTransformationManager;
    }

    public static <D extends DidoData>
    FieldTransformationBuilder<D> forSchema(DataSchema incomingSchema, WritableSchemaFactory<D> schemaFactory) {

        return new FieldTransformationBuilder<>(
                FieldTransformationManager.forSchema(incomingSchema, schemaFactory));
    }

    public static <D extends DidoData>
    FieldTransformationBuilder<D> forWritableSchema(WritableSchema<D> writableSchema) {

        return new FieldTransformationBuilder<>(FieldTransformationManager.forWriteableSchema(writableSchema));
    }

    public static <D extends DidoData, S extends WritableSchema<D>>
    FieldTransformationBuilder<D> forWritableSchemaWithCopy(WritableSchema<D> writableSchema) {

        return new FieldTransformationBuilder<>(FieldTransformationManager.forWritableSchemaWithCopy(writableSchema));
    }

    public static <D extends DidoData, S extends WritableSchema<D>>
    FieldTransformationBuilder<D> forSchemaWithCopy(DataSchema incomingSchema, WritableSchemaFactory<D> schemaFactory) {

        return new FieldTransformationBuilder<>(FieldTransformationManager
                .forSchemaWithCopy(incomingSchema, schemaFactory));
    }

    public FieldTransformationBuilder<D> addFieldOperation(TransformerDefinition transformerDefinition) {
        fieldOperationTransformation.addOperation(transformerDefinition);
        return this;
    }

    public Transformation<D> build() {
        return fieldOperationTransformation.createTransformation();
    }
}

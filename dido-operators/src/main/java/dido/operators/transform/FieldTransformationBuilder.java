package dido.operators.transform;

import dido.data.DataFactoryProvider;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.WritableSchema;

public class FieldTransformationBuilder<D extends DidoData> {

    private final FieldTransformationManager<D> fieldOperationTransformation;

    private FieldTransformationBuilder(FieldTransformationManager<D> fieldTransformationManager) {
        this.fieldOperationTransformation = fieldTransformationManager;
    }

    public static <D extends DidoData>
    FieldTransformationBuilder<D> forSchema(DataSchema incomingSchema, DataFactoryProvider<D> dataFactoryProvider) {

        return new FieldTransformationBuilder<>(
                FieldTransformationManager.forSchema(incomingSchema,
                        dataFactoryProvider.getSchemaFactory()));
    }

    public static <D extends DidoData>
    FieldTransformationBuilder<D> forTransformableSchema(WritableSchema<D> writableSchema) {

        return new FieldTransformationBuilder<>(FieldTransformationManager.forTransformableSchema(writableSchema));
    }

    public static <D extends DidoData, S extends WritableSchema<D>>
    FieldTransformationBuilder<D> forSchemaWithCopy(WritableSchema<D> writableSchema) {

        return new FieldTransformationBuilder<>(FieldTransformationManager.forSchemaWithCopy(writableSchema));
    }

    public FieldTransformationBuilder<D> addFieldOperation(TransformerDefinition transformerDefinition) {
        fieldOperationTransformation.addOperation(transformerDefinition);
        return this;
    }

    public Transformation<D> build() {
        return fieldOperationTransformation.createTransformation();
    }
}

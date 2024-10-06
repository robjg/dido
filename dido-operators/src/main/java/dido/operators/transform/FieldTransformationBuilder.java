package dido.operators.transform;

import dido.data.DataFactoryProvider;
import dido.data.DidoData;
import dido.data.ReadSchema;

public class FieldTransformationBuilder<D extends DidoData> {

    private final FieldTransformationManager<D> fieldOperationTransformation;

    private final DataFactoryProvider<D> dataFactoryProvider;

    private FieldTransformationBuilder(FieldTransformationManager<D> fieldTransformationManager,
                                       DataFactoryProvider<D> dataFactoryProvider) {
        this.fieldOperationTransformation = fieldTransformationManager;
        this.dataFactoryProvider = dataFactoryProvider;
    }

    public static class WithFactory<D extends DidoData> {

        private final DataFactoryProvider<D> dataFactoryProvider;

        public WithFactory(DataFactoryProvider<D> dataFactoryProvider) {
            this.dataFactoryProvider = dataFactoryProvider;
        }

        public FieldTransformationBuilder<D> forSchema(ReadSchema incomingSchema) {

            return new FieldTransformationBuilder<>(
                    FieldTransformationManager.forSchema(incomingSchema),
                    dataFactoryProvider);
        }

        public FieldTransformationBuilder<D> forSchemaWithCopy(ReadSchema incomingSchema) {

            return new FieldTransformationBuilder<>(FieldTransformationManager
                    .forSchemaWithCopy(incomingSchema), dataFactoryProvider);
        }
    }

    public static <D extends DidoData>
    WithFactory<D> withFactory(DataFactoryProvider<D> dataFactoryProvider) {
        return new WithFactory<>(dataFactoryProvider);
    }

    public FieldTransformationBuilder<D> addFieldOperation(TransformerDefinition transformerDefinition) {
        fieldOperationTransformation.addOperation(transformerDefinition);
        return this;
    }

    public Transformation<D> build() {
        return fieldOperationTransformation.createTransformation(dataFactoryProvider);
    }
}

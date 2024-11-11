package dido.operators.transform;

import dido.data.DataFactoryProvider;
import dido.data.DataSchema;

public class FieldTransformationBuilder {

    private final FieldTransformationManager fieldOperationTransformation;

    private final DataFactoryProvider dataFactoryProvider;

    private FieldTransformationBuilder(FieldTransformationManager fieldTransformationManager,
                                       DataFactoryProvider dataFactoryProvider) {
        this.fieldOperationTransformation = fieldTransformationManager;
        this.dataFactoryProvider = dataFactoryProvider;
    }

    public static class WithFactory {

        private final DataFactoryProvider dataFactoryProvider;

        public WithFactory(DataFactoryProvider dataFactoryProvider) {
            this.dataFactoryProvider = dataFactoryProvider;
        }

        public FieldTransformationBuilder forSchema(DataSchema incomingSchema) {

            return new FieldTransformationBuilder(
                    FieldTransformationManager.forSchema(incomingSchema),
                    dataFactoryProvider);
        }

        public FieldTransformationBuilder forSchemaWithCopy(DataSchema incomingSchema) {

            return new FieldTransformationBuilder(FieldTransformationManager
                    .forSchemaWithCopy(incomingSchema), dataFactoryProvider);
        }
    }

    public static WithFactory withFactory(DataFactoryProvider dataFactoryProvider) {
        return new WithFactory(dataFactoryProvider);
    }

    public FieldTransformationBuilder addFieldOperation(TransformerDefinition transformerDefinition) {
        fieldOperationTransformation.addOperation(transformerDefinition);
        return this;
    }

    public Transformation build() {
        return fieldOperationTransformation.createTransformation(dataFactoryProvider);
    }
}

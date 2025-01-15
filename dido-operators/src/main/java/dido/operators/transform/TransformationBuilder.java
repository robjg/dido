package dido.operators.transform;

import dido.data.DataFactoryProvider;
import dido.data.DataSchema;

import java.util.Objects;

public class TransformationBuilder {

    private final FieldTransformationManager fieldOperationTransformation;

    private final DataFactoryProvider dataFactoryProvider;

    private TransformationBuilder(FieldTransformationManager fieldTransformationManager,
                                  DataFactoryProvider dataFactoryProvider) {
        this.fieldOperationTransformation = fieldTransformationManager;
        this.dataFactoryProvider = dataFactoryProvider;
    }

    public static class Settings {

        private DataFactoryProvider dataFactoryProvider;

        private boolean copy;

        public Settings dataFactoryProvider(DataFactoryProvider dataFactoryProvider) {
            this.dataFactoryProvider = dataFactoryProvider;
            return this;
        }

        public Settings copy(boolean copy) {
            this.copy = copy;
            return this;
        }

        public TransformationBuilder forSchema(DataSchema incomingSchema) {

            DataFactoryProvider dataFactoryProvider = Objects.requireNonNullElse(
                    this.dataFactoryProvider, DataFactoryProvider.newInstance());

            if (copy) {
                return new TransformationBuilder(
                        FieldTransformationManager.forSchemaWithCopy(incomingSchema),
                        dataFactoryProvider);
            }
            else {
                return new TransformationBuilder(
                        FieldTransformationManager.forSchema(incomingSchema),
                        dataFactoryProvider);

            }
        }
    }

    public static Settings with() {
        return new Settings();
    }

    public static class WithFactory {

        private final DataFactoryProvider dataFactoryProvider;

        public WithFactory(DataFactoryProvider dataFactoryProvider) {
            this.dataFactoryProvider = dataFactoryProvider;
        }

        public TransformationBuilder forSchema(DataSchema incomingSchema) {

            return new TransformationBuilder(
                    FieldTransformationManager.forSchema(incomingSchema),
                    dataFactoryProvider);
        }

        public TransformationBuilder forSchemaWithCopy(DataSchema incomingSchema) {

            return new TransformationBuilder(FieldTransformationManager
                    .forSchemaWithCopy(incomingSchema), dataFactoryProvider);
        }
    }

    public static WithFactory withFactory(DataFactoryProvider dataFactoryProvider) {
        return new WithFactory(dataFactoryProvider);
    }

    public TransformationBuilder addOp(TransformerDefinition transformerDefinition) {
        fieldOperationTransformation.addOperation(transformerDefinition);
        return this;
    }

    public TransformationBuilder setNamed(String name, Object value) {
        fieldOperationTransformation.addOperation(FieldOps.setNamed(name, value));
        return this;
    }


    public Transformation build() {
        return fieldOperationTransformation.createTransformation(dataFactoryProvider);
    }
}

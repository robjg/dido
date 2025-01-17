package dido.operators.transform;

import dido.data.DataFactoryProvider;
import dido.data.DataSchema;

import java.util.Objects;

/**
 * Provides a Builder for creating {@link DidoTransform}s from {@link OpDef}s.
 */
public class OpTransformBuilder {

    private final FieldTransformationManager fieldOperationTransformation;

    private final DataFactoryProvider dataFactoryProvider;

    private OpTransformBuilder(FieldTransformationManager fieldTransformationManager,
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

        public OpTransformBuilder forSchema(DataSchema incomingSchema) {

            DataFactoryProvider dataFactoryProvider = Objects.requireNonNullElse(
                    this.dataFactoryProvider, DataFactoryProvider.newInstance());

            if (copy) {
                return new OpTransformBuilder(
                        FieldTransformationManager.forSchemaWithCopy(incomingSchema),
                        dataFactoryProvider);
            }
            else {
                return new OpTransformBuilder(
                        FieldTransformationManager.forSchema(incomingSchema),
                        dataFactoryProvider);

            }
        }
    }

    public static Settings with() {
        return new Settings();
    }

    public static OpTransformBuilder forSchema(DataSchema incomingSchema) {
        return with().forSchema(incomingSchema);
    }

    public static class WithFactory {

        private final DataFactoryProvider dataFactoryProvider;

        public WithFactory(DataFactoryProvider dataFactoryProvider) {
            this.dataFactoryProvider = dataFactoryProvider;
        }

        public OpTransformBuilder forSchema(DataSchema incomingSchema) {

            return new OpTransformBuilder(
                    FieldTransformationManager.forSchema(incomingSchema),
                    dataFactoryProvider);
        }

        public OpTransformBuilder forSchemaWithCopy(DataSchema incomingSchema) {

            return new OpTransformBuilder(FieldTransformationManager
                    .forSchemaWithCopy(incomingSchema), dataFactoryProvider);
        }
    }

    public static WithFactory withFactory(DataFactoryProvider dataFactoryProvider) {
        return new WithFactory(dataFactoryProvider);
    }

    public OpTransformBuilder addOp(OpDef opDef) {
        fieldOperationTransformation.addOperation(opDef);
        return this;
    }

    public OpTransformBuilder setNamed(String name, Object value) {
        fieldOperationTransformation.addOperation(FieldOps.setNamed(name, value));
        return this;
    }


    public DidoTransform build() {
        return fieldOperationTransformation.createTransformation(dataFactoryProvider);
    }
}

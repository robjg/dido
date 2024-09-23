package dido.operators.transform;

import dido.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Copies fields from one data item to another allowing for change of field names, indexes
 * and type.
 */
public class TransformationFactory implements Supplier<Function<DidoData, DidoData>> {

    private final List<TransformerDefinition> of = new ArrayList<>();

    /**
     * Strategy for creating the new schema for outgoing data. Defaults to merge.
     */
    private boolean withCopy;

    private DataFactoryProvider<?> dataFactoryProvider;

    @Override
    public Function<DidoData, DidoData> get() {

        return new TransformerFunctionInitial(this);
    }

    public void setOf(int index, TransformerDefinition transformer) {
        if (transformer == null) {
            of.remove(index);
        }
        else {
            of.add(index, transformer);
        }
    }

    public boolean getWithCopy() {
        return withCopy;
    }

    public void setWithCopy(boolean withCopy) {
        this.withCopy = withCopy;
    }

    public DataFactoryProvider<?> getDataFactoryProvider() {
        return dataFactoryProvider;
    }

    public void setDataFactoryProvider(DataFactoryProvider<?> dataFactoryProvider) {
        this.dataFactoryProvider = dataFactoryProvider;
    }

    static class TransformerFunctionInitial implements Function<DidoData, DidoData> {

        private final List<TransformerDefinition> transformerFactories;

        private final boolean withCopy;

        private final DataFactoryProvider<?> dataFactoryProvider;

        private Function<? super DidoData, ? extends DidoData> delegate;

        private ReadableSchema lastSchema;

        TransformerFunctionInitial(TransformationFactory config) {
            this.transformerFactories = new ArrayList<>(config.of);
            this.withCopy = config.withCopy;
            this.dataFactoryProvider = Objects.requireNonNullElseGet(config.dataFactoryProvider,
                    ArrayDataDataFactoryProvider::new);
        }

        @Override
        public DidoData apply(DidoData dataIn) {

            if (delegate == null || !dataIn.getSchema().equals(lastSchema)) {
                lastSchema = dataIn.getSchema();
                delegate = functionFor(transformerFactories, lastSchema, withCopy,
                        dataFactoryProvider.getSchemaFactory());
            }
            return delegate.apply(dataIn);
        }
    }

    static <D extends DidoData> Transformation<D> functionFor(List<TransformerDefinition> definitions,
                                                ReadableSchema schemaFrom,
                                                boolean withCopy,
                                                WritableSchemaFactory<D> writableSchemaFactory) {

        FieldTransformationManager<D> transformationManager = withCopy ?
                FieldTransformationManager.forSchemaWithCopy(schemaFrom, writableSchemaFactory) :
                FieldTransformationManager.forSchema(schemaFrom, writableSchemaFactory);

        for (TransformerDefinition definition : definitions) {

            transformationManager.addOperation(definition);
        }

        return transformationManager.createTransformation();
    }

}

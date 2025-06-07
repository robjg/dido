package dido.operators.transform;

import dido.data.ArrayDataDataFactoryProvider;
import dido.data.DataFactoryProvider;
import dido.data.DataSchema;
import dido.data.DidoData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @oddjob.description Copies fields from one data item to another allowing for change of field names, indexes
 * and type.
 *
 * For examples see {@link ValueSetFactory} and {@link ValueCopyFactory}
 */
public class TransformationFactory implements Supplier<Function<DidoData, DidoData>> {

    private final List<FieldWrite> of = new ArrayList<>();

    /**
     * @oddjob.description Copy existing fields before applying transformations.
     * @oddjob.required No, defaults to false.
     */
    private boolean withCopy;

    /**
     * @oddjob.description A factory for creating the new Data.
     * @oddjob.required No. Defaults to something reasonable.
     */
    private DataFactoryProvider dataFactoryProvider;

    @Override
    public Function<DidoData, DidoData> get() {

        return new TransformerFunctionInitial(this);
    }

    /**
     * @oddjob.description The field level transformations to apply.
     * @oddjob.required No. Will just copy or create empty data.
     *
     * @param index The index.
     * @param transformer The transformer.
     */
    public void setOf(int index, FieldWrite transformer) {
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

    public DataFactoryProvider getDataFactoryProvider() {
        return dataFactoryProvider;
    }

    public void setDataFactoryProvider(DataFactoryProvider dataFactoryProvider) {
        this.dataFactoryProvider = dataFactoryProvider;
    }

    static class TransformerFunctionInitial implements Function<DidoData, DidoData> {

        private final List<FieldWrite> transformerFactories;

        private final boolean withCopy;

        private final DataFactoryProvider dataFactoryProvider;

        private Function<? super DidoData, ? extends DidoData> delegate;

        private DataSchema lastSchema;

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
                        dataFactoryProvider);
            }
            return delegate.apply(dataIn);
        }
    }

    static DidoTransform functionFor(List<FieldWrite> definitions,
                                     DataSchema schemaFrom,
                                     boolean withCopy,
                                     DataFactoryProvider dataFactoryProvider) {

        WriteTransformBuilder transformationManager = WriteTransformBuilder.with()
                .existingFields(withCopy)
                .dataFactoryProvider(dataFactoryProvider)
                .forSchema(schemaFrom);

        for (FieldWrite definition : definitions) {

            transformationManager.addFieldWrite(definition);
        }

        return transformationManager.build();
    }

}

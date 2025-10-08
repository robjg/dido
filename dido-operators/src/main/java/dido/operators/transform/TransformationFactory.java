package dido.operators.transform;

import dido.data.DataFactoryProvider;
import dido.data.DataSchema;
import dido.data.DidoData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @oddjob.description Create a transformation of data by applying field transformations.
 * <p>
 * By default, only
 * fields resulting from a transformation will appear in the resultant data. If you include all other existing fields
 * in a transformation then set the {@code withExisting} property to {@code true}. If you wish to include most, but not
 * all fields, use the {@code withExisting} property in conjunction with the {@link ValueRemoveFactory} transformation.
 * <p>
 * The resultant data is by default, a view of the original data. Any functions on the data are applied every time
 * that field is read. This is the most performant approach for simple in-out pipelines. If the data is going to
 * be read many times, then set the {@code copy} property to  {@code true} so the operations are only performed once.
 * <p>
 * For examples see {@link ValueSetFactory}, {@link ValueCopyFactory} and {@link ValueRemoveFactory}
 */
public class TransformationFactory implements Supplier<Function<DidoData, DidoData>> {

    private final List<FieldView> of = new ArrayList<>();

    /**
     * @oddjob.description Include existing fields before applying transformations.
     * @oddjob.required No, defaults to false.
     */
    private boolean withExisting;

    /**
     * @oddjob.description If fields have been removed there will be wholes in
     * the schema where indices are missing. Setting this property to true
     * ensures the resultant schema has indices 1, 2, 3, etc.
     * @oddjob.required No, defaults to false.
     */
    private boolean reIndex;

    /**
     * @oddjob.description Include existing fields before applying transformations.
     * @oddjob.required No, defaults to false.
     */
    private boolean copy;

    /**
     * @oddjob.description A factory for creating the new Data. This is only applicable
     * if {@code copy} is true, as no new data is created when {code copy} is false.
     * @oddjob.required No. Defaults to something reasonable.
     */
    private DataFactoryProvider dataFactoryProvider;

    @Override
    public Function<DidoData, DidoData> get() {

        if (copy) {
            return new TransformerFunctionCopy(this);
        }
        else {
            return new TransformerFunctionView(this);
        }
    }

    /**
     * @param index       The index.
     * @param transformer The transformer.
     * @oddjob.description The field level transformations to apply.
     * @oddjob.required No. Will just copy or create empty data.
     */
    public void setOf(int index, FieldView transformer) {
        if (transformer == null) {
            of.remove(index);
        } else {
            of.add(index, transformer);
        }
    }

    public boolean isWithExisting() {
        return withExisting;
    }

    public void setWithExisting(boolean withExisting) {
        this.withExisting = withExisting;
    }

    public boolean isCopy() {
        return copy;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }

    public boolean isReIndex() {
        return reIndex;
    }

    public void setReIndex(boolean reIndex) {
        this.reIndex = reIndex;
    }

    public DataFactoryProvider getDataFactoryProvider() {
        return dataFactoryProvider;
    }

    public void setDataFactoryProvider(DataFactoryProvider dataFactoryProvider) {
        this.dataFactoryProvider = dataFactoryProvider;
    }

    abstract static class TransformerFunctionInitial implements DidoTransform {

        protected final List<FieldView> transformerFactories;

        protected final boolean withExisting;

        protected final boolean reIndex;

        private DataSchema lastSchema;

        private DidoTransform delegate;

        TransformerFunctionInitial(TransformationFactory config) {
            this.transformerFactories = new ArrayList<>(config.of);
            this.withExisting = config.withExisting;
            this.reIndex = config.reIndex;
        }


        @Override
        public DataSchema getResultantSchema() {
            return delegate == null ? null : delegate.getResultantSchema();
        }

        @Override
        public DidoData apply(DidoData dataIn) {
            if (delegate == null || !dataIn.getSchema().equals(lastSchema)) {
                lastSchema = dataIn.getSchema();
                delegate = createTransform(lastSchema);
            }
            return delegate.apply(dataIn);
        }

        abstract DidoTransform createTransform(DataSchema schema);
    }


    static class TransformerFunctionCopy extends TransformerFunctionInitial {

        private final DataFactoryProvider dataFactoryProvider;

        TransformerFunctionCopy(TransformationFactory config) {
            super(config);
            this.dataFactoryProvider = Objects.requireNonNullElseGet(config.dataFactoryProvider,
                    DataFactoryProvider::newInstance);
        }

        @Override
        DidoTransform createTransform(DataSchema lastSchema) {
            return functionForCopy(transformerFactories, lastSchema,
                    withExisting, reIndex, dataFactoryProvider);
        }

    }

    static class TransformerFunctionView extends TransformerFunctionInitial {

        TransformerFunctionView(TransformationFactory config) {
            super(config);
        }

        @Override
        DidoTransform createTransform(DataSchema schema) {
            return functionForView(transformerFactories, schema,
                    withExisting, reIndex);
        }
    }

    static DidoTransform functionForView(List<FieldView> definitions,
                                         DataSchema schemaFrom,
                                         boolean withExisting,
                                         boolean reIndex) {

        ViewTransformBuilder transformationManager = ViewTransformBuilder.with()
                .existingFields(withExisting)
                .reIndex(reIndex)
                .forSchema(schemaFrom);

        for (FieldView definition : definitions) {

            transformationManager.addFieldView(definition);
        }

        return transformationManager.build();
    }

    static DidoTransform functionForCopy(List<FieldView> definitions,
                                         DataSchema schemaFrom,
                                         boolean withExisting,
                                         boolean reIndex,
                                         DataFactoryProvider dataFactoryProvider) {

        WriteTransformBuilder transformationManager = WriteTransformBuilder.with()
                .existingFields(withExisting)
                .reIndex(reIndex)
                .dataFactoryProvider(dataFactoryProvider)
                .forSchema(schemaFrom);

        for (FieldView definition : definitions) {

            transformationManager.addFieldView(definition);
        }

        return transformationManager.build();
    }

}

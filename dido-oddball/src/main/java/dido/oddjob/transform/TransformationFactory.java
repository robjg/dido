package dido.oddjob.transform;

import dido.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Copies fields from one data item to another allowing for change of field names, indexes
 * and type.
 */
public class TransformationFactory implements Supplier<Function<DidoData, DidoData>> {

    private final List<TransformerDefinition> of = new ArrayList<>();

    /**
     * Strategy for creating the new schema for outgoing data. Defaults to merge.
     */
    private SchemaStrategy strategy;

    @Override
    public Function<DidoData, DidoData> get() {

        return new TransformerFunctionInitial(of, strategy);
    }

    public void setOf(int index, TransformerDefinition transformer) {
        if (transformer == null) {
            of.remove(index);
        }
        else {
            of.add(index, transformer);
        }
    }

    public SchemaStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(SchemaStrategy strategy) {
        this.strategy = strategy;
    }

    static class TransformerFunctionInitial implements Function<DidoData, DidoData> {

        private final List<TransformerDefinition> transformerFactories;

        private final SchemaStrategy strategy;

        private Function<? super DidoData, ? extends DidoData> delegate;

        private DataSchema lastSchema;

        TransformerFunctionInitial(List<TransformerDefinition> transformerFactories, SchemaStrategy strategy) {
            this.transformerFactories = new ArrayList<>(transformerFactories);
            this.strategy = strategy;
        }

        @Override
        public DidoData apply(DidoData dataIn) {

            if (delegate == null || !dataIn.getSchema().equals(lastSchema)) {
                lastSchema = dataIn.getSchema();
                delegate = functionFor(transformerFactories, lastSchema, strategy);
            }
            return delegate.apply(dataIn);
        }
    }

    static TransformerFunctionKnown functionFor(List<TransformerDefinition> factories,
                                                DataSchema schemaFrom,
                                                SchemaStrategy partial) {

        List<SchemaField> newFields = new ArrayList<>();

        SchemaSetter schemaSetter = newFields::add;

        List<TransformerFactory> transformerFactories = new ArrayList<>(factories.size());

        for (TransformerDefinition factory : factories) {

            transformerFactories.add(factory.define(schemaFrom, schemaSetter));
        }

        SchemaStrategy schemaStrategy = Objects.requireNonNullElse(partial, SchemaStrategy.MERGE);

        DataSchema schema = schemaStrategy.newSchemaFrom(schemaFrom,
                newFields,
                i -> transformerFactories.add(into -> {
                    Setter setter = into.getSetterAt(i);
                    return in -> setter.set(in.getAt(i));
                }));

        DataFactory<ArrayData> dataFactory = new ArrayDataDataFactoryProvider().provideFactory(schema);

        return new TransformerFunctionKnown(dataFactory, transformerFactories);
    }

    static class TransformerFunctionKnown implements Function<DidoData, DidoData> {

        private final DataFactory<? extends DidoData> dataFactory;

        private final List<Consumer<DidoData>> transformers;

        TransformerFunctionKnown(DataFactory<? extends DidoData> dataFactory, List<TransformerFactory> transformerFactories) {
            this.dataFactory = dataFactory;
            this.transformers = transformerFactories.stream().map(t -> t.create(dataFactory))
                    .collect(Collectors.toList());
        }

        @Override
        public DidoData apply(DidoData dataIn) {
            for (Consumer<DidoData> transformer : transformers) {
                transformer.accept(dataIn);
            }

            return dataFactory.toData();
        }
    }
}

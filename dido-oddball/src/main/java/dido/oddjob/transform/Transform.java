package dido.oddjob.transform;

import dido.data.*;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Copies fields from one data item to another allowing for change of field names, indexes
 * and type.
 */
public class Transform implements ValueFactory<Function<DidoData, DidoData>> {

    private final ListSetterHelper<TransformerFactory> of = new ListSetterHelper<>();

    /**
     * Strategy for creating the new schema for outgoing data. Defaults to merge.
     */
    private SchemaStrategy strategy;

    @Override
    public Function<DidoData, DidoData> toValue() {

        return new TransformerFunctionInitial(of.getList(), strategy);
    }

    public void setOf(int index, TransformerFactory transformer) {
        this.of.set(index, transformer);
    }

    public SchemaStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(SchemaStrategy strategy) {
        this.strategy = strategy;
    }

    static class TransformerFunctionInitial implements Function<DidoData, DidoData> {

        private final List<TransformerFactory> transformerFactories;

        private final SchemaStrategy strategy;

        private Function<? super DidoData, ? extends DidoData> delegate;

        private DataSchema lastSchema;

        TransformerFunctionInitial(List<TransformerFactory> transformerFactories, SchemaStrategy strategy) {
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

    static TransformerFunctionKnown functionFor(List<TransformerFactory> factories,
                                                DataSchema schemaFrom,
                                                SchemaStrategy partial) {

        List<SchemaField> newFields = new ArrayList<>();

        SchemaSetter schemaSetter = newFields::add;

        List<Transformer> transformers = new ArrayList<>(factories.size());

        for (TransformerFactory factory : factories) {

            transformers.add(factory.create(schemaFrom, schemaSetter));
        }

        SchemaStrategy schemaStrategy = Objects.requireNonNullElse(partial, SchemaStrategy.MERGE);

        DataSchema schema = schemaStrategy.newSchemaFrom(schemaFrom,
                newFields,
                i -> transformers.add(into -> {
                    Setter setter = into.getSetterAt(i);
                    return in -> setter.set(in.getAt(i));
                }));

        DataFactory<ArrayData> dataFactory = new ArrayDataDataFactoryProvider().provideFactory(schema);

        return new TransformerFunctionKnown(dataFactory, transformers);
    }

    static class TransformerFunctionKnown implements Function<DidoData, DidoData> {

        private final DataFactory<? extends DidoData> dataFactory;

        private final List<Consumer<DidoData>> transformers;

        TransformerFunctionKnown(DataFactory<? extends DidoData> dataFactory, List<Transformer> transformers) {
            this.dataFactory = dataFactory;
            this.transformers = transformers.stream().map(t -> t.transform(dataFactory))
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

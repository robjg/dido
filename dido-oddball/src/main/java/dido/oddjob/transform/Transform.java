package dido.oddjob.transform;

import dido.data.DataSchema;
import dido.data.DidoData;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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

        int position = 0;

        List<SchemaFieldOptions> newFields = new ArrayList<>();

        SchemaSetter schemaSetter = (index, field, fieldType)
                -> newFields.add(SchemaFieldOptions.of(index, field, fieldType));

        List<Transformer> transformers = new ArrayList<>(factories.size());

        for (TransformerFactory factory : factories) {

            transformers.add(factory.create(++position, schemaFrom, schemaSetter));
        }

        SchemaStrategy schemaStrategy = Objects.requireNonNullElse(partial, SchemaStrategy.MERGE);

        DataSchema schema = schemaStrategy.newSchemaFrom(schemaFrom,
                newFields,
                i -> transformers.add((in, setter) -> setter.setAt(i, in.getAt(i))));

        DataFactory dataFactory = new ArrayDataSetterProvider().provideSetter(schema);

        return new TransformerFunctionKnown(dataFactory, transformers);
    }

    static class TransformerFunctionKnown implements Function<DidoData, DidoData> {

        private final DataFactory dataFactory;

        private final List<Transformer> transformers;

        TransformerFunctionKnown(DataFactory dataFactory, List<Transformer> transformers) {
            this.dataFactory = dataFactory;
            this.transformers = transformers;
        }

        @Override
        public DidoData apply(DidoData dataIn) {
            for (Transformer transformer : transformers) {
                transformer.transform(dataIn, dataFactory.getSetter());
            }

            return dataFactory.toData();
        }
    }
}

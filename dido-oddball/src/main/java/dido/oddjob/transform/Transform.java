package dido.oddjob.transform;

import dido.data.DataSchema;
import dido.data.GenericData;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Copies fields from one data item to another allowing for change of field names, indexes
 * and type.
 *
 * @param <F>
 * @param <T>
 */
public class Transform<F, T> implements ValueFactory<Function<GenericData<F>, GenericData<T>>> {

    private final ListSetterHelper<TransformerFactory<F, T>> of = new ListSetterHelper<>();

    /** Strategy for creating the new schema for outgoing data. Defaults to merge. */
    private SchemaStrategy strategy;

    @Override
    public Function<GenericData<F>, GenericData<T>> toValue() {

        return new TransformerFunctionInitial<>(of.getList(), strategy);
    }

    public void setOf(int index, TransformerFactory<F, T> transformer) {
        this.of.set(index, transformer);
    }

    public SchemaStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(SchemaStrategy strategy) {
        this.strategy = strategy;
    }

    static class TransformerFunctionInitial<F, T> implements Function<GenericData<F>, GenericData<T>> {

        private final List<TransformerFactory<F, T>> transformerFactories;

        private final SchemaStrategy strategy;

        private Function<GenericData<F>, GenericData<T>> delegate;

        private DataSchema<F> lastSchema;

        TransformerFunctionInitial(List<TransformerFactory<F, T>> transformerFactories, SchemaStrategy strategy) {
            this.transformerFactories = new ArrayList<>(transformerFactories);
            this.strategy = strategy;
        }

        @Override
        public GenericData<T> apply(GenericData<F> dataIn) {

            if (delegate == null || !dataIn.getSchema().equals(lastSchema)) {
                lastSchema = dataIn.getSchema();
                delegate = functionFor(transformerFactories, lastSchema, strategy);
            }
            return delegate.apply(dataIn);
        }
    }

    static <T, F> TransformerFunctionKnown<F, T> functionFor(List<TransformerFactory<F, T>> factories,
                                                             DataSchema<F> schemaFrom,
                                                             SchemaStrategy partial) {

        int position = 0;

        List<SchemaFieldOptions<F>> newFields = new ArrayList<>();

        SchemaSetter<T> schemaSetter = (index, field, fieldType) -> {
            // need to think about how to support changing field type to enum
            //noinspection unchecked
            newFields.add(SchemaFieldOptions.of(index, (F) field, fieldType));
        };

        List<Transformer<F, T>> transformers = new ArrayList<>(factories.size());

        for (TransformerFactory<F, T> factory : factories) {

            transformers.add(factory.create(++position, schemaFrom, schemaSetter));
        }

        SchemaStrategy schemaStrategy = Objects.requireNonNullElse(partial, SchemaStrategy.MERGE);

        //noinspection rawtypes
        @SuppressWarnings("unchecked")
        DataSchema<T> schema = schemaStrategy.newSchemaFrom((DataSchema) schemaFrom,
                newFields,
                i -> transformers.add((in, setter) -> setter.setAt(i, in.getAt(i))));

        DataFactory<T> dataFactory = new ArrayDataSetterProvider<T>().provideSetter(schema);

        return new TransformerFunctionKnown<>(dataFactory, transformers);
    }

    static class TransformerFunctionKnown<F, T> implements Function<GenericData<F>, GenericData<T>> {

        private final DataFactory<T> dataFactory;

        private final List<Transformer<F, T>> transformers;

        TransformerFunctionKnown(DataFactory<T> dataFactory, List<Transformer<F, T>> transformers) {
            this.dataFactory = dataFactory;
            this.transformers = transformers;
        }

        @Override
        public GenericData<T> apply(GenericData<F> dataIn) {
            for (Transformer<F, T> transformer : transformers) {
                transformer.transform(dataIn, dataFactory.getSetter());
            }

            return dataFactory.toData();
        }
    }
}

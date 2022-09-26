package dido.oddjob.transpose;

import dido.data.DataSchema;
import dido.data.GenericData;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Copies fields from one data to another allowing for chane of field names, indexes and type.
 *
 * @param <F>
 * @param <T>
 */
public class Transpose<F, T> implements ValueFactory<Function<GenericData<F>, GenericData<T>>> {

    private final ListSetterHelper<TransposerFactory<F, T>> of = new ListSetterHelper<>();

    private SchemaStrategy strategy;

    @Override
    public Function<GenericData<F>, GenericData<T>> toValue() {

        return new TransposeFunctionInitial<>(of.getList(), strategy);
    }

    public void setOf(int index, TransposerFactory<F, T> transposer) {
        this.of.set(index, transposer);
    }

    public SchemaStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(SchemaStrategy strategy) {
        this.strategy = strategy;
    }

    static class TransposeFunctionInitial<F, T> implements Function<GenericData<F>, GenericData<T>> {

        private final List<TransposerFactory<F, T>> transposerFactories;

        private final SchemaStrategy strategy;

        private Function<GenericData<F>, GenericData<T>> delegate;

        private DataSchema<F> lastSchema;

        TransposeFunctionInitial(List<TransposerFactory<F, T>> transposerFactories, SchemaStrategy strategy) {
            this.transposerFactories = new ArrayList<>(transposerFactories);
            this.strategy = strategy;
        }

        @Override
        public GenericData<T> apply(GenericData<F> dataIn) {

            if (delegate == null || lastSchema != dataIn.getSchema()) {
                lastSchema = dataIn.getSchema();
                delegate = functionFor(transposerFactories, lastSchema, strategy);
            }
            return delegate.apply(dataIn);
        }
    }

    static <T, F> TransposeFunctionKnown<F, T> functionFor(List<TransposerFactory<F, T>> factories,
                                                           DataSchema<F> schemaFrom,
                                                           SchemaStrategy partial) {

        int position = 0;

        List<SchemaFieldOptions<F>> newFields = new ArrayList<>();

        SchemaSetter<T> schemaSetter = (index, field, fieldType) -> {
            // need to think about how to support changing field type to enum
            //noinspection unchecked
            newFields.add(SchemaFieldOptions.of(index, (F) field, fieldType));
        };

        List<Transposer<F, T>> transposers = new ArrayList<>(factories.size());

        for (TransposerFactory<F, T> factory : factories) {

            transposers.add(factory.create(++position, schemaFrom, schemaSetter));
        }

        SchemaStrategy schemaStrategy = Objects.requireNonNullElse(partial, SchemaStrategy.MERGE);

        //noinspection rawtypes
        @SuppressWarnings("unchecked")
        DataSchema<T> schema = schemaStrategy.newSchemaFrom((DataSchema) schemaFrom,
                newFields,
                i -> transposers.add((in, setter) -> setter.setAt(i, in.getAt(i))));

        DataFactory<T> dataFactory = new ArrayDataSetterProvider<T>().provideSetter(schema);

        return new TransposeFunctionKnown<>(dataFactory, transposers);
    }

    static class TransposeFunctionKnown<F, T> implements Function<GenericData<F>, GenericData<T>> {

        private final DataFactory<T> dataFactory;

        private final List<Transposer<F, T>> transposers;

        TransposeFunctionKnown(DataFactory<T> dataFactory, List<Transposer<F, T>> transposers) {
            this.dataFactory = dataFactory;
            this.transposers = transposers;
        }

        @Override
        public GenericData<T> apply(GenericData<F> dataIn) {
            for (Transposer<F, T> transposer : transposers) {
                transposer.transpose(dataIn, dataFactory.getSetter());
            }

            return dataFactory.toData();
        }
    }
}

package dido.oddjob.transpose;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.SchemaBuilder;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Transpose<F, T> implements ValueFactory<Function<GenericData<F>, GenericData<T>>> {

    private final ListSetterHelper<TransposerFactory<F, T>> of = new ListSetterHelper<>();

    private boolean partial;

    @Override
    public Function<GenericData<F>, GenericData<T>> toValue() {

        return new TransposeFunctionInitial<>(of.getList(), partial);
    }

    public void setOf(int index, TransposerFactory<F, T> transposer) {
        this.of.set(index, transposer);
    }

    public boolean isPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
    }

    static class TransposeFunctionInitial<F, T> implements Function<GenericData<F>, GenericData<T>> {

        private final List<TransposerFactory<F, T>> transposerFactories;

        private final boolean partial;

        private Function<GenericData<F>, GenericData<T>> delegate;

        private DataSchema<F> lastSchema;

        TransposeFunctionInitial(List<TransposerFactory<F, T>> transposerFactories, boolean partial) {
            this.transposerFactories = new ArrayList<>(transposerFactories);
            this.partial = partial;
        }

        @Override
        public GenericData<T> apply(GenericData<F> dataIn) {

            if (delegate == null || lastSchema != dataIn.getSchema()) {
                lastSchema = dataIn.getSchema();
                delegate = functionFor(transposerFactories, lastSchema, partial);
            }
            return delegate.apply(dataIn);
        }
    }

    static <T, F> TransposeFunctionKnown<F, T> functionFor(List<TransposerFactory<F, T>> factories,
                                                           DataSchema<F> schemaFrom,
                                                           boolean partial) {

        int position = 0;

        SchemaBuilder<T> schemaBuilder = SchemaBuilder.impliedType();

        SchemaSetter<T> schemaSetter = schemaBuilder::addIndexedField;

        List<Transposer<F, T>> transposers = new ArrayList<>(factories.size());

        for (TransposerFactory<F, T> factory : factories) {

            transposers.add(factory.create(++position, schemaFrom, schemaSetter));
        }

        DataSchema<T> schema = schemaBuilder.build();

        if (!partial) {
            SchemaBuilder<T> completeBuilder = SchemaBuilder.impliedType();

            int n = schema.firstIndex();
            for (int i = schemaFrom.firstIndex(); i > 0; i = schemaFrom.nextIndex(i)) {
                if (i == n) {
                    completeBuilder.addIndexedField(n, schema.getFieldAt(n), schema.getTypeAt(n));
                    n = schema.nextIndex(n);
                }
                else {
                    //noinspection unchecked - we assume from and to the same for partials.
                    completeBuilder.addIndexedField(i, (T) schemaFrom.getFieldAt(i), schemaFrom.getTypeAt(i));
                    final int index = i;
                    transposers.add((in, setter) -> setter.setAt(index, in.getAt(index)));
                }
            }

            // Indexes greater than the original schema.
            if (n > schemaFrom.lastIndex()) {
                for (; n > 0; n = schema.nextIndex(n)) {
                    completeBuilder.addIndexedField(n, schema.getFieldAt(n), schema.getTypeAt(n));
                }
            }

            schema = completeBuilder.build();
        }

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

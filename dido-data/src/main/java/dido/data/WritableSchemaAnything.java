package dido.data;

import java.util.Collection;
import java.util.function.Function;

/**
 * Schema for Anything.
 */
public class WritableSchemaAnything<D extends DidoData> extends DataSchemaImpl
        implements WritableSchema<D> {

    private final Function<? super DataSchema, ? extends DataFactory<D>> dataFactoryProvider;

    public WritableSchemaAnything(Function<? super DataSchema, ? extends DataFactory<D>> dataFactoryProvider) {
        this.dataFactoryProvider = dataFactoryProvider;
    }

    public WritableSchemaAnything(DataSchema from, Function<? super DataSchema, ? extends DataFactory<D>> dataFactoryProvider) {
        super(from.getSchemaFields(), from.firstIndex(), from.lastIndex());
        this.dataFactoryProvider = dataFactoryProvider;
    }

    WritableSchemaAnything(Iterable<SchemaField> schemaFields, int firstIndex, int lastIndex,
                           Function<? super DataSchema, ? extends DataFactory<D>> dataFactoryProvider) {
        super(schemaFields, firstIndex, lastIndex);
        this.dataFactoryProvider = dataFactoryProvider;
    }

    public static <D extends DidoData> WritableSchemaFactory<D> newFactory(Function<? super DataSchema, ? extends DataFactory<D>> dataFactoryProvider) {
        return new SchemaFactory<>(dataFactoryProvider);
    }


    @Override
    public WritableSchemaFactory<D> newSchemaFactory() {
        return new SchemaFactory<>(dataFactoryProvider);
    }

    @Override
    public DataFactory<D> newDataFactory() {
        return dataFactoryProvider.apply(this);
    }

    static class SchemaFactory<D extends DidoData> extends SchemaFactoryImpl<WritableSchemaAnything<D>>
        implements WritableSchemaFactory<D> {

        private final Function<? super DataSchema, ? extends DataFactory<D>> dataFactoryProvider;

        protected SchemaFactory(Function<? super DataSchema, ? extends DataFactory<D>> dataFactoryProvider) {
            this.dataFactoryProvider = dataFactoryProvider;
        }

        protected SchemaFactory(DataSchema from,
                                Function<? super DataSchema, ? extends DataFactory<D>> dataFactoryProvider) {
            super(from);
            this.dataFactoryProvider = dataFactoryProvider;
        }

        @Override
        WritableSchemaAnything<D> create(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
            return new WritableSchemaAnything<>(fields, firstIndex, lastIndex, dataFactoryProvider);
        }
    }

}

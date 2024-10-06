package dido.data.generic;

import dido.data.DataFactory;
import dido.data.DataFactoryProvider;
import dido.data.DataSchema;
import dido.data.WriteSchemaFactory;

import java.util.function.Function;

public class GenericMapDataFactoryProvider<F> implements DataFactoryProvider<GenericMapData<F>> {

    private final GenericMapData.Of<F> of;

    public GenericMapDataFactoryProvider(Class<F> fieldType,
                                         Function<? super String, ? extends F> fieldMapping) {
        this.of = GenericMapData.with(fieldType, fieldMapping);
    }

    public GenericMapDataFactoryProvider(GenericMapData.Of<F> of) {
        this.of = of;
    }

    @Override
    public Class<?> getDataType() {
        return GenericMapData.class;
    }

    @Override
    public WriteSchemaFactory getSchemaFactory() {
        return of.schemaFactory();
    }

    @Override
    public DataFactory<GenericMapData<F>> provideFactory(DataSchema schema) {
        return of.factoryForSchema(schema);
    }
}

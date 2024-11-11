package dido.data.generic;

import dido.data.DataSchema;

import java.util.function.Function;

public class GenericMapDataFactoryProvider<F> implements GenericDataFactoryProvider<F> {

    private final GenericMapData.Of<F> of;

    public GenericMapDataFactoryProvider(Class<F> fieldType,
                                         Function<? super String, ? extends F> fieldMapping) {
        this.of = GenericMapData.with(fieldType, fieldMapping);
    }

    public GenericMapDataFactoryProvider(GenericMapData.Of<F> of) {
        this.of = of;
    }

    @Override
    public GenericSchemaFactory<F> getSchemaFactory() {
        return of.schemaFactory();
    }

    @Override
    public GenericDataFactory<F> factoryFor(DataSchema schema) {
        return of.factoryForSchema(schema);
    }
}

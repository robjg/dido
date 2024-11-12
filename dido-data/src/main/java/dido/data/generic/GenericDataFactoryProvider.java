package dido.data.generic;

import dido.data.DataFactoryProvider;
import dido.data.DataSchema;

public interface GenericDataFactoryProvider<F> extends DataFactoryProvider {

    @Override
    GenericSchemaFactory<F> getSchemaFactory();

    @Override
    GenericDataFactory<F> factoryFor(DataSchema schema);
}

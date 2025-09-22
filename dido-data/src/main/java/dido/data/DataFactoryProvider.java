package dido.data;

import dido.data.immutable.ArrayDataDataFactoryProvider;

/**
 * Something that is able to provide a {@link DataFactory}.
 *
 */
public interface DataFactoryProvider {

    SchemaFactory getSchemaFactory();

    DataFactory factoryFor(DataSchema schema);

    static DataFactoryProvider newInstance() {
        return new ArrayDataDataFactoryProvider();
    }
}

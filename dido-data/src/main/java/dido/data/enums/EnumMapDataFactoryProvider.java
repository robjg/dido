package dido.data.enums;

import dido.data.DataSchema;
import dido.data.generic.GenericDataFactory;
import dido.data.generic.GenericDataFactoryProvider;
import dido.data.generic.GenericSchemaFactory;

public class EnumMapDataFactoryProvider<E extends Enum<E>> implements GenericDataFactoryProvider<E> {

    private final EnumMapData.Of<E> of;

    public EnumMapDataFactoryProvider(EnumMapData.Of<E> of) {
        this.of = of;
    }

    public static <E extends Enum<E>> EnumMapDataFactoryProvider<E> ofEnumClass(Class<E> enumType) {
        return new EnumMapDataFactoryProvider<>(EnumMapData.ofEnumClass(enumType));
    }

    public static <E extends Enum<E>> EnumMapDataFactoryProvider<E> of(EnumMapData.Of<E> of) {
        return new EnumMapDataFactoryProvider<>(of);
    }

    @Override
    public GenericSchemaFactory<E> getSchemaFactory() {
        return of.schemaFactory();
    }

    @Override
    public GenericDataFactory<E> factoryFor(DataSchema schema) {
        return of.factoryForSchema(schema);
    }
}

package dido.data.enums;

import dido.data.generic.GenericSchemaFactory;
import dido.data.generic.GenericSchemaFactoryImpl;
import dido.data.generic.GenericSchemaField;
import dido.data.generic.GenericSchemaImpl;

import java.util.Collection;

public interface EnumSchemaFactory<E extends Enum<E>> extends GenericSchemaFactory<E> {

    @Override
    EnumSchema<E> toSchema();


    static <E extends Enum<E>> EnumSchemaFactory<E> schemaFactoryFor(Class<E> enumClass) {

        class Schema extends GenericSchemaImpl<E> implements EnumSchema<E> {

            protected Schema(Class<E> fieldType, Iterable<GenericSchemaField<E>> genericSchemaFields, int firstIndex, int lastIndex) {
                super(fieldType, genericSchemaFields, firstIndex, lastIndex);
            }
        }

        class Factory extends GenericSchemaFactoryImpl<E, EnumSchema<E>> implements EnumSchemaFactory<E> {

            Factory() {
                super(enumClass,
                        name -> Enum.valueOf(enumClass, name));
            }

            @Override
            protected EnumSchema<E> createGeneric(Collection<GenericSchemaField<E>> genericSchemaFields,
                                                  int firstIndex,
                                                  int lastIndex) {
                return new Schema(enumClass, genericSchemaFields,
                        firstIndex, lastIndex);
            }
        }

        return new Factory();
    }
}

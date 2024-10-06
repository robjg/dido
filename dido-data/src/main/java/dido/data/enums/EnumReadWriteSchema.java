package dido.data.enums;

import dido.data.generic.GenericReadWriteSchema;

public interface EnumReadWriteSchema<E extends Enum<E>>
        extends GenericReadWriteSchema<E>, EnumReadSchema<E>, EnumWriteSchema<E> {

}

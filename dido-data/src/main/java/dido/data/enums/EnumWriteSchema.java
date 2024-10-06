package dido.data.enums;

import dido.data.generic.GenericWriteSchema;

public interface EnumWriteSchema<E extends Enum<E>> extends GenericWriteSchema<E>, EnumSchema<E> {

}

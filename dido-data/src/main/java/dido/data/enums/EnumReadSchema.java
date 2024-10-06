package dido.data.enums;

import dido.data.generic.GenericReadSchema;

public interface EnumReadSchema<E extends Enum<E>> extends GenericReadSchema<E>, EnumSchema<E> {

}

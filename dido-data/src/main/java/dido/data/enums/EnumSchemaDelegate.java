package dido.data.enums;

import dido.data.generic.GenericSchemaDelegate;

public class EnumSchemaDelegate<E extends Enum<E>> extends GenericSchemaDelegate<E> implements EnumSchema<E> {

    protected EnumSchemaDelegate(EnumSchema<E> delegate) {
        super(delegate);
    }
}

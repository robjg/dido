package dido.data;

import dido.data.generic.AbstractGenericData;

public abstract class AbstractEnumData<E extends Enum<E>> extends AbstractGenericData<E> implements EnumData<E> {

}

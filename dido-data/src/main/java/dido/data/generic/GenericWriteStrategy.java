package dido.data.generic;

import dido.data.FieldSetter;
import dido.data.WriteStrategy;

public interface GenericWriteStrategy<F> extends WriteStrategy {

    FieldSetter getFieldSetter(F field);


}

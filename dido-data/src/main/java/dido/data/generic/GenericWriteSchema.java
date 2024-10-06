package dido.data.generic;

import dido.data.FieldSetter;
import dido.data.WriteSchema;

public interface GenericWriteSchema<F>
        extends GenericDataSchema<F>, WriteSchema {

    FieldSetter getFieldSetter(F field);

}

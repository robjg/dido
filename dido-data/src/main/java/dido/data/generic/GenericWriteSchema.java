package dido.data.generic;

import dido.data.WriteSchema;

public interface GenericWriteSchema<F>
        extends GenericDataSchema<F>, GenericWriteStrategy<F>, WriteSchema {

}

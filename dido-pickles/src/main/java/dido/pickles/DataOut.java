package dido.pickles;

import dido.data.GenericData;

/**
 * Something that data can be written to. This is one of the fundamental
 * concepts in Dido.
 * 
 * @see DataOutHow
 * @see DataIn
 * 
 * @author rob
 *
 */
public interface DataOut<F> extends CloseableConsumer<GenericData<F>> {

}

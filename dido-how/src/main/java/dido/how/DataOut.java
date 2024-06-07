package dido.how;

import dido.data.DidoData;

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
public interface DataOut extends CloseableConsumer<DidoData> {

}

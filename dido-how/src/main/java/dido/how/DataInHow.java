package dido.how;

import dido.data.DidoData;

/**
 * Something that can create a {@link DataIn}. This is the starting
 * point for reading data.
 * 
 * @see DataOutHow
 * 
 * @author rob
 *
 * @param <I> The Input Type
 */
public interface DataInHow<I, D extends DidoData> {

	Class<I> getInType();

	/**
	 * Create a reader.
	 * 
	 * @param dataIn The place that data will be read from.
	 * @return A reader. Never null.
	 * 
	 */
	DataIn<D> inFrom(I dataIn) throws Exception;
	
}

package dido.how;

/**
 * Something that can create a {@link DataIn}. This is the starting
 * point for reading data.
 * 
 * @see DataOutHow
 * 
 * @author rob
 *
 */
public interface DataInHow<F, I> {

	Class<I> getInType();

	/**
	 * Create a reader.
	 * 
	 * @param dataIn The place that data will be read from.
	 * @return A reader. Never null.
	 * 
	 */
	DataIn<F> inFrom(I dataIn) throws Exception;
	
}

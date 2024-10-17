package dido.how;

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
public interface DataInHow<I> {

	Class<I> getInType();

	/**
	 * Create a reader.
	 * 
	 * @param dataIn The place that data will be read from.
	 * @return A reader. Never null.
	 * 
	 */
	DataIn inFrom(I dataIn);
}

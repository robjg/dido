package dido.how;

/**
 * Something that can create a {@link DataOut}. This is the starting
 * point for writing data.
 * 
 * @see DataInHow
 * 
 * @author rob
 *
 * @param <O> The Output Type
 */
public interface DataOutHow<O> {

	Class<O> getOutType();

	/**
	 * Create a writer.
	 * 
	 * @param dataOut The place that data will be written to.
	 * @return A writer. Never null.
	 * 
	 */
	DataOut outTo(O dataOut);

}

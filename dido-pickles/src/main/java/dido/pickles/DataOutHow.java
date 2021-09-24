package dido.pickles;

/**
 * Something that can create a {@link DataOut}. This is the starting
 * point for writing data.
 * 
 * @see DataInHow
 * 
 * @author rob
 *
 */
public interface DataOutHow<F, O> {

	Class<O> getOutType();

	/**
	 * Create a writer.
	 * 
	 * @param dataOut The place that data will be written to.
	 * @return A writer. Never null.
	 * 
	 */
	DataOut<F> outTo(O dataOut) throws Exception;
}

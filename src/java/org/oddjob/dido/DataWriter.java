package org.oddjob.dido;

public interface DataWriter {

	/**
	 * 
	 * @param value
	 * 
	 * @return Is more data required. If true then any calling node should
	 * return to client code to be given a new value. If false calling code
	 * should progress on to the next node in the sequence of walking the
	 * layout hierarchy.
	 * 
	 * @throws DataException
	 */
	public boolean write(Object value) throws DataException;
}

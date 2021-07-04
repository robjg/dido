package org.oddjob.dido;

/**
 * Something has gone wrong with reading or writing data.
 * 
 * @author rob
 *
 */
public class DataException extends Exception {
	private static final long serialVersionUID = 2010072700L;
	
	public DataException() {
	}

	public DataException(String message) {
		super(message);
	}

	public DataException(Throwable cause) {
		super(cause);
	}

	public DataException(String message, Throwable cause) {
		super(message, cause);
	}

}

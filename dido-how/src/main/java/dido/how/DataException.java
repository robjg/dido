package dido.how;

/**
 * A General Purpose Runtime Exception for wrapping exceptions.
 * 
 * @author rob
 *
 */
public class DataException extends RuntimeException {
	private static final long serialVersionUID = 2021102000L;
	
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

package dido.how;

/**
 * A General Purpose Runtime Exception for wrapping exceptions.
 * 
 * @author rob
 *
 */
public class DataException extends RuntimeException {
	private static final long serialVersionUID = 2021102000L;
	
	protected DataException() {
	}

	protected DataException(String message) {
		super(message);
	}

	protected DataException(Throwable cause) {
		super(cause);
	}

	protected DataException(String message, Throwable cause) {
		super(message, cause);
	}

	public static DataException of(String message) {
		return new DataException(message);
	}

	public static DataException of(String message, Throwable cause) {
		return new DataException(message, cause);
	}

	public static DataException of(Throwable cause) {
		return new DataException(cause);
	}
}

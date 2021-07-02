package org.oddjob.dido;

/**
 * Thrown when a {@link DataOut} doesn't support the required type.
 * 
 * @author rob
 *
 */
public class UnsupportedDataOutException extends DataException {
	private static final long serialVersionUID = 2010072700L;
	
	
	public UnsupportedDataOutException(Class<? extends DataOut> provider, 
			Class<? extends DataOut> required) {
		super(provider.getName() + " cannot provide " + required.getName());
	}

}
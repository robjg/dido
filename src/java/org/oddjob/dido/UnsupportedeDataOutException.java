package org.oddjob.dido;

/**
 * Something has gone wrong with reading or writing data.
 * 
 * @author rob
 *
 */
public class UnsupportedeDataOutException extends DataException {
	private static final long serialVersionUID = 2010072700L;
	
	
	public UnsupportedeDataOutException(Class<? extends DataOut> provider, 
			Class<? extends DataOut> required) {
		super(provider.getName() + " cannot provide " + required.getName());
	}

}

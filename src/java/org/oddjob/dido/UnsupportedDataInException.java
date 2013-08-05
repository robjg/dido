package org.oddjob.dido;

/**
 * Something has gone wrong with reading or writing data.
 * 
 * @author rob
 *
 */
public class UnsupportedDataInException extends DataException {
	private static final long serialVersionUID = 2010072700L;
	
	
	public UnsupportedDataInException(Class<? extends DataIn> provider, 
			Class<? extends DataIn> required) {
		super(provider.getName() + " cannot provide " + required.getName());
	}

}

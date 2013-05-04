package org.oddjob.dido;

/**
 * Something has gone wrong with reading or writing data.
 * 
 * @author rob
 *
 */
public class UnsupportedeDataInException extends DataException {
	private static final long serialVersionUID = 2010072700L;
	
	
	public UnsupportedeDataInException(Class<? extends DataIn> provider, 
			Class<? extends DataIn> required) {
		super(provider.getName() + " cannot provide " + required.getName());
	}

}

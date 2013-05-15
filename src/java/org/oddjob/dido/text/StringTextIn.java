package org.oddjob.dido.text;

import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataInProvider;
import org.oddjob.dido.UnsupportedeDataInException;


public class StringTextIn implements TextIn, DataInProvider {

	private final String text;
	
	public StringTextIn(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}	
	
	public String toString() {
		return text;
	}
	
	@Override
	public <T extends DataIn> T provideIn(Class<T> type)
			throws UnsupportedeDataInException {
		if (type.isAssignableFrom(TextIn.class)) {
			return type.cast(this);
		}
		else {
			throw new UnsupportedeDataInException(this.getClass(), type);
		}
	}
}

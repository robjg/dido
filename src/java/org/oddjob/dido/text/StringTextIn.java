package org.oddjob.dido.text;

import java.util.Arrays;

import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedDataInException;
import org.oddjob.dido.stream.LinesIn;
import org.oddjob.dido.stream.ListLinesIn;


public class StringTextIn implements TextIn {

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
	public <T extends DataIn> T provideDataIn(Class<T> type)
			throws UnsupportedDataInException {
		
		if (type.isInstance(this)) {
			return type.cast(this);
		}

		if (type.isAssignableFrom(LinesIn.class)) {
			return type.cast(new ListLinesIn(Arrays.asList(text)));
		}
		
		throw new UnsupportedDataInException(this.getClass(), type);
	}
}

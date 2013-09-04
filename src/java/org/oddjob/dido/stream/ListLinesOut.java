package org.oddjob.dido.stream;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.text.StringTextOut;
import org.oddjob.dido.text.TextOut;

public class ListLinesOut implements LinesOut {

	private List<String> lines = new ArrayList<String>();
	
	private StringTextOut textOut;
	
	private String lastLine;
	
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type)
			throws UnsupportedDataOutException {
		
		if (type.isAssignableFrom(LinesOut.class)) {
			return type.cast(this);
		}
		
		if (type.isAssignableFrom(TextOut.class)) {
			if (textOut == null) {
				textOut = new StringTextOut();
			}
			return type.cast(textOut);
		}

		throw new UnsupportedDataOutException(getClass(), type);
	}
	
	@Override
	public void writeLine(String text) throws DataException {
		lastLine = text;
		lines.add(text);
		textOut = null;
	}	
	
	@Override
	public String lastLine() {
		if (textOut != null) {
			return textOut.toText();
		}
		else {
			return lastLine;
		}
	}
	
	@Override
	public void resetWrittenTo() {
		textOut = null;
		lastLine = null;
	}

	@Override
	public boolean isWrittenTo() {
		return lastLine != null || textOut != null && textOut.isWrittenTo();
	}
	
	@Override
	public boolean isMultiLine() {
		return true;
	}
	
	public List<String> getLines() {
		return lines;
	}
	
	@Override
	public void close() throws DataException {
		// Nothing to close.
	}
}

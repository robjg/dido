package org.oddjob.dido.stream;

import java.io.IOException;
import java.io.OutputStream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedeDataOutException;
import org.oddjob.dido.text.StringTextOut;
import org.oddjob.dido.text.TextOut;

public class StreamLinesOut implements LinesOut {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private final OutputStream outputStream;
	
	private TextOut textOut;
	
	public StreamLinesOut(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	public void writeLine(String text) throws DataException {
		try {
			outputStream.write(text.getBytes());
			outputStream.write(LINE_SEPARATOR.getBytes());
		} 
		catch (IOException e) {
			throw new DataException(e);
		}
		finally {
			reset();
		}
	}
	
	protected void reset() {
		textOut = null;
	}
	
	
	@Override
	public <T extends DataOut> T provide(Class<T> type)
			throws UnsupportedeDataOutException {
		
		if (type.isAssignableFrom(LinesOut.class)) {
			if (textOut == null) {
				return type.cast(this);
			}
			else {
				type.cast(new StreamLinesOut(outputStream) {
					@Override
					public void writeLine(String text) throws DataException {
						textOut.append(LINE_SEPARATOR);
						textOut.append(text);
						
						// Clear any text this LinesOut has.
						this.reset();
					}
				});
			}
		}

		if (type.isAssignableFrom(TextOut.class)) {
			if (textOut == null) {
				textOut = new StringTextOut();
			}
			return type.cast(textOut);
		}
		
		throw new UnsupportedeDataOutException(getClass(), type);
	}
	
	@Override
	public boolean hasData() {
		return textOut != null && textOut.hasData();
	}
	
	@Override
	public <T> T toValue(Class<T> type) {
		if (textOut != null) {
			return textOut.toValue(type);
		}
		else {
			return null;
		}
	}
	
}

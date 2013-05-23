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
	
	private boolean disabled;
	
	private TextOut textOut;
	
	private String lastLine;
	
	public StreamLinesOut(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void writeLine(String text) throws DataException {
		if (!disabled) {
			_writeLine(text);
		}
		textOut = null;
	}
	
	private void _writeLine(String text) throws DataException {
		lastLine = text;
		try {
			outputStream.write(text.getBytes());
			outputStream.write(LINE_SEPARATOR.getBytes());
		} catch (IOException e) {
			throw new DataException(e);
		}
	}

	@Override
	public <T extends DataOut> T provide(Class<T> type)
			throws UnsupportedeDataOutException {
		
		if (type.isAssignableFrom(LinesOut.class)) {
			this.disabled = true;
			return type.cast(new StreamLinesOut(outputStream));
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
	public boolean flush() throws DataException {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean hasData() {
		return lastLine != null;
	}
	
	@Override
	public <T> T toValue(Class<T> type) {
		if (textOut != null) {
			return textOut.toValue(type);
		}
		else {
			return type.cast(lastLine);
		}
	}
	
}

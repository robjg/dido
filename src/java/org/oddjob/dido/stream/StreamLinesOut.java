package org.oddjob.dido.stream;

import java.io.IOException;
import java.io.OutputStream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.text.StringTextOut;
import org.oddjob.dido.text.TextOut;

/**
 * Provide a way of writing lines to an {@code OutputStream}.
 * 
 * @author rob
 *
 */
public class StreamLinesOut implements LinesOut {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private final OutputStream outputStream;
	
	private StringTextOut textOut;
	
	private String lastLine;
	
	private int linesWritten;
	
	public StreamLinesOut(OutputStream outputStream) {
		if (outputStream == null) {
			throw new NullPointerException("Null OutputStream!");
		}
		
		this.outputStream = outputStream;
	}
	
	@Override
	public void writeLine(String text) throws DataException {

		try {
			outputStream.write(text.getBytes());
			outputStream.write(LINE_SEPARATOR.getBytes());
			
			lastLine = text;
			
			++linesWritten;
		} 
		catch (IOException e) {
			throw new DataException(e);
		}
		finally {
			reset();
		}
	}
	
	@Override
	public int getLinesWritten() {
		return linesWritten;
	}
	
	protected void reset() {
		textOut = null;
	}
	
	@Override
	public void resetWrittenTo() {
		textOut = null;
		lastLine = null;
	}
	
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type)
			throws UnsupportedDataOutException {
		
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
					@Override
					public void close() throws DataException {
						// only outer class closes the stream.
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
		
		throw new UnsupportedDataOutException(getClass(), type);
	}
	
	@Override
	public boolean isWrittenTo() {
		return textOut != null && textOut.isWrittenTo();
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
	public boolean isMultiLine() {
		return true;
	}
	
	@Override
	public void close() throws DataException {
		try {
			outputStream.close();
		} catch (IOException e) {
			throw new DataException(e);
		}
	}
		
	@Override
	public String toString() {
		return getClass().getSimpleName() + 
				(textOut == null ? "" : " with text length [" + 
						textOut.length() + "]");
	}
}

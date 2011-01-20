package org.oddjob.dido.stream;

import java.io.IOException;
import java.io.OutputStream;

import org.oddjob.dido.DataException;


public class OutputStreamOut implements StreamOut, LinesOut {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private final OutputStream outputStream;
	
	public OutputStreamOut(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
		
	public void newLine() throws DataException {
		try {
			outputStream.write(LINE_SEPARATOR.getBytes());
		} catch (IOException e) {
			throw new DataException(e);
		}
	}

	public void writeLine(String text) throws DataException {
		try {
			outputStream.write(text.getBytes());
			outputStream.write(LINE_SEPARATOR.getBytes());
		} catch (IOException e) {
			throw new DataException(e);
		}
	}

	
	
	public void write(String text) throws DataException {
		try {
			outputStream.write(text.getBytes());
		} catch (IOException e) {
			throw new DataException(e);
		}
	}
	
	@Override
	public OutputStream getStream() {
		return outputStream;
	}
	
	public boolean flush() throws DataException {
		try {
			outputStream.flush();
		} catch (IOException e) {
			throw new DataException(e);
		}
		return true;
	}
}

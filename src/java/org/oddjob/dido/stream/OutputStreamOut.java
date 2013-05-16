package org.oddjob.dido.stream;

import java.io.IOException;
import java.io.OutputStream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedeDataOutException;


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
	
	@Override
	public <T extends DataOut> T provideOut(Class<T> type)
			throws UnsupportedeDataOutException {
		
		if (type.isAssignableFrom(StreamOut.class)) {
			return type.cast(this);
		}
		else {
			throw new UnsupportedeDataOutException(getClass(), type);
		}
	}
	
	@Override
	public boolean hasData() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public <T> T toValue(Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

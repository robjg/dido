package org.oddjob.dido.stream;

import java.io.IOException;
import java.io.OutputStream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;


public class OutputStreamOut implements StreamOut {

	private OutputStream outputStream;
	
	public OutputStreamOut() {
	}
	
	public OutputStreamOut(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
		
	public void setOutput(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	@Override
	public OutputStream outputStream() {
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
	public <T extends DataOut> T provideDataOut(Class<T> type)
			throws UnsupportedDataOutException {
		
		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		if (type.isAssignableFrom(LinesOut.class)) {
			return type.cast(new StreamLinesOut(outputStream));
		}
		
		throw new UnsupportedDataOutException(getClass(), type);
	}
	
	@Override
	public boolean isWrittenTo() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void close() throws DataException {
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				throw new DataException();
			}
		}
	}
	
}

package org.oddjob.dido.stream;

import java.io.IOException;
import java.io.OutputStream;

import org.oddjob.dido.Closeable;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataValueOut;
import org.oddjob.dido.UnsupportedeDataOutException;


public class OutputStreamOut implements StreamOut, DataValueOut, Closeable {

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
	public <T extends DataOut> T provide(Class<T> type)
			throws UnsupportedeDataOutException {
		
		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		if (type.isAssignableFrom(LinesOut.class)) {
			return type.cast(new StreamLinesOut(outputStream));
		}
		
		throw new UnsupportedeDataOutException(getClass(), type);
	}
	
	@Override
	public boolean isWrittenTo() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <T> T toValue(Class<T> type) {
		throw new UnsupportedOperationException();
	}
	
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

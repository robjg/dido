package org.oddjob.dido.stream;

import java.io.IOException;
import java.io.OutputStream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedeDataOutException;


public class OutputStreamOut implements StreamOut {

	private final OutputStream outputStream;
	
	public OutputStreamOut(OutputStream outputStream) {
		this.outputStream = outputStream;
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
	public boolean hasData() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <T> T toValue(Class<T> type) {
		throw new UnsupportedOperationException();
	}
	
	public void close() throws IOException {
		outputStream.close();
	}
	
}

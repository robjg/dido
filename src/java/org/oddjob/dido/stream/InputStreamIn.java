package org.oddjob.dido.stream;

import java.io.IOException;
import java.io.InputStream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedDataInException;

/**
 * A {@link DataIn} from an {@link InputStream}.
 * 
 * @author rob
 *
 */
public class InputStreamIn implements StreamIn {
	
	private InputStream inputStream;
	
	public InputStreamIn() {
	}
	
	public InputStreamIn(InputStream inputStream) {
		this.inputStream = inputStream;
	}
		
	public void setInput(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	@Override
	public InputStream inputStream() {
		return inputStream;
	}
	
	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type) 
	throws DataException{
		
		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		if (type.isAssignableFrom(LinesIn.class)) {
			return type.cast(new StreamLinesIn(inputStream()));
		}
		
		throw new UnsupportedDataInException(this.getClass(), type);
	}

	@Override
	public void close() throws DataException {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new DataException(e);
			}
		}
	}
}

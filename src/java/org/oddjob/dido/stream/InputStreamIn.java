package org.oddjob.dido.stream;

import java.io.IOException;
import java.io.InputStream;

import org.oddjob.arooa.life.Destroy;
import org.oddjob.dido.Closeable;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedeDataInException;

/**
 * A {@link DataIn} from an {@link InputStream}.
 * 
 * @author rob
 *
 */
public class InputStreamIn implements StreamIn, Closeable {
	
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
		
		throw new UnsupportedeDataInException(this.getClass(), type);
	}
	
	@Override
	@Destroy
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

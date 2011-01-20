package org.oddjob.dido.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;

/**
 * A {@link DataIn} from an {@link InputStream}.
 * 
 * @author rob
 *
 */
public class InputStreamIn implements StreamIn, LinesIn {
	
	public final LineNumberReader reader;
	
	private final InputStream inputStream;
	
	public InputStreamIn(InputStream inputStream) {
		this.inputStream = inputStream;
		this.reader = new LineNumberReader(new InputStreamReader(inputStream));
	}
		
	
	public String readLine() throws DataException {
		try {
			return reader.readLine();
		} catch (IOException e) {
			throw new DataException(e);
		}
	}
	
	@Override
	public InputStream getStream() {
		return inputStream;
	}
	
}

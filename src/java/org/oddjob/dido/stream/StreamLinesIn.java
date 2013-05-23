package org.oddjob.dido.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedeDataInException;
import org.oddjob.dido.text.StringTextIn;
import org.oddjob.dido.text.TextIn;

/**
 * A {@link LinesIn} from an {@link InputStream}.
 * 
 * @author rob
 *
 */
public class StreamLinesIn implements LinesIn {
	
	private final LineTracker lines;
	
	private class LineTracker {
		
		private final LineNumberReader reader;

		private String lastLine;
		
		LineTracker(LineNumberReader reader) throws DataException {
			this.reader = reader;
		}
		
		String readLine() throws DataException {
			try {
				lastLine = reader.readLine();
				return lastLine;
			}
			catch (IOException e) {
				throw new DataException(e);
			}
		}
		
		boolean hasNext() {
			return lastLine != null;
		}
	}
	
	public StreamLinesIn(InputStream inputStream) throws DataException {
		this.lines = new LineTracker(new LineNumberReader(
				new InputStreamReader(inputStream)));
	}
		
	public String readLine() throws DataException {
		return lines.readLine();
	}
	
	
	@Override
	public <T extends DataIn> T provide(Class<T> type) 
	throws DataException{
		
		if (type.isAssignableFrom(LinesIn.class)) {
			return type.cast(new LinesIn() {
				
				boolean used;
				
				@Override
				public <X extends DataIn> X provide(Class<X> type)
						throws DataException {
					return StreamLinesIn.this.provide(type);
				}
				
				@Override
				public String readLine() throws DataException {
					
					if (used) {
						return StreamLinesIn.this.readLine();
					}
					else {
						used = true;
						return lines.lastLine;
					}
				}
			});
		}
		
		if (type.isAssignableFrom(TextIn.class)) {
			if (lines.hasNext()) {
				return type.cast(new StringTextIn(lines.lastLine));
			}
			else {
				throw new DataException("No more lines.");
			}
		}
			
		throw new UnsupportedeDataInException(this.getClass(), type);
	}
}
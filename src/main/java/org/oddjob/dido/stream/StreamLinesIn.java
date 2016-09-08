package org.oddjob.dido.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedDataInException;
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
	
	private int linesRead;
	
	private class LineTracker {
		
		private final LineNumberReader reader;

		private String lastLine;
		
		LineTracker(LineNumberReader reader) throws DataException {
			this.reader = reader;
		}
		
		String readLine() throws DataException {
			try {
				lastLine = reader.readLine();
				if (lastLine != null) {
					++linesRead;
				}
				return lastLine;
			}
			catch (IOException e) {
				throw new DataException(e);
			}
		}
		
		boolean hasNext() {
			return lastLine != null;
		}
		
		void close() throws DataException {
			try {
				reader.close();
			} catch (IOException e) {
				throw new DataException(e);
			}
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
	public <T extends DataIn> T provideDataIn(Class<T> type) 
	throws DataException{
		
		if (type.isAssignableFrom(LinesIn.class)) {			
			return type.cast(new LinesIn() {
				
				private boolean readALineAlready;
				
				@Override
				public <X extends DataIn> X provideDataIn(Class<X> type)
						throws DataException {
					return StreamLinesIn.this.provideDataIn(type);
				}
				
				@Override
				public String readLine() throws DataException {
					
					try {
						if (readALineAlready || lines.lastLine == null) {
							return lines.readLine();
						}
						else {
							return lines.lastLine; 
						}
					}
					finally {
						readALineAlready = true;
					}
				}
				
				@Override
				public int getLinesRead() {
					return linesRead;
				}
				
				@Override
				public void close() throws DataException {
					// Nothing to do as outer class closes stream.
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
			
		throw new UnsupportedDataInException(this.getClass(), type);
	}
	
	@Override
	public int getLinesRead() {
		return linesRead;
	}
	
	@Override
	public void close() throws DataException {
		lines.close();
	}
}

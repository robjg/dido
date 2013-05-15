package org.oddjob.dido.stream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataInProvider;
import org.oddjob.dido.DataOutProvider;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.LayoutBase;
import org.oddjob.dido.UnsupportedeDataInException;
import org.oddjob.dido.UnsupportedeDataOutException;

public class LinesLayout extends LayoutBase<String> {

	private DataReader reader;
	
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	class LineReader implements DataReader { 

		final DataReader delegate;

		final LinesIn linesIn;
		
		public LineReader(DataReader delegate, LinesIn linesIn) {
			this.delegate = delegate;
			this.linesIn = linesIn;
		}
		
		@Override
		public Object read() throws DataException {
			String line = linesIn.readLine();
			if (line == null) {
				return null;
			}
			else {
				value(line);
				return delegate.read();				
			}
		}
	}
	
	@Override
	public DataReader readerFor(DataInProvider dataInProvider) throws UnsupportedeDataInException {
		
		if (reader == null) {
			
			final LinesIn linesIn = dataInProvider.provideIn(LinesIn.class);

			DataReader delegate = downOurOutReader(linesIn);
			reader = new LineReader(delegate, linesIn);
		}

		return reader;
	}
	
	@Override
	public DataWriter writerFor(DataOutProvider dataOutProvider)
			throws UnsupportedeDataOutException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void close() {
		reader = null;
		value(null);
	}
}

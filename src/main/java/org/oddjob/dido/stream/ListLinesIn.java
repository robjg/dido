package org.oddjob.dido.stream;

import java.util.Iterator;

import org.oddjob.arooa.utils.Iterables;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedDataInException;
import org.oddjob.dido.text.StringTextIn;
import org.oddjob.dido.text.StringsIn;
import org.oddjob.dido.text.TextIn;

public class ListLinesIn implements LinesIn, StringsIn {

	private final Iterable<String> list;
	
	private final Iterator<String> lines;

	private String lastLine;

	private int linesRead;
	
	public ListLinesIn(Iterable<String> list) {
		this.list = list;
		this.lines = list.iterator();
	}

	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type)
			throws UnsupportedDataInException {

		if (type.isAssignableFrom(LinesIn.class)) {
			return type.cast(this);
		}

		if (type.isAssignableFrom(TextIn.class)) {
			return type.cast(new StringTextIn(lastLine));
		}

		throw new UnsupportedDataInException(getClass(), type);
	}

	@Override
	public String readLine() throws DataException {
		
		if (lines.hasNext()) {
			lastLine = lines.next();
			++linesRead;
		} 
		else {
			lastLine = null;
		}
		
		return lastLine;
	}
	
	@Override
	public String[] getValues() {
		return Iterables.toArray(list, String.class);
	}
	
	@Override
	public int getLinesRead() {
		return linesRead;
	}
	
	@Override
	public void close() throws DataException {
		// Nothing to close.
	}
}

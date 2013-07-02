package org.oddjob.dido.stream;

import java.util.Iterator;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedeDataInException;
import org.oddjob.dido.text.StringTextIn;
import org.oddjob.dido.text.TextIn;

public class ListLinesIn implements LinesIn {

	private final Iterator<String> lines;

	private String lastLine;

	public ListLinesIn(Iterable<String> list) {
		this.lines = list.iterator();
	}

	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type)
			throws UnsupportedeDataInException {

		if (type.isAssignableFrom(LinesIn.class)) {
			return type.cast(this);
		}

		if (type.isAssignableFrom(TextIn.class)) {
			return type.cast(new StringTextIn(lastLine));
		}

		throw new UnsupportedeDataInException(getClass(), type);
	}

	@Override
	public String readLine() throws DataException {
		if (lines.hasNext()) {
			lastLine = lines.next();
		} else {
			lastLine = null;
		}
		return lastLine;
	}
}

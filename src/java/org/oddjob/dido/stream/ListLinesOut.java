package org.oddjob.dido.stream;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedeDataOutException;
import org.oddjob.dido.text.StringTextOut;
import org.oddjob.dido.text.TextOut;

public class ListLinesOut implements LinesOut {

	private List<String> lines = new ArrayList<String>();
	
	private TextOut textOut;
	
	@Override
	public <T extends DataOut> T provide(Class<T> type)
			throws UnsupportedeDataOutException {
		
		if (type.isAssignableFrom(LinesOut.class)) {
			return type.cast(this);
		}
		
		if (type.isAssignableFrom(TextOut.class)) {
			if (textOut == null) {
				textOut = new StringTextOut();
			}
			return type.cast(textOut);
		}

		throw new UnsupportedeDataOutException(getClass(), type);
	}
	
	@Override
	public void writeLine(String text) throws DataException {
		lines.add(text);
		textOut = null;
	}
	
	
	@Override
	public <T> T toValue(Class<T> type) {
		if (textOut != null) {
			return textOut.toValue(type);
		}
		else {
			return null;
		}
	}

	@Override
	public boolean hasData() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public List<String> getLines() {
		return lines;
	}
}

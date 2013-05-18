package org.oddjob.dido.text;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedeDataInException;

public class MappedFieldsIn
implements FieldsIn {

	private Map<String, Integer> headerToColumn;
	
	private String[] values;
	
	private int nextColumn = 1;
	
	public void setHeadings(String[] headings) {

		if (headings == null) {
			throw new NullPointerException("Null Headings.");
		}
		
		headerToColumn = new HashMap<String, Integer>();

		for (int i = 0; i < headings.length; ++i) {
			headerToColumn.put(headings[i], i + 1);
		}		
	}
	
	public void setValues(String[] values) {
		this.values = values;
	}

	@Override
	public int columnFor(String heading, boolean optional, int column) {
		
		if (column < 1) {
			column = this.nextColumn++;
		}
		else {
			this.nextColumn = column + 1;
		}
		
		if (heading == null || headerToColumn == null) {
			return column;
		}
		
		Integer headerColumn = headerToColumn.get(heading);
		
		if (headerColumn == null) {
			if (optional) {
				return 0;
			}
			else {
				return column;
			}
		}
		else {		
			nextColumn = headerColumn.intValue();
			return nextColumn++;
		}
	}
		
	@Override
	public String getColumn(int column) {
		if (column > values.length ) {
			return null;
		}
		return values[column - 1];
	}
	
	@Override
	public <T extends DataIn> T provide(Class<T> type)
	throws UnsupportedeDataInException {
		if (type.isInstance(this)) {
			return type.cast(this);
		}
		else {
			throw new UnsupportedeDataInException(this.getClass(), type);
		}
	}
}

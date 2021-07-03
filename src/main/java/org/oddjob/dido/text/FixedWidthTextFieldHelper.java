package org.oddjob.dido.text;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.oddjob.dido.field.Field;
import org.oddjob.dido.tabular.Column;

/**
 * A Helper Class for tracking the columns in a fixed width layout.
 * 
 * @see FixedWidthTextFieldsIn
 * 
 * @author rob
 *
 */
public class FixedWidthTextFieldHelper {

	private final NavigableMap<Integer, MutableFixedWidthColumn> startEndPositions = 
			new TreeMap<Integer, MutableFixedWidthColumn>();
	
	/**
	 * Provide the fixed width column information for the field.
	 * 
	 * @param field
	 * 
	 * @return
	 */
	public FixedWidthColumn columnIndexFor(Field field) {
		
		int start = 0;
		int end = -1;
		int length = 0;
		
		if (field instanceof FixedWidthColumn) {
			
			start = ((FixedWidthColumn) field).getIndex();
			length = ((FixedWidthColumn) field).getLength();			
		}
		else if (field instanceof Column){
			
			start = ((Column) field).getIndex();
		}
		
		if (start == 0) {
			Map.Entry<Integer, MutableFixedWidthColumn> last = 
					startEndPositions.lastEntry();
			if (last == null) {
				start = 1;
			}
			else if (last.getValue().end < 1) {
				start = last.getKey() + 1;
			}
			else {
				start = last.getValue().end;
			}
		}
		
		while (true) {
			Integer higher = startEndPositions.higherKey(start);
			if (higher == null) {
				break;
			}
			else {
				startEndPositions.remove(higher);
			}
		}
	
		if (length > 0) {
			end = length + start;
		}			
		
		Map.Entry<Integer, MutableFixedWidthColumn> below = 
				startEndPositions.lowerEntry(start);
		if (below != null && below.getValue().end < 1) {
			below.getValue().end = start;
		}
		
		MutableFixedWidthColumn column = new
				MutableFixedWidthColumn(start, end);
		
		startEndPositions.put(start, column);
		
		return column;
	}
	
	static class MutableFixedWidthColumn implements FixedWidthColumn {
		
		private final int start;
		private int end;
		
		public MutableFixedWidthColumn(int start, int end) {
			this.start = start;
			this.end = end;
		}
		
		@Override
		public int getIndex() {
			return start;
		}
		
		@Override
		public int getLength() {
			if (end < 1) {
				return -1;
			}
			else {
				return end - start;
			}
		}
		
		@Override
		public String getLabel() {
			return null;
		}	
	}
}

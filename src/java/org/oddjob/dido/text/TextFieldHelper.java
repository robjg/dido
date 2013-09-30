package org.oddjob.dido.text;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.oddjob.dido.field.Field;
import org.oddjob.dido.tabular.Column;

public class TextFieldHelper {

	private final NavigableMap<Integer, Integer> startEndPositions = 
			new TreeMap<Integer, Integer>();
	
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
				
		if (field instanceof FixedWidthColumn) {
			
			start = ((FixedWidthColumn) field).getColumnIndex();
			int length = ((FixedWidthColumn) field).getLength();
			
			if (length > 0) {
				end = length + start;
			}			
		}
		else if (field instanceof Column){
			
			start = ((Column) field).getColumnIndex();
		}
		
		if (start == 0) {
			Map.Entry<Integer, Integer> last = startEndPositions.lastEntry();
			if (last == null) {
				start = 1;
			}
			else if (last.getValue() < 1) {
				start = last.getKey() + 1;
			}
			else {
				start = last.getValue();
			}
		}
		
		Map.Entry<Integer, Integer> below = startEndPositions.lowerEntry(start);
		if (below != null) {
			if (below.getValue() > start || below.getValue() < 1) {
				startEndPositions.put(below.getKey(), start);
			}
		}
		
		Integer above = startEndPositions.higherKey(start);
		if (above != null) {
			if (end < start || end >= above.intValue()) {
				end = above.intValue();
			}
		}
		
		startEndPositions.put(start, end);
		
		final int finalStart = start;
		
		return new FixedWidthColumn() {
			
			@Override
			public int getLength() {
				int endPos = startEndPositions.get(finalStart);
				if (endPos < 1) {
					return -1;
				}
				else {
					return endPos - finalStart;
				}
			}
			
			@Override
			public int getColumnIndex() {
				return finalStart;
			}
		};
	}
	
}

package org.oddjob.dido.tabular;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.field.Field;

public class ColumnHelper {

	private Map<String, Integer> indexForHeading;
	
	private final Map<Integer, String> headings = 
			new HashMap<Integer, String>();
	
	private int lastColumn;
	
	private int maxColumn;
	
	/**
	 * Create a new instance without headings.
	 */
	public ColumnHelper() {
	}
	
	/**
	 * Create a new instance with headings if provided.
	 * 
	 * @param headings The headings. May be null.
	 */
	public ColumnHelper(String[] headings) {
		setHeadings(headings);
	}
	
	/**
	 * Set the heading to use.
	 * 
	 * @param headings An array. May be null.
	 */
	public void setHeadings(String[] headings) {
		
		if (headings == null) {
			indexForHeading = null;
		}
		else {
			indexForHeading = new HashMap<String, Integer>();
			
			for (int i = 0; i < headings.length; ++i) {
				
				Integer columnIndex = new Integer(i + 1);
				String heading = headings[i];
				this.indexForHeading.put(heading, columnIndex);
				this.headings.put(columnIndex, heading);
			}
		}
	}
	
	/**
	 * The headings as an array.
	 * 
	 * @return An array. Null if no headings have been set.
	 */
	public String[] getHeadings() {
		return toArray(headings);
	}
	
	/**
	 * The last column provided.
	 * 
	 * @return The column index. 0 if none have been provided.
	 */
	public int getLastColumn() {
		return lastColumn;
	}
	
	/**
	 * Get the highest column index provided.
	 * 
	 * @return The column index. 0 if none have been provided.
	 */
	public int getMaxColumn() {
		return maxColumn;
	}
	
	/**
	 * Provide the column index for the column.
	 * 
	 * @param column
	 * @return
	 */
	public int columnIndexFor(Field field) {
		
		String heading = field.getLabel();
		int useColumnIndex = 0;
		
		if (indexForHeading != null && heading != null) {
			
			Integer headingColumn = indexForHeading.get(heading);
			if (headingColumn == null) {
				useColumnIndex = 0;
			}
			else {
				useColumnIndex = headingColumn.intValue();
				lastColumn = useColumnIndex;
			}
		}
		else if (field instanceof Column){
			
			Column column = (Column) field;
			
			useColumnIndex = column.getIndex();
		
			if (useColumnIndex < 0) {
				throw new IllegalArgumentException("Column Index of " + 
						useColumnIndex + " is invalid.");
			}
			if (useColumnIndex == 0) {
				useColumnIndex = ++lastColumn;
			}
			else {
				lastColumn = useColumnIndex;
			}
			
			if (heading != null) {
				headings.put(useColumnIndex, heading);
			}	
		}

		if (useColumnIndex > maxColumn) {
			maxColumn = useColumnIndex;
		}
		
		return useColumnIndex;
	}
	
	/**
	 * Helper method to convert a map keyed by an integer that is the index
	 * of the thing. Not that things with a key of less than 1 will be 
	 * ignored.
	 * 
	 * @param things Map of things.
	 * @return An array of all the things who's key is greater than 0. This
	 * will not be null but may contain null elements.
	 */
	public static String[] toArray(Map<Integer, String> things) {
		if (things == null) {
			return null;
		}
		
		int maxColumn = 0;
		for (Integer i : things.keySet() ) {
			int index = i.intValue();
			if (index > maxColumn) {
				maxColumn = index;
			}
		}
		
		String[] a = new String[maxColumn];
		
		for (int i = 0; i < maxColumn; ++i) {
			String thing = things.get(i + 1);
			a[i] = thing;
		}
		
		return a;
	}
	
}

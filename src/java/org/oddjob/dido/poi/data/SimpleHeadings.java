package org.oddjob.dido.poi.data;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Manage Heading for {@link RowsIn}.
 * 
 * @author rob
 *
 */
class SimpleHeadings {
	private static final Logger logger = Logger.getLogger(SimpleHeadings.class);
	
	private final Map<String, Integer> headings;
	
	private int columnCursor = 0;
	
	/**
	 * Create a new Instance.
	 * 
	 * @param row The row with the headings on.
	 * @param offset The column offset. 0 for none which means the first
	 * column will have a position of 1 but come from Poi column 0.
	 */
	public SimpleHeadings(Row row, int offset) {
		
		headings = new LinkedHashMap<String, Integer>();
			
		for (int columnPosition = 1; true; ++columnPosition) {
			
			Cell cell = row.getCell(offset + columnPosition - 1);

			if (cell == null) {
				break;
			}
			
			if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
				break;
			}
			
			String title = cell.getStringCellValue();
			Integer existing = headings.get(title);
			
			if (existing != null) {
				logger.warn("Heading " + title + " is duplicated in columns " +
						existing + " and " + columnPosition + 
						". The first first column will be ignored.");
			}
			
			headings.put(title, new Integer(columnPosition));
		}
		
		if (logger.isDebugEnabled()) {
			StringBuilder text = new StringBuilder();
			for (Map.Entry<String, Integer> entry : headings.entrySet()) {
				if (text.length() > 0) {
					text.append(", ");
				}
				text.append(entry.getKey() + "=" + entry.getValue());
			}
			logger.debug("Processed Headings: " + text.toString());
		}
	}
	
	/**
	 * Get the column position for the given heading. If heading is null
	 * then the next column position is used.
	 * 
	 * @param heading
	 * 
	 * @return The column position of the heading. 0 if the heading
	 * isn't in the row.
	 */
	public int position(String heading) {
		if (heading == null || this.headings == null) {
			return ++columnCursor;
		}
		Integer column = headings.get(heading);
		if (column == null) {
			return 0;
		}
		else {
			return column.intValue();
		}
	}
}

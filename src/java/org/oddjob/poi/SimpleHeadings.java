package org.oddjob.poi;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

class SimpleHeadings {
	private static final Logger logger = Logger.getLogger(SimpleHeadings.class);
	
	private final Map<String, Integer> headings;
	
	int columnCursor = 0;
	
	public SimpleHeadings(int firstColumn) {
		headings = null;
		columnCursor = firstColumn;
	}
	
	public SimpleHeadings(Row row, int firstColumn) {
		
		headings = new LinkedHashMap<String, Integer>();
			
		for (int i = firstColumn; true; ++i) {
			Cell cell = row.getCell(i);
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
						existing + " and " + i + 
						". The first first column will be ignored.");
			}
			headings.put(title, new Integer(i));
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
	
	public int position(String heading) {
		if (heading == null || this.headings == null) {
			return columnCursor++;
		}
		Integer column = headings.get(heading);
		if (column == null) {
			return -1;
		}
		else {
			return column.intValue();
		}
	}
}

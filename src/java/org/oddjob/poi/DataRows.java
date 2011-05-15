package org.oddjob.poi;

import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellRangeAddress;
import org.oddjob.dido.AbstractParent;
import org.oddjob.dido.DataDriver;
import org.oddjob.dido.DataException;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;

public class DataRows 
extends AbstractParent<SheetIn, SheetIn, SheetOut, SheetOut>
implements DataDriver {
	private static final Logger logger = Logger.getLogger(DataRows.class);
	
	private int firstRow;
	
	private int firstColumn;
	
	private int lastRow;
	
	private int lastColumn;
	
	private boolean withHeadings;

	private String headingsStyle;
	
	private boolean autoWidth;
	
	private boolean autoFilter;
	
	private boolean begun;	
		
	@Override
	public WhereNextIn<SheetIn> in(
			SheetIn din) throws DataException {
		
		if (!begun) {
			
			din.startAt(firstRow, firstColumn);
			
			if (withHeadings) {
				if (din.headerRow()) {
					fireChildrenBegin(din);
				}
				else {
					// No more rows
					return null;
				}
			}
			else {
				fireChildrenBegin(din);
			}
			
			begun = true;
		}
		
		if (!din.nextRow()) {
			return null;
		}
		else {
			logger.debug("[" + toString() + "] reading row " + 
					din.getCurrentRow());
			
			return new WhereNextIn<SheetIn>(childrenToArray(), 
					din);
		}
	}

	@Override
	public void complete(SheetIn in) throws DataException {
		lastRow = in.getCurrentRow();
		lastColumn = in.getLastColumn();
		
		fireChildrenEnd(in);
		begun = false;
	}
	
	@Override
	public WhereNextOut<SheetOut> out(
			SheetOut dout) throws DataException {
		
		if (!begun) {
			dout.startAt(firstRow, firstColumn);
			
			if (withHeadings) {
				dout.headerRow(headingsStyle);
			}
			fireChildrenBegin(dout);
			begun = true;
		}
		
		dout.nextRow();
		
		logger.debug("[" + toString() + "] writing row " + 
				dout.getCurrentRow());
		
		return new WhereNextOut<SheetOut>(childrenToArray(), 
				dout);
	}

	@Override
	public void complete(SheetOut dout) throws DataException {
		lastRow = dout.getCurrentRow();
		lastColumn = dout.getLastColumn();

		fireChildrenEnd(dout);
		
		if (autoFilter) {
			dout.getTheSheet().setAutoFilter(
					new CellRangeAddress(
							firstRow, lastRow,
							firstColumn, lastColumn));
		}
		if (autoWidth) {
			for (int i = 0; i <= dout.getLastColumn(); ++i) {
				dout.getTheSheet().autoSizeColumn(i);
			}
		}
		begun = false;
	}
	
	public boolean isWithHeadings() {
		return withHeadings;
	}

	public void setWithHeadings(boolean withHeading) {
		this.withHeadings = withHeading;
	}

	public boolean isAutoWidth() {
		return autoWidth;
	}

	public void setAutoWidth(boolean autoWidth) {
		this.autoWidth = autoWidth;
	}

	public String getHeadingsStyle() {
		return headingsStyle;
	}

	public void setHeadingsStyle(String headingsStyle) {
		this.headingsStyle = headingsStyle;
	}
	
	@Override
	public String toString() {
		String name = getName();
		return getClass().getSimpleName() + 
				(name == null ? "" : " " + name);
	}

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int startRow) {
		this.firstRow = startRow;
	}

	public int getFirstColumn() {
		return firstColumn;
	}

	public void setFirstColumn(int startColumn) {
		this.firstColumn = startColumn;
	}

	public boolean isAutoFilter() {
		return autoFilter;
	}

	public void setAutoFilter(boolean autoFilter) {
		this.autoFilter = autoFilter;
	}

	public int getLastRow() {
		return lastRow;
	}

	public int getLastColumn() {
		return lastColumn;
	}
}

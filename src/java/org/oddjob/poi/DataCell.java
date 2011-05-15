package org.oddjob.poi;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.dido.AbstractStencil;
import org.oddjob.dido.BoundedDataNode;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;

abstract public class DataCell<T> 
extends AbstractStencil<T, SheetIn, DataIn, SheetOut, DataOut>
implements BoundedDataNode<SheetIn, DataIn, SheetOut, DataOut>,
		ArooaSessionAware {
	private static final Logger logger = Logger.getLogger(DataCell.class);
	
	private ArooaConverter converter;
	
	private String style;
		
	private int column = -1;
	
	private String title;
	
	private SheetData sheet;
	
	
	abstract protected int getCellType();
	
	@Override
	@ArooaHidden
	public void setArooaSession(ArooaSession session) {
		this.converter = session.getTools().getArooaConverter();
	}

	protected ArooaConverter getConverter() {
		return converter;
	}

	abstract protected void extractCellValue(Cell cell)
	throws DataException;
	

	@Override
	public void begin(SheetIn in) {
		this.sheet = in;
		column = in.columnFor(title);
		logger.info("[" + toString() + "] is column " + column);
	}
	
	@Override
	public WhereNextIn<DataIn> in(
			SheetIn data) throws DataException {
		
		if (column < 0) {
			return null;
		}
		
		
		Cell cell = data.getCell(column);
		
		if (cell == null) {
			throw new NullPointerException("Cell in row " + 
					data.getCurrentRow() + ", column " + column + 
					" is null");
		}
		
		try {
			extractCellValue(cell);
			
			logger.debug("[" + this + "] read [" + value() + "]");
		}
		catch (RuntimeException e) {
			throw new DataException("Failed extracting cell value in row " +
					data.getCurrentRow() + ", column " + column, e);
		}
		
		return new WhereNextIn<DataIn>();
	}

	@Override
	public void end(SheetIn in) {
		this.sheet = null;
	}
	
	abstract protected void insertValueInto(Cell cell)
	throws DataException;
		
	public String getDefaultStyle() {
		return null;
	}
	
	@Override
	public void begin(SheetOut out) {
		this.sheet = out;
		
		column = out.writeHeading(title);
	}
	
	@Override
	public WhereNextOut<DataOut> out(
			SheetOut data) throws DataException {
		
		Cell cell = data.createCell(column, getCellType());
		
		insertValueInto(cell);
		
		String style = this.style;
		if (style == null) {
			style = getDefaultStyle();
		}
		if (style != null) {
			CellStyle cellStyle = data.styleFor(style);
			
			if (cellStyle == null) {
				throw new DataException("No style available of name [" + 
						style + "] from cell [" + toString() + "]");
			}
			
			cell.setCellStyle(cellStyle);
		}
		
		logger.debug("[" + this + "] wrote [" + value() + "]");
		
		return new WhereNextOut<DataOut>();
	}

	@Override
	public void end(SheetOut out) {
		this.sheet = null;
	}
	
	public String getReference() {
		SheetData sheet = this.sheet;
		if (sheet == null) {
			return null;
		}
		return new CellReference(sheet.getCurrentRow(), column).formatAsString();
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getColumn() {
		return column;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}	
	
	public String toString() {
		if (title != null) {
			return title;
		}
		String name = getName();
		if (name != null) {
			return name;
		}
		return getClass().getSimpleName();
	}
}

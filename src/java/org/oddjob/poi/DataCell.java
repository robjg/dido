package org.oddjob.poi;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.layout.LayoutValueNode;
import org.oddjob.dido.layout.NullReader;
import org.oddjob.dido.layout.NullWriter;
import org.oddjob.dido.text.StringTextOut;
import org.oddjob.dido.text.TextOut;

abstract public class DataCell<T> 
extends LayoutValueNode<T>
implements ArooaSessionAware {
	
	private static final Logger logger = Logger.getLogger(DataCell.class);
	
	private ArooaConverter converter;
	
	private String style;
		
	private int column = -1;
	
	private String title;
	
	private SheetData sheet;
	
	private boolean initialised;
	
	private DataReader reader;
	
	private DataWriter writer;
	
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
	
	class MainReader implements DataReader {
		
		private final SheetIn sheetIn;
		
		private DataReader nextReader;
		
		public MainReader(SheetIn sheetIn) {
			this.sheetIn = sheetIn;
		}
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {
				Object value = nextReader.read();
				
				return value;
			}
			
			Cell cell = sheetIn.getCell(column);
			
			if (cell == null) {
				throw new NullPointerException("Cell in row " + 
						sheetIn.getCurrentRow() + ", column " + column + 
						" is null");
			}
			
			try {
				extractCellValue(cell);
				
				logger.debug("[" + this + "] read [" + value() + "]");
			}
			catch (RuntimeException e) {
				throw new DataException("Failed extracting cell value in row " +
						sheetIn.getCurrentRow() + ", column " + column, e);
			}
			
			nextReader = nextReaderFor(null);
			
			return read();
		}
	}
	
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {

		SheetIn in = dataIn.provide(SheetIn.class);
		
		if (!initialised) {
			
			this.sheet = in;
			column = in.columnFor(title);
			logger.info("[" + toString() + "] is column " + column);
			initialised = true;
		}

		if (column < 0) {
			return new NullReader();
		}
		
		if (reader == null) {
			reader = new MainReader(in);
		}
		
		return new DataReader() {
			public Object read() throws DataException {
				Object value = reader.read();
				if (value == null) {
					reader = null;
				}
				return value;
			}
		};
	}
	
	
	abstract protected void insertValueInto(Cell cell)
	throws DataException;
		
	public String getDefaultStyle() {
		return null;
	}
	

	class MainWriter implements DataWriter {

		private final SheetOut data;
		
		private DataWriter nextWriter;
		
		private TextOut textOut;
		
		public MainWriter(SheetOut outgoing) {
			this.data = outgoing; 
		}
		
		@Override
		public boolean write(Object value) throws DataException {

			if (nextWriter == null) {
				
				value(null);
				textOut = new StringTextOut();
				nextWriter = nextWriterFor(textOut);
			}
			
			if (nextWriter.write(value)) {
				return true;
			}
			
			
			if (value() != null ) {
				Cell cell = data.createCell(column, getCellType());
				
				insertValueInto(cell);
				
				String style = DataCell.this.style;
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
			}	
			
			writer = null;

			return false;
		}
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {

		SheetOut out = dataOut.provide(SheetOut.class);
		
		if (writer == null) {
			
			String heading = title;
			
			if (heading == null) {
				heading = getName();
			}
			
			this.sheet = out;
			
			column = out.writeHeading(title);
			
			if (column == 0) {
				
				writer = new NullWriter();
			}
			else {
				
				writer = new MainWriter(out);
			}
		}		
		
		return writer;
	}
	
	@Override
	public void reset() {
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

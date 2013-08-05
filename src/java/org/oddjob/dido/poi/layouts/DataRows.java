package org.oddjob.dido.poi.layouts;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.layout.LayoutNode;
import org.oddjob.dido.layout.NullReader;
import org.oddjob.dido.morph.MorphDefinition;
import org.oddjob.dido.morph.MorphProvider;
import org.oddjob.dido.morph.Morphable;
import org.oddjob.dido.poi.RowsIn;
import org.oddjob.dido.poi.RowsOut;
import org.oddjob.dido.poi.SheetIn;
import org.oddjob.dido.poi.SheetOut;
import org.oddjob.dido.poi.data.PoiRowsIn;
import org.oddjob.dido.poi.data.PoiRowsOut;

public class DataRows extends LayoutNode 
implements Morphable, MorphProvider {
	
	private static final Logger logger = Logger.getLogger(DataRows.class);
	
	private final static Set<Class<?>> NUMERIC_PRIMATIVES = 
			new HashSet<Class<?>>();
		
	static {
		
		NUMERIC_PRIMATIVES.add(byte.class);
		NUMERIC_PRIMATIVES.add(short.class);
		NUMERIC_PRIMATIVES.add(int.class);
		NUMERIC_PRIMATIVES.add(long.class);
		NUMERIC_PRIMATIVES.add(float.class);
		NUMERIC_PRIMATIVES.add(double.class);
	}
	
	private int firstRow;
	
	private int firstColumn;
	
	private int lastRow;
	
	private int lastColumn;
	
	private boolean withHeadings;

	private String headingsStyle;
	
	private boolean autoWidth;
	
	private boolean autoFilter;
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	public Runnable morphInto(MorphDefinition morphicness) {
		
		if (childLayouts().size() > 0) {
			logger.debug("[" + this + "] has children. Morphicness ignored.");
			
			return new Runnable() {
				@Override
				public void run() {
				}
			};
		}
		
		String[] properties = morphicness.getNames();
		
		int i = 0;
		for (String property : properties) {
			
			Class<?> propertyType = morphicness.typeOf(property);
			
			DataCell<?> cell;
			
			if (NUMERIC_PRIMATIVES.contains(propertyType) || 
					Number.class.isAssignableFrom(propertyType)) {
				cell = new NumericCell();
			}
			else if (Boolean.class == propertyType || 
					boolean.class == propertyType) {
				cell = new BooleanCell();
			}
			else if (Date.class.isAssignableFrom(propertyType)) {
				cell = new DateCell();
			}
			else {
				cell = new TextCell();
			}
			
			cell.setName(property);
			cell.setTitle(morphicness.titleFor(property));
			
			logger.debug("[" + this + "] adding morphicness [" + cell + "]");
			
			setOf(i++, cell);
		}
		
		return new Runnable() {
			
			@Override
			public void run() {
				childLayouts().clear();
			}
		};
	}
			
	@Override
	public MorphDefinition morphOf() {
	
		return null;
	}
	
	class MainReader implements DataReader {
	
		private final RowsIn rowsIn; 
		
		private DataReader nextReader;
		
		public MainReader(RowsIn rowsIn) {
			this.rowsIn = rowsIn;
		}
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {
				Object value = nextReader.read();
				
				if (value != null) {
					
					lastRow = rowsIn.getLastRow();
					lastColumn = rowsIn.getLastColumn();
					
					return value;
				}
			}
			
			if (!rowsIn.nextRow()) {
				return null;
			}

			logger.debug("[" + DataRows.this + "] reading row " + 
						rowsIn.getLastRow());

			nextReader = nextReaderFor(rowsIn);
			
			return read();
		}
		
		@Override
		public void close() throws DataException {
			if (nextReader != null) {
				nextReader.close();
				nextReader = null;
			}
			
			logger.debug("[" + DataRows.this +  
					"] closed reader at row, column [" + lastRow + 
					", " + lastColumn + "]");
		}
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		SheetIn sheetIn = dataIn.provideDataIn(SheetIn.class);
		
		RowsIn rowsIn = new PoiRowsIn(sheetIn, firstRow, firstColumn);
		
		if (withHeadings) {
			
			if (rowsIn.headerRow()) {
				logger.debug("[" + this + "] Read headings.");
			}
			else {
				return new NullReader();
			}
		}
		else {
			logger.debug("[" + this + "] No headings.");
		}
		
		return new MainReader(rowsIn);
	}

	class MainWriter implements DataWriter {
		
		private final RowsOut rowsOut;
		
		private DataWriter nextWriter;
		
		public MainWriter(RowsOut dout) {
			this.rowsOut = dout;
		}
		
		@Override
		public boolean write(Object object) throws DataException {
			
			if (nextWriter == null) {

				rowsOut.nextRow();
				
				nextWriter = nextWriterFor(rowsOut);
			}
			
			logger.trace("[" + DataRows.this + "] writing row " + 
					rowsOut.getLastRow());
			
			boolean keep = nextWriter.write(object);
			
			lastRow = rowsOut.getLastRow();
			lastColumn = rowsOut.getLastColumn();

			if (!keep) {
				
				nextWriter.close();
				nextWriter = null;
			}

			// Want to be kept by parent and given more data.
			return true;
		}
		
		@Override
		public void close() throws DataException {
			
			if (nextWriter != null) {
				nextWriter.close();
				nextWriter = null;
			}
				
			if (autoFilter) {
				rowsOut.autoFilter();
			}
			
			if (autoWidth) {
				rowsOut.autoWidth();
			}
			
			logger.debug("[" + DataRows.this +  
					"] closed writer at row, column [" + lastRow + 
					", " + lastColumn + "]");
		}
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {

		SheetOut sheetOut = dataOut.provideDataOut(SheetOut.class);
		
		logger.debug("Creating writer for [" + sheetOut + "]");

		PoiRowsOut rowsOut = new PoiRowsOut(sheetOut, firstRow, firstColumn);
		
		if (withHeadings) {
			rowsOut.headerRow(headingsStyle);
			
			logger.debug("[" + this + "] initialsed at [" + 
					firstRow + ", " + firstColumn + "]");
		}
		
		return new MainWriter(rowsOut);
	}

	@Override
	public void reset() {
		super.reset();
		
		lastRow = 0;
		lastColumn = 0;
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

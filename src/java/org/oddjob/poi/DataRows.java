package org.oddjob.poi;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellRangeAddress;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.Morphicness;
import org.oddjob.dido.io.ClassMorphic;
import org.oddjob.dido.layout.LayoutNode;
import org.oddjob.dido.layout.NullReader;

public class DataRows extends LayoutNode 
implements ClassMorphic {
	
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
	
	private boolean initialised;	
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	public Runnable beFor(Morphicness morphicness) {
		
		if (initialised) {
			throw new IllegalStateException(
					"QuickSheet Already initialised.");
		}
		
		setWithHeadings(true);
		
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
			
			setOf(i++, cell);
		}
		
		initialised = true;
		
		return new Runnable() {
			
			@Override
			public void run() {
				childLayouts().clear();
			}
		};
	}
		

	
	class MainReader implements DataReader {
	
		private final SheetIn din; 
		
		private DataReader nextReader;
		
		public MainReader(SheetIn din) {
			this.din = din;
		}
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {
				Object value = nextReader.read();
				
				if (value != null) {
					return value;
				}
			}
			
			if (!din.nextRow()) {
				return null;
			}

			logger.debug("[" + toString() + "] reading row " + 
						din.getCurrentRow());

			nextReader = nextReaderFor(din);
			
			return read();
		}
		
		public void close() {
			lastRow = din.getCurrentRow();
			lastColumn = din.getLastColumn();
		}
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		SheetIn din = dataIn.provide(SheetIn.class);
		
		if (!initialised) {
			
			din.startAt(firstRow, firstColumn);
			
			if (withHeadings) {
				if (din.headerRow()) {
					// What happens here?
				}
				else {
					return new NullReader();
				}
			}
			
			initialised = true;
		}
		
		return new MainReader(din);
	}

	class MainWriter implements DataWriter {
		
		private final SheetOut dout;
		
		private DataWriter nextWriter;
		
		public MainWriter(SheetOut dout) {
			this.dout = dout;
		}
		
		@Override
		public boolean write(Object object) throws DataException {
			
			if (nextWriter == null) {

				nextWriter = nextWriterFor(dout);
			}
			
			if (nextWriter.write(object)) {
				return true;
			}
			else {
				
				nextWriter = null;

				dout.nextRow();
			
				logger.debug("[" + toString() + "] writing row " + 
						dout.getCurrentRow());
				
				return false;
			}
		}
		
		public void close() {
			lastRow = dout.getCurrentRow();
			lastColumn = dout.getLastColumn();

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
		}
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {

		SheetOut dout = dataOut.provide(SheetOut.class);
		
		if (!initialised) {
			dout.startAt(firstRow, firstColumn);
			
			if (withHeadings) {
				dout.headerRow(headingsStyle);
			}
			initialised = true;
		}
		
		return new MainWriter(dout);
	}

	@Override
	public void reset() {
		initialised = false;
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

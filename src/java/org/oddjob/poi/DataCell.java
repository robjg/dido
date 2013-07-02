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

abstract public class DataCell<T> 
extends LayoutValueNode<T>
implements ArooaSessionAware {
	
	private static final Logger logger = Logger.getLogger(DataCell.class);
	
	private ArooaConverter converter;
	
	private String style;
		
	private int index = -1;
	
	private String title;
	
	private String reference;
	
	private boolean initialised;
	
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
		
		private final TupleIn tupleIn;
		
		private DataReader nextReader;
		
		public MainReader(TupleIn tupleIn) {
			this.tupleIn = tupleIn;
		}
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {
				
				Object value = nextReader.read();
				
				if (value == null) {
					nextReader.close();
				}
				
				return value;
			}
			
			Cell cell = tupleIn.getCell(index);
			
			if (cell == null) {
				
				throw new NullPointerException("Cell index " + 
						index + " is null");
			}
			
			try {
				extractCellValue(cell);
				
				logger.debug("[" + DataCell.this + "] read [" + value() + "]");
			}
			catch (RuntimeException e) {
				
				throw new DataException("Failed extracting cell value in row " +
						cell.getRowIndex() + ", column " + 
						cell.getColumnIndex(), e);
			}
			
			setReferenceFrom(cell);
			
			nextReader = nextReaderFor(null);
			
			return read();
		}
		
		@Override
		public void close() throws DataException {
			
			if (nextReader != null) {
				nextReader.close();
				nextReader = null;
			}
		}
	}
	
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {

		TupleIn tupleIn = dataIn.provideDataIn(TupleIn.class);
		
		logger.debug("Creating reader for [" + tupleIn + "]");
		
		if (!initialised) {
			
			index = tupleIn.indexForHeading(title);
			
			logger.info("[" + this + "] is column " + index);
			
			initialised = true;
		}

		if (index < 0) {
			return new NullReader();
		}
		else {
			return new MainReader(tupleIn);
		}
		
	}
	
	
	abstract protected void insertValueInto(Cell cell)
	throws DataException;
		
	public String getDefaultStyle() {
		return null;
	}
	

	class MainWriter implements DataWriter {

		private final TupleOut data;
		
		private DataWriter nextWriter;
		
		public MainWriter(TupleOut outgoing) {
			this.data = outgoing; 
		}
		
		@Override
		public boolean write(Object object) throws DataException {

			if (nextWriter == null) {
				
				value(null);
				
				nextWriter = nextWriterFor(null);
			}
			
			boolean keep = nextWriter.write(object);
							
			Cell cell = data.createCell(index, getCellType());
			
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
			
			logger.trace("[" + DataCell.this + "] wrote [" + value() + "]");
			
			setReferenceFrom(cell);
			
			if (!keep) {
				nextWriter.close();
				nextWriter = null;
			}
			
			return keep;
		}
		
		@Override
		public void close() throws DataException {
			
			if (nextWriter != null) {
				nextWriter.close();
				nextWriter = null;
			}
		}
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {

		TupleOut tupleOut = dataOut.provideDataOut(TupleOut.class);
		
		logger.debug("Creating new column writer for [" + tupleOut + "]");
		
		if (!initialised) {
			
			String heading = title;
			
			if (heading == null) {
				heading = getName();
			}
			
			this.index = tupleOut.indexForHeading(heading);
			
			this.initialised = true;

			logger.debug("[" + this + "] initialsed for column [" + index + "]");
		}		
		
		if (index < 0) {
			
			return new NullWriter();
		}
		else {
			
			return new MainWriter(tupleOut);
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		index = -1;
		initialised = false;
	}
	
	private void setReferenceFrom(Cell cell) {
		
		reference = new CellReference(
				cell.getRowIndex(), cell.getColumnIndex()
					).formatAsString();
	}
	
	public String getReference() {
		return reference;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIndex() {
		return index;
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

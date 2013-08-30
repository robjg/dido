package org.oddjob.dido.poi.layouts;

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
import org.oddjob.dido.layout.VoidIn;
import org.oddjob.dido.layout.VoidOut;
import org.oddjob.dido.poi.CellIn;
import org.oddjob.dido.poi.CellOut;
import org.oddjob.dido.poi.TupleIn;
import org.oddjob.dido.poi.TupleOut;
import org.oddjob.dido.poi.data.CellLayout;
import org.oddjob.dido.tabular.Column;

/**
 * Shared implementation base class for cells.
 * 
 * @author rob
 *
 * @param <T> The type of the cell.
 */
abstract public class DataCell<T> extends LayoutValueNode<T>
implements ArooaSessionAware, Column, CellLayout<T> {
	
	private static final Logger logger = Logger.getLogger(DataCell.class);
	
	/** Converter for converting value to cell type. */
	private ArooaConverter converter;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The name of the style to use. The style will have
	 * been defined with the {@link DataBook} definition.
	 * @oddjob.required No.
	 */
	private String style;
		
	/**
	 * @oddjob.property
	 * @oddjob.description The 1 based column index of this layout.
	 * @oddjob.required Read only.
	 */
	private int columnIndex;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The title of this column.
	 * @oddjob.required No.
	 */
	private String label;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The Excel reference of the last row of this
	 * column that has been written.
	 * @oddjob.required Read only.
	 */
	private String reference;
	
	private CellIn<T> columnIn;
	
	private CellOut<T> columnOut;
	
	private boolean initialised;
	
	/**
	 * @oddjob.property cellType
	 * @oddjob.description The Excel type of this column.
	 * @oddjob.required Read only.
	 */
	@Override
	abstract public int getCellType();
	
	@Override
	@ArooaHidden
	public void setArooaSession(ArooaSession session) {
		this.converter = session.getTools().getArooaConverter();
	}

	/**
	 * Provide sub classes access to the converter.
	 * 
	 * @return
	 */
	protected ArooaConverter getConverter() {
		return converter;
	}

	/**
	 * Sub classes must override this to extract the value from the
	 * cell.
	 * 
	 * @param cell
	 * @throws DataException
	 */
	@Override
	abstract public T extractCellValue(Cell cell)
	throws DataException;
	
	/**
	 * Data Reader for the cell.
	 */
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
			
			Cell cell = tupleIn.getCell(columnIndex);
			
			if (cell == null) {
				
				throw new NullPointerException("Cell index " + 
						columnIndex + " is null");
			}
			
			try {
				value(extractCellValue(cell));
				
				logger.debug("[" + DataCell.this + "] read [" + value() + "]");
			}
			catch (RuntimeException e) {
				
				throw new DataException("Failed extracting cell value in row " +
						cell.getRowIndex() + ", column " + 
						cell.getColumnIndex(), e);
			}
			
			setReferenceFrom(cell);
			
			nextReader = nextReaderFor(new VoidIn());
			
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
	
	class MainReader2 implements DataReader {
		
		private DataReader nextReader;
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {

				return nextReader.read();
			}
			
			T field = columnIn.getData();
			
			reference = columnIn.getCellReference();
			
			value(field);

			logger.trace("[" + DataCell.this + "] value is [" + 
					field + "]");
			
			nextReader = nextReaderFor(new VoidIn());
			
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

//		TupleIn tupleIn = dataIn.provideDataIn(TupleIn.class);
//		
//		logger.debug("Creating reader for [" + tupleIn + "]");
//		
//		if (!initialised) {
//			
//			columnIndex = tupleIn.indexForHeading(label);
//			
//			logger.info("[" + this + "] is column " + columnIndex);
//			
//			initialised = true;
//		}
//
//		if (columnIndex < 1) {
//			return new NullReader();
//		}
//		else {
//			return new MainReader(tupleIn);
//		}
		
		if (columnIn == null) {
			
			TupleIn in = dataIn.provideDataIn(TupleIn.class);
			
			columnIn = (CellIn<T>) in.inFor(this);
						
			logger.trace("Create Reader for [" + DataCell.this + "], column is [" + 
					columnIn.getColumnIndex() + "]");
		}
		
		return new MainReader2();
	}
	
	/**
	 * Sub classes must override this to write a value into the cell.
	 * 
	 * @param cell
	 * @throws DataException
	 */
	@Override
	abstract public void insertValueInto(Cell cell, T value)
	throws DataException;
		
	/**
	 * @oddjob.property defaultStyle
	 * @oddjob.description The default style for the cell. This is a 
	 * visible property of the layout so that users can see which style
	 * name to use if they wish to override the style at the {@link DataBook}
	 * level.
	 * @oddjob.required Read only.
	 * 
	 * @return
	 */
	public String getDefaultStyle() {
		return null;
	}
	
	/**
	 * The Data Writer that writes to the cell.
	 */
	class MainWriter implements DataWriter {

		private final TupleOut data;
		
		private final DataWriter nextWriter;
		
		public MainWriter(TupleOut outgoing) throws DataException {
			this.data = outgoing; 
			
			this.nextWriter = nextWriterFor(new VoidOut());
		}
		
		@Override
		public boolean write(Object object) throws DataException {

			if (nextWriter.write(object)) {
				return true;
			}

			if (isWrittenTo()) {
				
				resetWrittenTo();
				
				return write(object);
			}
			
			Cell cell = data.createCell(columnIndex, getCellType());
			
			insertValueInto(cell, value());
			
			String style = DataCell.this.style;
			if (style == null) {
				style = getDefaultStyle();
			}
			if (style != null) {
				CellStyle cellStyle = data.styleFor(style);
				
				if (cellStyle == null) {
					throw new DataException("No style available of name [" + 
							style + "] from cell [" + DataCell.this + "]");
				}
				
				cell.setCellStyle(cellStyle);
			}
			
			logger.trace("[" + DataCell.this + "] wrote [" + value() + "]");
			
			setReferenceFrom(cell);
			
			return false;
		}
		
		@Override
		public void close() throws DataException {
			
			logger.trace("Closing [" + nextWriter + "]");
			
			nextWriter.close();
		}
	}
	
	class MainWriter2 implements DataWriter {

		private final DataWriter nextWriter;
		
		public MainWriter2() throws DataException {
			this.nextWriter = nextWriterFor(new VoidOut());
		}
		
		@Override
		public boolean write(Object object) throws DataException {

			if (nextWriter.write(object)) {
				return true;
			}
			
			if (isWrittenTo()) {
				
				resetWrittenTo();
				
				return write(object);
			}
			
			T value = value();
			
			columnOut.setData(value);
			
			reference = columnOut.getCellReference();
			
			logger.trace("[" + DataCell.this + "] wrote value [" + 
					value + "]");
		
			return false;
		}
		
		@Override
		public void close() throws DataException {
			
			logger.trace("Closing [" + nextWriter + "]");
			
			nextWriter.close();
		}
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {

//		TupleOut tupleOut = dataOut.provideDataOut(TupleOut.class);
//		
//		logger.debug("Creating new column writer for [" + tupleOut + "]");
//		
//		if (!initialised) {
//			
//			String heading = label;
//			
//			if (heading == null) {
//				heading = getName();
//			}
//			
//			this.columnIndex = tupleOut.indexForHeading(heading);
//			
//			this.initialised = true;
//
//			logger.debug("[" + this + "] initialsed for column [" + columnIndex + "]");
//		}		
//		
//		if (columnIndex < 0) {
//			
//			return new NullWriter();
//		}
//		else {
//			
//			return new MainWriter(tupleOut);
//		}
		
		if (columnOut == null) {
			
			TupleOut tupleOut = dataOut.provideDataOut(TupleOut.class);
			
			columnOut = (CellOut<T>) tupleOut.outFor(this);
			
			logger.trace("Created writer for [" + DataCell.this + "], column is [" + 
					columnOut.getColumnIndex() + "]");
			
		}
		
		return new MainWriter2();
	}
	
	@Override
	public void reset() {
		super.reset();
		
		reference = null;
		initialised = false;
		columnIn = null;
		columnOut = null;
	}
	
	private void setReferenceFrom(Cell cell) {
		
		reference = new CellReference(
				cell.getRowIndex(), cell.getColumnIndex()
					).formatAsString();
	}
		
	public String getReference() {
		return reference;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String title) {
		this.label = title;
	}

	public int getColumnIndex() {
		if (columnIn != null) {
			return columnIn.getColumnIndex();
		}
		if (columnOut != null) {
			return columnOut.getColumnIndex();
		}
		return columnIndex;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}	
	
	public String toString() {
		if (label == null) {
			return super.toString();
		}
		else {
			return super.toString() + ", title=" + label;
		}
	}
}

package org.oddjob.dido.poi.layouts;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
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
			
			nextReader = nextReaderFor(childDataIn());
			
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
	
	protected DataIn childDataIn() {
		return new VoidIn();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		if (columnIn == null) {
			
			TupleIn in = dataIn.provideDataIn(TupleIn.class);
			
			columnIn = (CellIn<T>) in.inFor(this);
						
			logger.trace("Create Reader for [" + DataCell.this + "], column is [" + 
					columnIn.getColumnIndex() + "]");
		}
		
		return new MainReader();
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
	 * 
	 * @author rob
	 *
	 * @param <T>
	 */
	protected interface DataOutControl<T> {
		
		public DataOut dataOut();
		
		public boolean isWrittenTo();
		
		public void resetWrittenTo();
		
		public T value();
	}
	
	/**
	 * The Data Writer that writes to the cell.
	 */
	class MainWriter implements DataWriter {

		private final DataWriter nextWriter;
		
		private final DataOutControl<T> dataOutControl;
		
		public MainWriter() throws DataException {
			this.dataOutControl = childDataOut();
			this.nextWriter = nextWriterFor(dataOutControl.dataOut());
		}
		
		@Override
		public boolean write(Object object) throws DataException {

			if (nextWriter.write(object)) {
				return true;
			}
			
			if (isWrittenTo() || dataOutControl.isWrittenTo()) {
				
				dataOutControl.resetWrittenTo();
				resetWrittenTo();
				
				return write(object);
			}
			
			T value = dataOutControl.value();
			if (value == null) {
				value = value();
			}
			else {
				value(value);
			}
			
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
	
	protected DataOutControl<T> childDataOut() {
		
		return new DataOutControl<T>() {
			
			@Override
			public T value() {
				return null;
			}
			
			@Override
			public boolean isWrittenTo() {
				return false;
			}
			
			@Override
			public void resetWrittenTo() {
			}
			
			@Override
			public DataOut dataOut() {
				return new VoidOut();
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {
		
		if (columnOut == null) {
			
			TupleOut tupleOut = dataOut.provideDataOut(TupleOut.class);
			
			columnOut = (CellOut<T>) tupleOut.outFor(this);
			
			logger.trace("Created writer for [" + DataCell.this + "], column is [" + 
					columnOut.getColumnIndex() + "]");
			
		}
		
		return new MainWriter();
	}
	
	@Override
	public void reset() {
		super.reset();
		
		reference = null;
		columnIn = null;
		columnOut = null;
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

	public int getIndex() {
		if (columnIn != null) {
			return columnIn.getColumnIndex();
		}
		if (columnOut != null) {
			return columnOut.getColumnIndex();
		}
		return columnIndex;
	}

	public void setColumnIndex(int column) {
		this.columnIndex = column;
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

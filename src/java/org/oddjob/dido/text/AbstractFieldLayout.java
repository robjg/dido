package org.oddjob.dido.text;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.layout.LayoutValueNode;
import org.oddjob.dido.tabular.ColumnIn;
import org.oddjob.dido.tabular.ColumnOut;

/**
 * 
 * @author rob
 *
 */
abstract public class AbstractFieldLayout<T> extends LayoutValueNode<T> 
implements FixedWidthColumn {

	private static final Logger logger = Logger.getLogger(AbstractFieldLayout.class);
	
	private String label;
	
	private int index;

	private int length;
	
	private ColumnIn<String> columnIn;
	
	private ColumnOut<String> columnOut;
			
	/**
	 * Used by super classes to convert the text to their required type.
	 * 
	 * @param value The text value. Might be 0 length but will not be null.
	 * @return The result of the conversion. May be null.
	 * 
	 * @throws DataException
	 */
	abstract protected T convertIn(String value)
	throws DataException;
	
	/**
	 * Used by super classes to convert their data value back to text.
	 * 
	 * @param value The data value. May be null.
	 * @return The text equivalent. May be null.
	 * 
	 * @throws DataException
	 */
	abstract protected String convertOut(T value)
	throws DataException;
	
	/**
	 * Read and item of data.
	 */
	class MainReader implements DataReader {
		
		private DataReader nextReader;
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {

				return nextReader.read();
			}
			
			String field = columnIn.getData();

			value(convertIn(field));

			logger.trace("[" + AbstractFieldLayout.this + "] value is [" + 
					field + "]");
			
			TextIn textIn = new StringTextIn(field);
			
			nextReader = nextReaderFor(textIn);
			
			return read();
		}
		
		@Override
		public void close() throws DataException {
			if (nextReader != null) {
				nextReader.close();
			}
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " for " + 
					AbstractFieldLayout.this.toString();
		}
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn)
	throws DataException {

		if (columnIn == null) {
			
			FieldsIn in = dataIn.provideDataIn(FieldsIn.class);
			
			columnIn = in.inFor(this);
						
			logger.trace("Created Reader for [" + AbstractFieldLayout.this + "], column is [" + 
					columnIn.getColumnIndex() + "]");
		}
		
		return new MainReader();
	}	
		
	/**
	 * Provide a writer. 
	 * 
	 * The writer will provide a child node or binding with the opportunity to
	 * write multiple times from one object (which is probably complete
	 * overkill)
	 *
	 */
	class MainWriter implements DataWriter {

		private final DataWriter nextWriter;
		
		private final StringTextOut textOut;
		
		public MainWriter() throws DataException {
			this.textOut = new StringTextOut();
			this.nextWriter = nextWriterFor(textOut);
		}
		
		@Override
		public boolean write(Object object) throws DataException {

			if (nextWriter.write(object)) {
				return true;
			}
			
			if (textOut.isWrittenTo() || isWrittenTo()) {
				textOut.resetWrittenTo();
				resetWrittenTo();
				return write(object);
			}
			
			String value = textOut.toText();
			if (value == null) {
				value = convertOut(value());
			}
			else {
				value(convertIn(value));
			}
			
			if (value != null) {
				columnOut.setData(value);
				
				logger.trace("[" + AbstractFieldLayout.this + "] wrote value [" + 
						value + "]");
			}
			else {
				logger.trace("[" + AbstractFieldLayout.this + "] no value.");
			}
			
			return false;
		}
		
		@Override
		public void close() throws DataException {
			
			logger.trace("Closing [" + nextWriter + "]");
			
			nextWriter.close();
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " for " + 
					AbstractFieldLayout.this.toString();
		}
	}
	

	@Override
	public DataWriter writerFor(DataOut dataOut)
	throws DataException {
		
		if (columnOut == null) {
			
			FieldsOut out = dataOut.provideDataOut(FieldsOut.class);
			
			columnOut = out.outFor(this);
			
			logger.trace("Created writer for [" + AbstractFieldLayout.this + "], column is [" + 
					columnOut.getColumnIndex() + "]");
			
		}
		
		return new MainWriter();
	}
	
	
	@Override
	public void reset() {
		super.reset();
		
		columnIn = null;
		columnOut = null;
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
		return index;
	}

	public void setIndex(int column) {
		this.index = column;
	}	
	
	public T getValue() {
		return this.value();
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}	
}

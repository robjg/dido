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
	
	private boolean raw;
	
	private ColumnIn<String> columnIn;
	
	private ColumnOut<String> columnOut;
		
	
	
	abstract protected T convertIn(String value);
	
	abstract protected String convertOut(T value);
	
	class MainReader implements DataReader {
		
		private DataReader nextReader;
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {

				return nextReader.read();
			}
			
			String field = columnIn.getData();
			if (!raw) {
				field = field.trim();
			}

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
			
			if (textOut.isWrittenTo()) {
				value(convertIn(textOut.toText()));
			}
			
			if (isWrittenTo()) {
				
				resetWrittenTo();
				textOut.resetWrittenTo();
				
				return write(object);
			}
			
			String value = convertOut(value());
			
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
	
	public boolean isRaw() {
		return raw;
	}

	public void setRaw(boolean trim) {
		this.raw = trim;
	}
}

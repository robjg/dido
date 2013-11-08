package org.oddjob.dido.tabular;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.field.FieldIn;
import org.oddjob.dido.field.FieldOut;
import org.oddjob.dido.layout.LayoutValueNode;
import org.oddjob.dido.layout.VoidIn;
import org.oddjob.dido.layout.VoidOut;

/**
 * @oddjob.description A general purpose column.
 * 
 * @author rob
 *
 * @param <T>
 */
public class ColumnLayout<T> extends LayoutValueNode<T>
implements Column {
	
	private static final Logger logger = Logger.getLogger(ColumnLayout.class);

	private Class<?> type;
	
	private String label;
	
	private int columnIndex;
	
	private FieldIn<T> columnIn;
	
	private FieldOut<T> columnOut;
	
	@Override
	public Class<?> getType() {
		return type;
	}
	
	public void setType(Class<?> type) {
		this.type = type;
	}
	
	class ColumnReader implements DataReader {
		
		private final DataReader nextReader;
		
		public ColumnReader() throws DataException {

			this.nextReader = nextReaderFor(new VoidIn());
		}
		
		@Override
		public Object read() throws DataException {
		
			T value = columnIn.getData();
			
			value(value);
			
			return nextReader.read();
		}
		
		@Override
		public void close() throws DataException {
			
			nextReader.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		if (columnIn == null) {
			
			TabularDataIn columnarDataIn = dataIn.provideDataIn(
					TabularDataIn.class);

			this.columnIn = (ColumnIn<T>) 
					columnarDataIn.inFor(this);
						
			if (type != null && !type.isAssignableFrom(columnIn.getType())) {
				throw new DataException("Type " + type.getName() + 
						" is not assignable from field type " + 
						columnIn.getType().getName());
			}
			
			type = (Class<T>) columnIn.getType();
			
			logger.debug("[" + this + "] initialised on [" + 
					columnIn + "] of type [" + type.getName() + "]");
			
		}
		
		return new ColumnReader();
	}
	
	class ColumnWriter implements DataWriter {

		private final DataWriter nextWriter;
		
		public ColumnWriter() throws DataException {
			
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

			columnOut.setData(value());
			
			return false;
		}
		
		@Override
		public void close() throws DataException {
			
			nextWriter.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {
		
		if (columnOut == null) {
			
			TabularDataOut columnarDataOut = 
					dataOut.provideDataOut(TabularDataOut.class);
			
			columnOut = (ColumnOut<T>) columnarDataOut.outFor(this);
			
			if (type != null && !type.isAssignableFrom(columnIn.getType())) {
				throw new DataException("Type " + type.getName() + 
						" is not assignable from field type " + 
						columnIn.getType().getName());
			}
			
			type = columnOut.getType();
			
			logger.debug("[" + this + "] initialised on [" + 
					columnOut + "] of type [" + type.getName() + "]");
		}
		
		return new ColumnWriter();
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

	public void setLabel(String columnName) {
		this.label = columnName;
	}

	public int getIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int column) {
		this.columnIndex = column;
	}
}

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
import org.oddjob.dido.layout.NullReader;
import org.oddjob.dido.layout.NullWriter;
import org.oddjob.dido.layout.VoidIn;
import org.oddjob.dido.layout.VoidOut;
import org.oddjob.dido.morph.Morphable;

/**
 * @oddjob.description A general purpose column.
 * <p>
 * This column is frequently used by {@link Morphable} layouts to provide
 * their child layouts as the type does not need to be set until the 
 * column is about to be used. 
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
	
	private FieldIn<T> fieldIn;
	
	private FieldOut<T> fieldOut;
	
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
		
			T value = fieldIn.getData();
			
			value(value);
			
			return nextReader.read();
		}
		
		@Override
		public void close() throws DataException {
			
			nextReader.close();
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " for " + 
					ColumnLayout.this.toString();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		if (fieldIn == null) {
			
			TabularDataIn columnarDataIn = dataIn.provideDataIn(
					TabularDataIn.class);

			this.fieldIn = (FieldIn<T>) 
					columnarDataIn.inFor(this);
				
			Class<?> columnType = fieldIn.getType();
					
			if (type == null) {
				type = columnType;
			}
			else if (columnType != null 
					&& !type.isAssignableFrom(columnType)) {
				
				throw new DataException("Type " + type.getName() + 
						" is not assignable from field type " + 
						columnType.getName());
			}
			
			if (type == null) {
				logger.debug("[" + this + "] initialised on null field.");
				
			}
			else {
				logger.debug("[" + this + "] initialised on [" + 
						fieldIn + "] of type [" + type.getName() + "]");
			}			
		}
		
		if (type == null) {
			return new NullReader();
		}
		else {
			return new ColumnReader();
		}
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

			fieldOut.setData(value());
			
			return false;
		}
		
		@Override
		public void close() throws DataException {
			
			nextWriter.close();
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " for " + 
					ColumnLayout.this.toString();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {
		
		if (fieldOut == null) {
			
			TabularDataOut columnarDataOut = 
					dataOut.provideDataOut(TabularDataOut.class);
			
			fieldOut = (FieldOut<T>) columnarDataOut.outFor(this);
			
			Class<?> columnType = fieldOut.getType();
			
			if (type == null) {
				
				type = columnType;
			}
			else if (columnType != null 
					&& !type.isAssignableFrom(columnType)) {

				throw new DataException("Type " + type.getName() + 
						" is not assignable from field type " + 
						columnType.getName());
			}
			
			if (type == null) {
				logger.debug("[" + this + "] initialised on null field.");
			}
			else {
				logger.debug("[" + this + "] initialised on [" + 
						fieldOut + "] of type [" + type.getName() + "]");
			}
		}
		
		if (type == null) {
			return new NullWriter();
		}
		else {
			return new ColumnWriter();

		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		fieldIn = null;
		fieldOut = null;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String columnName) {
		this.label = columnName;
	}

	public int getIndex() {
		if (fieldIn instanceof ColumnData) {
			return ((ColumnData) fieldIn).getColumnIndex();
		}
		if (fieldOut instanceof ColumnData) {
			return ((ColumnData) fieldOut).getColumnIndex();
		}
		return columnIndex;
	}

	public void setColumnIndex(int column) {
		this.columnIndex = column;
	}
}

package org.oddjob.dido.column;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.layout.LayoutValueNode;
import org.oddjob.dido.layout.NullReader;
import org.oddjob.dido.layout.NullWriter;

public class ColumnLayout<T> extends LayoutValueNode<T>{
	
	private static final Logger logger = Logger.getLogger(ColumnLayout.class);

	private Class<T> type;
	
	private boolean initialised;
	
	private String columnName;
	
	private int column;
	
	private int originalColumn;
	
	@Override
	public Class<T> getType() {
		return type;
	}

	class ColumnReader implements DataReader {
		
		private final ColumnarDataIn columnDataIn;

		private final DataReader nextReader;
		
		public ColumnReader(ColumnarDataIn columnDataIn) throws DataException {

			this.columnDataIn = columnDataIn;
			
			this.nextReader = nextReaderFor(columnDataIn);
		}
		
		@Override
		public Object read() throws DataException {
		
			T value = columnDataIn.getColumnData(column);
			
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
		
		ColumnarDataIn columnarDataIn = dataIn.provideDataIn(ColumnarDataIn.class);

		if (!initialised) {
			
			originalColumn = column;
					
			column = columnarDataIn.columnIndexFor(columnName, column);
			
			logger.debug("[" + this + "] initialised on column " + column);
			
			if (column == 0) {
				type = (Class<T>) Void.class;
			}
			else {
				type = columnarDataIn.getColumnMetaData(column).getColumnType();
			}
			
			initialised = true;
		}
		
		if (column == 0) {
			return new NullReader();
		}
		else {
			return new ColumnReader(columnarDataIn);
		}
	}
	
	class ColumnWriter implements DataWriter {

		private final ColumnarDataOut columnDataOut;
		
		private final DataWriter nextWriter;
		
		public ColumnWriter(ColumnarDataOut columnDataOut) throws DataException {
			
			this.columnDataOut = columnDataOut;
			
			this.nextWriter = nextWriterFor(columnDataOut);
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

			columnDataOut.setColumnData(column, value());
			
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
		
		ColumnarDataOut columnarDataOut = dataOut.provideDataOut(ColumnarDataOut.class);
		
		if (!initialised) {
			
			originalColumn = column;
			
			column = columnarDataOut.columnIndexFor(columnName, column);
			
			logger.debug("[" + this + "] initialised on column " + column);
			
			if (column == 0) {
				type = (Class<T>) Void.class;
			}	
			else {
				type = columnarDataOut.getColumnMetaData(column).getColumnType();
			}
			
			initialised = true;
		}
		
		if (column == 0) {
			return new NullWriter();
		}
		else {
			return new ColumnWriter(columnarDataOut);
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		initialised = false;
		column = originalColumn;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
}

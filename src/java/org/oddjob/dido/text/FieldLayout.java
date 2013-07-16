package org.oddjob.dido.text;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.column.Column;
import org.oddjob.dido.column.ColumnIn;
import org.oddjob.dido.column.ColumnOut;
import org.oddjob.dido.layout.LayoutValueNode;


public class FieldLayout extends LayoutValueNode<String> 
implements Column {

	private static final Logger logger = Logger.getLogger(FieldLayout.class);
	
	private String columnLabel;
	
	private int columnIndex;

	private ColumnIn<String> columnIn;
	
	private ColumnOut<String> columnOut;
	
	@Override
	public Class<String> getType() {
		return String.class;
	}

	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	class MainReader implements DataReader {
		
		private DataReader nextReader;
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {

				return nextReader.read();
			}
			
			String field = columnIn.getColumnData();
			
			value(field);

			logger.trace("[" + FieldLayout.this + "] value is [" + 
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
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn)
	throws DataException {

		if (columnIn == null) {
			
			FieldsIn in = dataIn.provideDataIn(FieldsIn.class);
			
			columnIn = in.columnInFor(this);
						
			logger.trace("Create Reader for [" + FieldLayout.this + "], column is [" + 
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
				value(textOut.toText());
			}
			
			if (isWrittenTo()) {
				
				resetWrittenTo();
				textOut.resetWrittenTo();
				
				return write(object);
			}
			
			String value = value();
			
			if (value != null) {
				columnOut.setColumnData(value);
				
				logger.trace("[" + FieldLayout.this + "] wrote value [" + 
						value + "]");
			}
			else {
				logger.trace("[" + FieldLayout.this + "] no value.");
			}
			
			return false;
		}
		
		@Override
		public void close() throws DataException {
			
			logger.trace("Closing [" + nextWriter + "]");
			
			nextWriter.close();
		}
	}
	

	@Override
	public DataWriter writerFor(DataOut dataOut)
	throws DataException {
		
		if (columnOut == null) {
			
			FieldsOut out = dataOut.provideDataOut(FieldsOut.class);
			
			columnOut = out.columnOutFor(this);
			
			logger.trace("Created writer for [" + FieldLayout.this + "], column is [" + 
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
	
	
	public String getColumnLabel() {
		return columnLabel;
	}

	public void setColumnLabel(String title) {
		this.columnLabel = title;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int column) {
		this.columnIndex = column;
	}	
	
	public String getValue() {
		return this.value();
	}
}

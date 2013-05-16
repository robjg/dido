package org.oddjob.dido.text;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.UnsupportedeDataInException;
import org.oddjob.dido.UnsupportedeDataOutException;
import org.oddjob.dido.layout.LayoutValueNode;


public class FieldLayout 
extends LayoutValueNode<String> {

	private String title;
	
	private int column;
	
	private boolean initialised;
	
	@Override
	public Class<String> getType() {
		return String.class;
	}

	private class MainReader implements DataReader {
		
		private final FieldsIn in;
		
		private DataReader nextReader;
		
		public MainReader(FieldsIn in) {
			this.in = in;
		}
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {
				Object value = nextReader.read();
				
				return value;
			}
			
			String field = null;
			if (column > 0) {
				field = in.getColumn(column);
			}

			value(field);
			
			nextReader = nextReaderFor(in);
			
			return read();
		}
	}
	

	private DataReader reader;
	
	
	
	@Override
	public DataReader readerFor(DataIn dataIn)
	throws UnsupportedeDataInException {
	
		FieldsIn in = dataIn.provideIn(FieldsIn.class);
		
		if (!initialised) {
			String title = getTitle();
			if (title == null) {
				column = in.columnFor(getName(), false, column);
			}		
			else {
				column = in.columnFor(title, true, column);
			}
			
			initialised = true;
		}
		
		if (reader == null) {
			reader = new MainReader(in);
		}
		
		return new DataReader() {
			public Object read() throws DataException {
				Object value = reader.read();
				if (value == null) {
					reader = null;
				}
				return value;
			}
		};
	}	
		
	private DataWriter writer;
	
	class MainWriter implements DataWriter {

		private final FieldsOut outgoing;
		
		private DataWriter nextWriter;
		
		private TextOut textOut;
		
		public MainWriter(FieldsOut outgoing) {
			this.outgoing = outgoing; 
		}
		
		@Override
		public boolean write(Object value) throws DataException {

			if (nextWriter == null) {
				
				value(null);
				textOut = new StringTextOut();
				nextWriter = downOrOutWriter(textOut);
			}
			
			if (nextWriter.write(value)) {
				return true;
			}
			
			
			if (value() != null ) {
				outgoing.setColumn(column, value());
			}	
			
			writer = null;

			return false;
		}
	}
	

	@Override
	public DataWriter writerFor(DataOut dataOut)
			throws UnsupportedeDataOutException {
		
		FieldsOut out = dataOut.provideOut(FieldsOut.class);
		
		if (writer == null) {
			
			String heading = title;
			
			if (heading == null) {
				heading = getName();
			}
			
			column = out.writeHeading(heading, column);
			
			if (column == 0) {
				
				writer = new DataWriter() {
					@Override
					public boolean write(Object value) throws DataException {
						return false;
					}
				};
			}
			else {
				
				writer = new MainWriter(out);
			}
		}		
		
		return writer;
	}
	
	
	@Override
	public void close() {
		reader = null;
		writer = null;
	}
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}	
	
	public String getValue() {
		return this.value();
	}
}

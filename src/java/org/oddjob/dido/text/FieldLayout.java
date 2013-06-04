package org.oddjob.dido.text;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.layout.LayoutValueNode;
import org.oddjob.dido.layout.NullWriter;


public class FieldLayout 
extends LayoutValueNode<String> {

	private static final Logger logger = Logger.getLogger(FieldLayout.class);
	private String title;
	
	private int column;
	
	private boolean initialised;
	
	@Override
	public Class<String> getType() {
		return String.class;
	}

	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	private class MainReader implements DataReader {
		
		private final FieldsIn in;
		
		private DataReader nextReader;
		
		private TextIn textIn;
		
		public MainReader(FieldsIn in) {
			this.in = in;
		}
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {

				Object value = nextReader.read();
				
				nextReader.close();
				return value;
			}
			
			String field = null;
			
			if (column > 0) {
				field = in.getColumn(column);
			}

			value(field);

			logger.trace("[" + FieldLayout.this + "] value is [" + 
					field + "]");

			
			textIn = new StringTextIn(field);
			
			nextReader = nextReaderFor(textIn);
			
			return read();
		}
		
		@Override
		public void close() throws DataException {
		}
	}
	

	private DataReader reader;
	
	
	
	@Override
	public DataReader readerFor(DataIn dataIn)
	throws DataException {
	
		FieldsIn in = dataIn.provide(FieldsIn.class);
		
		if (!initialised) {
			String title = getTitle();
			if (title == null) {
				column = in.columnFor(getName(), false, column);
			}		
			else {
				column = in.columnFor(title, true, column);
			}
			
			logger.trace("Reader for [" + FieldLayout.this + "] column is [" + 
					column + "]");
			
			initialised = true;
		}
		
		if (reader == null) {
			reader = new MainReader(in);
		}
		
		return new DataReader() {			
			
			@Override
			public Object read() throws DataException {
				Object value = reader.read();
				if (value == null) {
					reader = null;
				}
				return value;
			}

			@Override
			public void close() throws DataException {
			}
		};
	}	
		
	class MainWriter implements DataWriter {

		private final FieldsOut outgoing;
		
		private DataWriter nextWriter;
		
		private TextOut textOut;
		
		public MainWriter(FieldsOut outgoing) {
			this.outgoing = outgoing; 
		}
		
		@Override
		public boolean write(Object object) throws DataException {

			if (nextWriter == null) {
				
				value(null);
				
				textOut = new StringTextOut();
				nextWriter = nextWriterFor(textOut);
			}
			
			boolean keep = nextWriter.write(object);
			
			String value = value();
			
			if (value != null ) {
			
				outgoing.setColumn(column, value);
				
				logger.trace("[" + FieldLayout.this + "] wrote value [" + 
						value + "] to [" + outgoing + "]");
			}
			else {
				logger.trace("[" + FieldLayout.this + "] no value.");
				
			}
			
			if (!keep) {
				nextWriter.close();
				nextWriter = null;
			}
			
			return keep;
		}
		
		@Override
		public void close() throws DataException {
			
			if (nextWriter != null) {
				
				logger.trace("Closing [" + nextWriter + "]");
				
				nextWriter.close();
				nextWriter = null;
			}
		}
	}
	

	@Override
	public DataWriter writerFor(DataOut dataOut)
	throws DataException {
		
		FieldsOut out = dataOut.provide(FieldsOut.class);
		
		if (!initialised) {
			
			String heading = title;
			
			if (heading == null) {
				heading = getName();
			}
			
			column = out.columnForHeading(heading, column);
			
			logger.trace("Initialised [" + FieldLayout.this + "] with column [" + 
					column + "]");
			
			initialised = true;
		}
		
		logger.trace("Creating Writer for column [" + column + 
				"] of [" + out + "]");
		
		if (column == 0) {
			
				return new NullWriter();
		}
		else {
				
				return new MainWriter(out);
		}		
		
	}
	
	
	@Override
	public void reset() {
		initialised = false;
		
		reader = null;
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

package org.oddjob.dido.text;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.layout.LayoutValueNode;
import org.oddjob.dido.morph.MorphDefinition;
import org.oddjob.dido.morph.MorphProvider;
import org.oddjob.dido.morph.Morphable;
import org.oddjob.dido.stream.LinesIn;
import org.oddjob.dido.stream.LinesOut;


public class DelimitedLayout extends LayoutValueNode<String[]>
implements Morphable, MorphProvider {

	private static final Logger logger = Logger.getLogger(DelimitedLayout.class);
	
	public static final String DEFAULT = ",";
	
	private String delimiter;
	
	private String regexp;
	
	private String[] headings;
	
	private boolean withHeadings;
	
	private boolean initialised;
	
	@Override
	public Class<String[]> getType() {
		return String[].class;
	}

	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	class HeaderFirstReader implements DataReader {
		
		private final LinesIn linesIn;
		
		private DataReader reader = new DataReader() {
			
			@Override
			public Object read() throws DataException {
				if (withHeadings) {
					String line = linesIn.readLine();
					
					if (line == null) {
						return null;
					}
					
					headings = parseDelimited(line);
				}

				reader = new BodyReader(linesIn);
				return reader.read();
			}
			
			@Override
			public void close() throws DataException {
			}
		};
		
		public HeaderFirstReader(LinesIn linesIn) {
			this.linesIn = linesIn;
		}
		
		@Override
		public Object read() throws DataException {
			return reader.read();
		}
		
		@Override
		public void close() throws DataException {
			reader.close();
		}
	}
	
	
	class BodyReader implements DataReader {
		
		private final LinesIn linesIn;
		
		private DataReader nextReader;
		
		public BodyReader(LinesIn linesIn) {
			this.linesIn = linesIn;
		}
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {
				Object value = nextReader.read();
				
				if (value != null) {
					return value;
				}
				else {
					nextReader.close();
				}
			}
			
			String line = linesIn.readLine();
			
			logger.trace("[" + DelimitedLayout.this + "] read line [" + 
					line + "]");
			
			if (line == null) {
				return null;
			}
						
			String use = regexp == null ? DEFAULT : regexp;
			
			String fields[] = line.split(use, -1);
			
			value(fields);

			MappedFieldsIn fieldsIn = new MappedFieldsIn();
			if (headings != null) {
				fieldsIn.setHeadings(headings);
			}
			fieldsIn.setValues(fields);
			
			nextReader = nextReaderFor(fieldsIn);
			
			return read();
		}
		
		@Override
		public void close() throws DataException {
		}
	}
	

	@Override
	public DataReader readerFor(DataIn dataIn)
	throws DataException {
		
		LinesIn linesIn = dataIn.provideDataIn(LinesIn.class);
		
		if (initialised) {
			return new BodyReader(linesIn);
		}
		else {
			initialised = true;
			return new HeaderFirstReader(linesIn);
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		initialised = false;
	}
	
	
	private interface LineWriter {
		
		void write(LinesOut linesOut)
		throws DataException;
	}
	
	private LineWriter lineWriter;
			
	private class MaybeWriteHeadings implements LineWriter {
	
		private final SimpleFieldsOut fields;
		
		public MaybeWriteHeadings(SimpleFieldsOut fields) {
			this.fields = fields;
		}
		
		@Override
		public void write(LinesOut linesOut) throws DataException {
			
			if (withHeadings) {
				
				String[] headings = fields.headings();

				linesOut.writeLine(delimitedString(headings));
				
				logger.trace("[" + DelimitedLayout.this + "] wrote line [" + 
						headings + "]");

			}
			
			lineWriter = new BodyWriter();
			
			lineWriter.write(linesOut);
		}
	}
	
	private class BodyWriter implements LineWriter {
		
		@Override
		public void write(LinesOut linesOut) throws DataException {

			String line = delimitedString(value());
			
			linesOut.writeLine(line);
			
			logger.trace("[" + DelimitedLayout.this + "] wrote line [" + 
					line + "]");
		}
	}		
	
	class DelimitedWriter implements DataWriter {
		
		private final LinesOut linesOut;
		
		private final SimpleFieldsOut fieldsOut = 
				new SimpleFieldsOut(headings);
		
		private DataWriter nextWriter;
		
		public DelimitedWriter(LinesOut linesOut) {
			this.linesOut = linesOut;
		}
		
		@Override
		public boolean write(Object object) throws DataException {
			
			if (nextWriter == null) {

				value(null);
				fieldsOut.clear();

				nextWriter = nextWriterFor(fieldsOut);
			}
			
			if (nextWriter.write(object)) {
				return true;
			}
					
			if (fieldsOut.isWrittenTo()) {
				value(fieldsOut.values());
			}
			
			if (isWrittenTo()) {
					
				resetWrittenTo();
				fieldsOut.resetWrittenTo();
				
				return write(object);
			}
			else {
				
				String[] value = value();
				
				if (value != null) {
					
					if (lineWriter == null) {
						lineWriter = new MaybeWriteHeadings(fieldsOut);
					}
				
					lineWriter.write(linesOut);
				}
				
				nextWriter.close();
				nextWriter = null;
				
				return linesOut.isMultiLine();
			}
		}
		
		@Override
		public void close() throws DataException {
			
			if (nextWriter != null) {
				
				logger.trace("Closing [" + nextWriter + "]");
				
				nextWriter.close();
				nextWriter = null;
			}
		}
		
		@Override
		public String toString() {
			
			return "Writer for [" + DelimitedLayout.this + "]";
		}
	}
	
	
	@Override
	public DataWriter writerFor(DataOut dataOut)
	throws DataException {

		LinesOut linesOut = dataOut.provideDataOut(LinesOut.class);

		logger.trace("Creating writer for [" + linesOut + "]");
		
		return new DelimitedWriter(linesOut);
	}
	
	@Override
	public Runnable morphInto(MorphDefinition morphicness) {
		
		if (childLayouts().size() > 0) {
			logger.debug("[" + this + "] has children. Morphicness ignored.");
			
			return new Runnable() {
				@Override
				public void run() {
				}
			};
		}
		
		for (String name : morphicness.getNames()) {
			
			FieldLayout layout = new FieldLayout();
			layout.setName(name);
			layout.setTitle(morphicness.titleFor(name));
			
			logger.debug("[" + this + "] adding morphicness [" + layout + "]");
			
			addOrRemoveChild(childLayouts().size(), layout);
		}
		
		return new Runnable() {
			
			@Override
			public void run() {
				childLayouts().clear();
			}
		};
	}

	public String[] parseDelimited(String line) {
		
		String use = regexp == null ? DEFAULT : regexp;
		
		String fields[] = line.split(use, -1);
		
		return fields;
	}
	
	public String delimitedString(String[] values) {
		String use = delimiter == null ? DEFAULT : delimiter;
		
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < values.length; ++i) {
			if (i > 0) {
				buffer.append(use);
			}
			if (values[i] != null) {
				buffer.append(values[i]);
			}
		}
		
		return buffer.toString();
	}
	
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
		if (regexp == null) {
			regexp = Pattern.quote(delimiter);
		}
	}
	
	public String getDelimiter() {
		return delimiter;
	}
	
	@Override
	public MorphDefinition morphOf() {
		
		if (headings == null) {
			return null;
		}
		
		return new MorphDefinition() {
			
			@Override
			public Class<?> typeOf(String name) {
				return String.class;
			}
			
			@Override
			public String titleFor(String name) {
				return name;
			}
			
			@Override
			public String[] getNames() {
				
				return headings;
			}
		};
	}
	
	public void setHeadings(String[] headings) {
		this.headings = headings;
	}

	public String[] getHeadings() {
		return headings;
	}
	
	public void setValue(String[] value) {
		this.value(value);
	}
	
	public String[] getValue() {
		return this.value();
	}

	public boolean isWithHeadings() {
		return withHeadings;
	}

	public void setWithHeadings(boolean withHeadings) {
		this.withHeadings = withHeadings;
	}

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}
}

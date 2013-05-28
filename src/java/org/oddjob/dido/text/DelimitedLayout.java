package org.oddjob.dido.text;

import java.util.regex.Pattern;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Headed;
import org.oddjob.dido.Layout;
import org.oddjob.dido.Morphicness;
import org.oddjob.dido.UnsupportedeDataOutException;
import org.oddjob.dido.io.ClassMorphic;
import org.oddjob.dido.layout.LayoutValueNode;
import org.oddjob.dido.stream.LinesIn;
import org.oddjob.dido.stream.LinesOut;


public class DelimitedLayout extends LayoutValueNode<String[]>
implements Headed, ClassMorphic {

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
		};
		
		public HeaderFirstReader(LinesIn linesIn) {
			this.linesIn = linesIn;
		}
		
		@Override
		public Object read() throws DataException {
			return reader.read();
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
			}
			
			String line = linesIn.readLine();
			
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
	}
	

	@Override
	public DataReader readerFor(DataIn dataIn)
	throws DataException {
		
		LinesIn linesIn = dataIn.provide(LinesIn.class);
		
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
			}
			
			lineWriter = new BodyWriter();
			
			lineWriter.write(linesOut);
		}
	}
	
	private class BodyWriter implements LineWriter {
		
		@Override
		public void write(LinesOut linesOut) throws DataException {
			
			linesOut.writeLine(delimitedString(value()));
		}
	}		
	
	class DelimitedWriter implements DataWriter {
		
		private final LinesOut linesOut;
		
		private final SimpleFieldsOut fieldsOut = 
				new SimpleFieldsOut(getHeadings());
		
		private DataWriter nextWriter;
		
		public DelimitedWriter(LinesOut linesOut) {
			this.linesOut = linesOut;
		}
		
		@Override
		public boolean write(Object value) throws DataException {
			
			if (nextWriter == null) {

				value(null);
				
				nextWriter = nextWriterFor(fieldsOut);
			}
			
			if (nextWriter.write(value)) {
				return true;
			}
			else {
				
				nextWriter = null;
				
				if (value() == null) {
					value(fieldsOut.values());
				}
				
				if (value() != null) {					
						
					if (lineWriter == null) {
						lineWriter = new MaybeWriteHeadings(fieldsOut);
					}
				
					lineWriter.write(linesOut);
				}
				
				fieldsOut.clear();

				return false;
			}
		}
	}
	
	
	@Override
	public DataWriter writerFor(DataOut dataOut)
	throws UnsupportedeDataOutException {

		LinesOut linesOut = dataOut.provide(LinesOut.class);

		return new DelimitedWriter(linesOut);
	}
	
	@Override
	public Runnable beFor(Morphicness morphicness) {
		if (childLayouts().size() > 0) {
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
			addOrRemoveChild(childLayouts().size(), layout);
		}
		
		return new Runnable() {
			
			@Override
			public void run() {
				childLayouts().clear();
			}
		};
	}

	private String[] parseDelimited(String line) {
		
		String use = regexp == null ? DEFAULT : regexp;
		
		String fields[] = line.split(use, -1);
		
		return fields;
	}
	
	private String delimitedString(String[] values) {
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
	public String[] getHeadings() {
		return headings;
	}

	public void setHeadings(String[] headings) {
		this.headings = headings;
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

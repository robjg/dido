package org.oddjob.dido.text;

import java.text.ParseException;

import org.apache.log4j.Logger;
import org.oddjob.arooa.utils.ArooaDelimiter;
import org.oddjob.arooa.utils.ArooaTokenizer;
import org.oddjob.arooa.utils.FlexibleDelimiterFactory;
import org.oddjob.arooa.utils.FlexibleTokenizerFactory;
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
import org.oddjob.dido.other.When;
import org.oddjob.dido.stream.LinesIn;
import org.oddjob.dido.stream.LinesOut;

/**
 * @oddjob.description Define a delimited layout.
 * <p>
 * <ul>
 * <li>This is generally a top level layout.</li> 
 * <li>It can be nested in a {@link When} layout.</li>
 * <li>It can be a child layout of a {link {@link TextLayout} layout to
 * further delimit a region of text.</li>
 * </ul>
 * <p>
 * This layout is Morphable which means that a binding can ask it to 
 * generate it's children If no child nodes (fields) are already defined.
 * 
 * @oddjob.example
 * 
 * Read data and then write it back out again. Note that as no type is 
 * specified in the binding, the headings are required to create the beans.
 * 
 * {@oddjob.xml.resource org/oddjob/dido/text/DelimitedSimplestReadWrite.xml}
 * 
 * @oddjob.example
 * 
 * Read and write data using actual java beans as defined by the type in 
 * the binding. The heading property is still required to create the beans
 * because the property order in a java bean isn't guaranteed.
 * 
 * {@oddjob.xml.resource org/oddjob/dido/text/DelimitedReadWriteByType.xml}
 * 
 * 
 * @author rob
 *
 */
public class DelimitedLayout extends LayoutValueNode<String[]>
implements Morphable, MorphProvider {

	private static final Logger logger = Logger.getLogger(DelimitedLayout.class);
	
	public static final String DEFAULT_DELIMITER = ",";
	
	private String delimiter;
	
	private String regexp;
	
	private Character quote;
	
	private boolean alwaysQuote;
	
	private Character escape;
	
	private String[] headings;
	
	private boolean withHeadings;
	
	private SimpleTextFieldsIn fieldsIn;
	
	private ArooaTokenizer arooaTokenizer;
	
	private SimpleTextFieldsOut fieldsOut;
	
	private ArooaDelimiter arooaDelimiter;
	
	@Override
	public Class<String[]> getType() {
		return String[].class;
	}

	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	class SwapReader implements DataReader {
		
		private DataReader reader;

		public SwapReader(LinesIn linesIn) {
			reader = new HeaderReader(linesIn, this);
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
	
	class HeaderReader implements DataReader {
		
		private final LinesIn linesIn;

		private final SwapReader swapReader;
		
		public HeaderReader(LinesIn linesIn, SwapReader swapReader) {
			this.linesIn = linesIn;
			this.swapReader = swapReader;
		}
		
		@Override
		public Object read() throws DataException {

			String line = linesIn.readLine();
			
			if (line == null) {
				return null;
			}
			
			headings = parseDelimited(line);

			swapReader.reader = new BodyReader(linesIn);
			return swapReader.reader.read();
		}
			
		@Override
		public void close() throws DataException {
			linesIn.close();
		}
	}
	
	class BodyReader implements DataReader {
		
		private final LinesIn linesIn;
		
		private DataReader nextReader;
		
		public BodyReader(LinesIn linesIn) {

			this.linesIn = linesIn;
			
			if (fieldsIn == null) {
				fieldsIn = new SimpleTextFieldsIn();
				
				if (headings != null) {
					fieldsIn.setHeadings(headings);
				}
			}
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
						
			String fields[] = parseDelimited(line);
			
			value(fields);

			fieldsIn.setValues(fields);
			
			nextReader = nextReaderFor(fieldsIn);
			
			return read();
		}
		
		@Override
		public void close() throws DataException {
			linesIn.close();
		}
	}
	

	@Override
	public DataReader readerFor(DataIn dataIn)
	throws DataException {
		
		LinesIn linesIn = dataIn.provideDataIn(LinesIn.class);
		
		FlexibleTokenizerFactory tokenizerFactory = new FlexibleTokenizerFactory();
		if (regexp == null) {
			tokenizerFactory.setDelimiter(delimiter == null ? DEFAULT_DELIMITER : delimiter);
			tokenizerFactory.setRegexp(false);
		}
		else {
			tokenizerFactory.setDelimiter(regexp);
			tokenizerFactory.setRegexp(true);
		}
		tokenizerFactory.setEscape(escape);
		tokenizerFactory.setQuote(quote);
		
		arooaTokenizer = tokenizerFactory.newTokenizer();
		
		if (withHeadings) {
			return new SwapReader(linesIn);
		}
		else {
			return new BodyReader(linesIn);
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		fieldsIn = null;
		fieldsOut = null;
	}
		
	class DelimitedWriter implements DataWriter {
		
		private final LinesOut linesOut;
		
		private DataWriter nextWriter;
		
		private boolean initialised;
		
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
				
			String[] value = value();
				
			if (value != null) {

				if (!initialised) {

					if (withHeadings) {

						String[] headings = fieldsOut.headings();

						linesOut.writeLine(delimitedString(headings));

						logger.trace("[" + DelimitedLayout.this + 
								"] wrote line [" + headings + "]");
					}

					initialised = true;
				}

				String line = delimitedString(value);

				linesOut.writeLine(line);

				logger.trace("[" + DelimitedLayout.this + "] wrote line [" + 
						line + "]");
			}

			nextWriter.close();
			nextWriter = null;

			return linesOut.isMultiLine();
		}
		
		@Override
		public void close() throws DataException {
			
			if (nextWriter != null) {
				
				logger.trace("Closing [" + nextWriter + "]");
				
				nextWriter.close();
				nextWriter = null;
			}
			
			linesOut.close();
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

		FlexibleDelimiterFactory delimiterFactory = new FlexibleDelimiterFactory();
		delimiterFactory.setDelimiter(delimiter == null ? DEFAULT_DELIMITER : delimiter);
		delimiterFactory.setQuote(quote);
		delimiterFactory.setEscape(escape);
		delimiterFactory.setAlwaysQuote(alwaysQuote);

		arooaDelimiter = delimiterFactory.newDelimiter();
		
		logger.trace("Creating writer for [" + linesOut + "]");

		if (fieldsOut == null) {
			fieldsOut = new SimpleTextFieldsOut(headings);
		}
		
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
			
			TextLayout layout = new TextLayout();
			layout.setName(name);
			layout.setLabel(morphicness.labelFor(name));
			
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

	public String[] parseDelimited(String line) throws DataException {
		
		try {
			return arooaTokenizer.parse(line);
		} catch (ParseException e) {
			throw new DataException(e);
		}
	}
	
	public String delimitedString(String[] values) {
		
		return arooaDelimiter.delimit(values);
	}
	
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
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
			public String labelFor(String name) {
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

	public Character getQuote() {
		return quote;
	}

	public void setQuote(Character quote) {
		this.quote = quote;
	}

	public boolean isAlwaysQuote() {
		return alwaysQuote;
	}

	public void setAlwaysQuote(boolean alwaysQuote) {
		this.alwaysQuote = alwaysQuote;
	}

	public Character getEscape() {
		return escape;
	}

	public void setEscape(Character escape) {
		this.escape = escape;
	}
	
	
}

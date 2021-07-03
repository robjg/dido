package org.oddjob.dido.text;

import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

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
import org.oddjob.dido.field.SimpleFieldDataIn;
import org.oddjob.dido.field.SimpleFieldDataOut;
import org.oddjob.dido.layout.LayoutValueNode;

/**
 * @oddjob.description Support name value pairs.
 * 
 * @author rob
 *
 */
public class NamedValuesLayout extends LayoutValueNode<Map<String, String>> {

	private static final Logger logger = Logger.getLogger(NamedValuesLayout.class);
	
	public static final String DEFAULT_REGEXP = "\\s*=\\s*";
	
	public static final String DEFAULT_DELIMITER = "=";
	
	private String delimiter;
	
	private String regexp;
	
	private Character quote;
	
	private boolean alwaysQuote;
	
	private Character escape;
	
	private ArooaTokenizer arooaTokenizer;
	
	private ArooaDelimiter arooaDelimiter;
	
	private SimpleFieldDataIn fieldDataIn;

	private SimpleFieldDataOut fieldDataOut;
	
	@Override
	public Class<?> getType() {
		return Map.class;
	}

	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	private class InternalReader implements DataReader {
		
		private final StringsIn stringsIn;
		
		private DataReader nextReader;
		
		public InternalReader(StringsIn stringsIn) {
			this.stringsIn = stringsIn;
		}
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {
				
				return nextReader.read();
			}
			
			String[] fields = stringsIn.getValues();

			Map<String, String> map = new LinkedHashMap<String, String>();
			
			for (String field: fields) {
				String[] nameAndValue;
				try {
					nameAndValue = arooaTokenizer.parse(field);
				} catch (ParseException e) {
					throw new DataException(e);
				}
				if (nameAndValue.length != 2) {
					throw new DataException("Failed to parse [" + 
							field + "] into a name and value.");
				}
				map.put(nameAndValue[0], nameAndValue[1]);
			}
			
			value(map);
			fieldDataIn.setValues(map);
			
			if (logger.isTraceEnabled()) {
				
				logger.trace("[" + NamedValuesLayout.this + "] value is [" + 
						map + "]");
			}
						
			nextReader = nextReaderFor(fieldDataIn);
			
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
			return getClass().getSimpleName() + " for " + NamedValuesLayout.this;
		}
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		if (arooaTokenizer == null) {
			
			FlexibleTokenizerFactory tokenizerFactory = new FlexibleTokenizerFactory();
			
			String regexp = this.regexp;
			if (regexp == null && delimiter == null) {
				regexp = DEFAULT_REGEXP;
			}
			if (regexp == null) {
				tokenizerFactory.setDelimiter(delimiter);
				tokenizerFactory.setRegexp(false);
			}
			else {
				tokenizerFactory.setDelimiter(regexp);
				tokenizerFactory.setRegexp(true);
			}
			tokenizerFactory.setEscape(escape);
			tokenizerFactory.setQuote(quote);
	
			arooaTokenizer = tokenizerFactory.newTokenizer();
		}

		if (fieldDataIn == null) {
			fieldDataIn = new SimpleFieldDataIn();
		}
		
		return new InternalReader(dataIn.provideDataIn(StringsIn.class));
	}

	private class InternalWriter implements DataWriter {
		
		private final StringsOut stringsOut;
		
		private DataWriter nextWriter;
		
		public InternalWriter(StringsOut stringsOut) {
			this.stringsOut = stringsOut;
		}
		
		@Override
		public boolean write(Object object) throws DataException {
			
			if (nextWriter == null) {

				value(null);

				nextWriter = nextWriterFor(fieldDataOut);
			}
			
			if (nextWriter.write(object)) {
				return true;
			}
					
			if (fieldDataOut.isWrittenTo()) {
				value(fieldDataOut.getValues());
			}
			
			if (isWrittenTo()) {
					
				resetWrittenTo();
				fieldDataOut.clear();
				
				return write(object);
			}
				
			Map<String, String> map = value();
				
			if (map != null) {
				String[] strings = new String[map.size()]; 
				int i = 0;
				for (Map.Entry<String, String> entry : map.entrySet()) {
					String field = arooaDelimiter.delimit(new String[] {
							entry.getKey(), entry.getValue() });
					strings[i++] = field;
				}
				
				stringsOut.setValues(strings);

				if (logger.isTraceEnabled()) {
					logger.trace("[" + NamedValuesLayout.this + "] wrote line [" + 
							Arrays.asList(strings) + "]");
				}
			}

			nextWriter.close();
			nextWriter = null;

			return false;
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
			return getClass().getSimpleName() + " for " + 
						NamedValuesLayout.this;
		}
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut)
	throws DataException {

		if (arooaDelimiter == null) {
			
			FlexibleDelimiterFactory delimiterFactory = new FlexibleDelimiterFactory();
			delimiterFactory.setDelimiter(delimiter == null ? DEFAULT_DELIMITER: delimiter);
			delimiterFactory.setQuote(quote);
			delimiterFactory.setEscape(escape);
			delimiterFactory.setAlwaysQuote(alwaysQuote);

			arooaDelimiter = delimiterFactory.newDelimiter();
		}
		
		if (fieldDataOut == null) {
			fieldDataOut = new SimpleFieldDataOut();
		}
		
		return new InternalWriter(dataOut.provideDataOut(
				StringsOut.class));
	}
	
	@Override
	public void reset() {
		super.reset();
		
		arooaTokenizer = null;
		arooaDelimiter = null;
		
		fieldDataIn = null;
		fieldDataOut = null;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
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

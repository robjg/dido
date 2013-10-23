package org.oddjob.dido.text;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
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
import org.oddjob.dido.layout.LayoutValueNode;
import org.oddjob.dido.layout.VoidIn;
import org.oddjob.dido.layout.VoidOut;

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
	
	
	@Override
	public Class<?> getType() {
		return Map.class;
	}

	private class NameValuePairReader implements DataReader {
		
		private final StringsIn stringsIn;
		
		private DataReader nextReader;
		
		public NameValuePairReader(StringsIn stringsIn) {
			this.stringsIn = stringsIn;
		}
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {
				
				return nextReader.read();
			}
			
			String[] fields = stringsIn.getValues();

			Map<String, String> map = new HashMap<String, String>();
			
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

			if (logger.isTraceEnabled()) {
				
				logger.trace("[" + NamedValuesLayout.this + "] value is [" + 
						map + "]");
			}
						
			// Todo: Support FieldsIn and children.
			nextReader = nextReaderFor(new VoidIn());
			
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
			return "Reader for [" + NamedValuesLayout.this + "]";
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

		return new NameValuePairReader(dataIn.provideDataIn(StringsIn.class));
	}

	class NamdValuePairWriter implements DataWriter {
		
		private final StringsOut stringsOut;
		
		private DataWriter nextWriter;
		
		public NamdValuePairWriter(StringsOut stringsOut) {
			this.stringsOut = stringsOut;
		}
		
		@Override
		public boolean write(Object object) throws DataException {
			
			if (nextWriter == null) {

				value(null);

				nextWriter = nextWriterFor(new VoidOut());
			}
			
			if (nextWriter.write(object)) {
				return true;
			}
					
			if (isWrittenTo()) {
					
				resetWrittenTo();
				
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
			return "Writer for [" + NamedValuesLayout.this + "]";
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
		
		return new NamdValuePairWriter(dataOut.provideDataOut(
				StringsOut.class));
	}
	
	@Override
	public void reset() {
		super.reset();
		
		arooaTokenizer = null;
		arooaDelimiter = null;
	}
}

package org.oddjob.dido.text;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.runtime.ExpressionParser;
import org.oddjob.arooa.runtime.ParsedExpression;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.field.FieldDataIn;
import org.oddjob.dido.field.FieldDataOut;
import org.oddjob.dido.field.FieldIn;
import org.oddjob.dido.field.FieldOut;
import org.oddjob.dido.layout.LayoutValueNode;
import org.oddjob.dido.tabular.ColumnData;


/**
 * @oddjob.description Perform a substitution on the value of the field when
 * reading and substitute the text when writing. 
 * <p>
 * When writing a substitution, child layouts will be called and used to
 * set the value property of this layout but this value will be ignored 
 * in favour of the substitution text when writing the data out.
 * <p>
 * The ability to substitute incoming data or write a substitution to 
 * outgoing data is particularly useful when creating files of test data
 * that will be replayed in different scenarios.
 * 
 * @oddjob.example
 * 
 * Substitute a date. This example demonstrates substituting the property
 * <code>order.date</code> defined in Oddjob with the first column of the
 * file. The substitution data is in the default Oddjob date format and
 * so no conversion is required.
 * 
 * {@oddjob.xml.resource org/oddjob/dido/text/SubstitutionLayoutExample.xml}
 * 
 * @oddjob.example Format and substitute a date. This example is like
 * the above but a nested {@link DateLayout} is used to format the date
 * 
 * {@oddjob.xml.resource org/oddjob/dido/text/SubstitutionLayoutExample.xml}
 * 
 * @author rob
 *
 */
public class SubstitutionLayout extends LayoutValueNode<String> 
implements FixedWidthColumn, ArooaSessionAware {

	private static final Logger logger = Logger.getLogger(SubstitutionLayout.class);

	private String substitution;
	
	private ArooaSession session;
	
	private String label;
	
	private int index;

	private int length;
	
	private FieldIn<Object> columnIn;
	
	private FieldOut<Object> columnOut;
			
	@ArooaHidden
	@Override
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	/**
	 * Read and item of data.
	 */
	private class InternalReader implements DataReader {
		
		private DataReader nextReader;
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {

				return nextReader.read();
			}
			
			Object data = columnIn.getData();
			String field = data == null ? null : data.toString();

			if (field == null) {
				value(null);
				return null;
			}
			
			String value = convertIn(field);
			value(value);

			logger.trace("[" + SubstitutionLayout.this + "] value is [" + 
					value + "]");
			
			if (value == null) {
				return null;
			}
			
			TextIn textIn = new StringTextIn(value);
			
			nextReader = nextReaderFor(textIn);
			
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
			return getClass().getSimpleName() + " for " + 
					SubstitutionLayout.this.toString();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DataReader readerFor(DataIn dataIn)
	throws DataException {

		if (columnIn == null) {
			
			FieldDataIn in = dataIn.provideDataIn(FieldDataIn.class);
			
			columnIn = (FieldIn<Object>) in.inFor(this);
						
			logger.trace("[" + this + "] initialised on [" + 
					columnIn+ "]");
		}
		
		return new InternalReader();
	}	
		
	/**
	 * Provide a writer. 
	 * 
	 * The writer will provide a child node or binding with the opportunity to
	 * write multiple times from one object (which is probably complete
	 * overkill)
	 *
	 */
	private class InternalWriter implements DataWriter {
		
		private final DataWriter nextWriter;
		
		private final StringTextOut textOut;
		
		public InternalWriter() throws DataException {
			this.textOut = new StringTextOut();
			this.nextWriter = nextWriterFor(textOut);
		}
		
		@Override
		public boolean write(Object object) throws DataException {

			if (nextWriter.write(object)) {
				return true;
			}
			
			value(textOut.toText());
		
			columnOut.setData(substitution);

			logger.trace("[" + SubstitutionLayout.this + "] wrote value [" + 
					substitution + "]");

			return false;
		}
		
		@Override
		public void close() throws DataException {
			
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " for " + 
					SubstitutionLayout.this.toString();
		}
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public DataWriter writerFor(DataOut dataOut)
	throws DataException {
		
		if (columnOut == null) {
			
			FieldDataOut out = dataOut.provideDataOut(FieldDataOut.class);
			
			columnOut = (FieldOut<Object>) out.outFor(this);
			
			logger.trace("[" + this + "] initialised on [" + 
					columnOut + "]");			
		}
		
		return new InternalWriter();
	}
	
	
	@Override
	public void reset() {
		super.reset();
		
		columnIn = null;
		columnOut = null;
	}
	
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String title) {
		this.label = title;
	}

	public int getIndex() {
		if (columnIn instanceof ColumnData) {
			return ((ColumnData) columnIn).getColumnIndex();
		}
		if (columnOut instanceof ColumnData) {
			return ((ColumnData) columnOut).getColumnIndex();
		}
		return index;
	}

	public void setIndex(int column) {
		this.index = column;
	}	
	
	public String getValue() {
		return this.value();
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}	
	
	protected String convertIn(String value) 
	throws DataException {
		ExpressionParser expressionParser = 
				session.getTools().getExpressionParser();

		String result = null;
		try {
			ParsedExpression parsed = expressionParser.parse(value);
			result = parsed.evaluate(session, String.class);		
		}
		catch (ArooaConversionException e) {
			throw new DataException("Failed substituting value " + value, e);
		}
		
		logger.trace("Replaced [" + value +
				"], with [" + result + "]");
		
		return result;
	}
	
	public String getSubsitition() {
		return substitution;
	}

	public void setSubstitution(String substitution) {
		this.substitution = substitution;
	}
}

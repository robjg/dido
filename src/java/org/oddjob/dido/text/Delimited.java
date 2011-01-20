package org.oddjob.dido.text;

import java.util.regex.Pattern;

import org.oddjob.dido.AbstractParentStencil;
import org.oddjob.dido.DataException;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;


public class Delimited 
extends AbstractParentStencil<String[], TextIn, FieldsIn, TextOut, FieldsOut> {

	public static final String DEFAULT = ",";
	
	private String delimiter;
	
	private String regexp;
	
	private String[] headings;
	
	private boolean withHeadings;
	
	private MappedFieldsIn fieldsIn;
	
	private boolean begun;
	
	@Override
	public Class<String[]> getType() {
		return String[].class;
	}
	
	public WhereNextIn<FieldsIn> in(TextIn textIn) throws DataException {
		
		
		String text = textIn.getText();
		
		String use = regexp == null ? DEFAULT : regexp;
		
		String fields[] = text.split(use, -1);
		
		if (!hasChildren()) {
			setValue(fields);
			
			return new WhereNextIn<FieldsIn>();
		}		
		
		if (fieldsIn == null) {			
			fieldsIn = new MappedFieldsIn();
			if (headings != null) {
				fieldsIn.setHeadings(headings);
			}
			
			fireChildrenBegin(fieldsIn);
		}
		
		fieldsIn.setValues(fields);
		
		return new WhereNextIn<FieldsIn>(childrenToArray(), 
				fieldsIn);	
	}
	
	@Override
	public void complete(TextIn textIn) throws DataException {
		if (fieldsIn != null) {
			fireChildrenEnd(fieldsIn);
			fieldsIn = null;
		}
	}
	
	public WhereNextOut<FieldsOut> out(final TextOut textOut) throws DataException {

		String[] values = getValue();
		if (values != null) {
			
			textOut.append(delimitedString(values));
			
			return new WhereNextOut<FieldsOut>();
		}

		if (!hasChildren()) {
			return null;
		}
		
		if (!begun) {

			FieldsOut headingsOut = new HeadingsFieldsOut(
					new MappedFieldsOut.FieldsWriter() {

						@Override
						public void write(String[] values) {
							textOut.append(delimitedString(values));
						}
					}, withHeadings);
			
			fireChildrenBegin(headingsOut);
			
			if (withHeadings) {
				headingsOut.flush();
				textOut.flush();
			}
			
			begun = true;
		}
		
		FieldsOut fieldsOut = new MappedFieldsOut(
				new MappedFieldsOut.FieldsWriter() {

			@Override
			public void write(String[] values) {
				textOut.append(delimitedString(values));
			}
		});
		
		return new WhereNextOut<FieldsOut>(childrenToArray(), 
				fieldsOut);
	}

	@Override
	public void flush(TextOut data, FieldsOut childData) 
	throws DataException {
		childData.flush();
	}
	
	
	@Override
	public void complete(final TextOut textOut) throws DataException {
		FieldsOut fieldsOut = new MappedFieldsOut(
				new MappedFieldsOut.FieldsWriter() {

			@Override
			public void write(String[] values) {
				textOut.append(delimitedString(values));
			}
		});
		
		fireChildrenEnd(fieldsOut);

		if (fieldsOut.flush()) {
			textOut.flush();
		}
		
		begun = false;
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

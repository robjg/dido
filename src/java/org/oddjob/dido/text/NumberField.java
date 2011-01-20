package org.oddjob.dido.text;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.oddjob.dido.AbstractStencil;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.WhereNext;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;

public class NumberField 
extends AbstractStencil<Number, TextIn, DataIn, TextOut, DataOut>{

	private String format;
	
	@Override
	public Class<Number> getType() {
		return Number.class;
	}
	
	@Override
	public WhereNext<DataNode<DataIn, ?, ?, ?>, DataIn> in(TextIn din)
			throws DataException {

		String number = din.getText().trim();
		
		if (number.length() == 0) {
			return null;
		}
		
		try {
			Number value;
			if (format == null) {
				value = Double.parseDouble(number);
			}
			else {
				value = new DecimalFormat(format).parse(number);
			}
			setValue(value);
			
		} catch (ParseException e) {
			throw new DataException(e);
		}
		
		return new WhereNextIn<DataIn>();
	}
	
	@Override
	public WhereNext<DataNode<?, ?, DataOut, ?>, DataOut> out(TextOut dout)
			throws DataException {
		
		Number value = getValue();
		
		if (value == null) {
			return null;
		}
		
		if (format == null) {
			dout.append(value.toString());
		}
		else {
			dout.append(new DecimalFormat(format).format(value));
		}
		
		return new WhereNextOut<DataOut>();
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}	
	
	public Number getValue() {
		return this.value();
	}
	
	public void setValue(Number value) {
		this.value(value);
	}
}

package org.oddjob.dido.text;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.oddjob.dido.DataException;

/**
 * 
 * @author rob
 *
 */
public class NumberLayout 
extends AbstractFieldLayout<Number> {

	private String format;
	
	@Override
	public Class<Number> getType() {
		return Number.class;
	}

	@Override
	protected Number convertIn(String value) throws DataException {

		String number = value.trim();
		
		if (number.length() == 0) {
			return null;
		}
		
		try {
			if (format == null) {
				return  Double.parseDouble(number);
			}
			else {
				return new DecimalFormat(format).parse(number);
			}
		} 
		catch (ParseException e) {
			throw new DataException(e);
		}
	}
	
	@Override
	protected String convertOut(Number value) throws DataException {

		if (value == null) {
			return null;
		}
		
		if (format == null) {
			return value.toString();
		}
		else {
			return new DecimalFormat(format).format(value);
		}
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
}

package org.oddjob.dido.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.dido.AbstractStencil;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;

public class DateField 
extends AbstractStencil<Date, TextIn, DataIn, TextOut, DataOut>{

	private String format;
	
	private String timeZone;
	
	@Override
	public Class<Date> getType() {
		return Date.class;
	}
	
	@Override
	public WhereNextIn<DataIn> in(TextIn din)
			throws DataException {

		String date = din.getText().trim();
		
		if (date.length() == 0) {
			return null;
		}
		
		TimeZone tz = TimeZone.getDefault(); 
		if (timeZone != null) {
			tz = TimeZone.getTimeZone(timeZone);
		}
		
		try {
			Date value;
			if (format == null) {
				value = DateHelper.parseDate(date, tz);
			}
			else {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				dateFormat.setTimeZone(tz);
				value = dateFormat.parse(date);
			}
			setValue(value);
			
		} catch (ParseException e) {
			throw new DataException(e);
		}
		
		return new WhereNextIn<DataIn>();
	}
	
	@Override
	public WhereNextOut<DataOut> out(TextOut dout)
			throws DataException {
		
		Date value = getValue();
		
		if (value == null) {
			return null;
		}
		
		TimeZone tz = TimeZone.getDefault(); 
		if (timeZone != null) {
			tz = TimeZone.getTimeZone(timeZone);
		}
				
		if (format == null) {
			dout.append(DateHelper.formatDateTime(value));
		}
		else {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			dateFormat.setTimeZone(tz);
			dout.append(dateFormat.format(value));
		}
		
		return new WhereNextOut<DataOut>();
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timesZone) {
		this.timeZone = timesZone;
	}	
	
	public Date getValue() {
		return this.value();
	}
	
	public void setValue(Date value) {
		this.value(value);
	}
}

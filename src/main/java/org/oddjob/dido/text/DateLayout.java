package org.oddjob.dido.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.dido.DataException;

/**
 * 
 * @author rob
 *
 */
public class DateLayout 
extends AbstractFieldLayout<Date> {

	private String format;
	
	private String timeZone;
	
	@Override
	public Class<Date> getType() {
		return Date.class;
	}
	
	@Override
	protected Date convertIn(String value) throws DataException {

		String date = value.trim();
		
		if (date.length() == 0) {
			return null;
		}
		
		TimeZone tz = TimeZone.getDefault(); 
		if (timeZone != null) {
			tz = TimeZone.getTimeZone(timeZone);
		}
		
		try {
			if (format == null) {
				return DateHelper.parseDate(date, tz);
			}
			else {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				dateFormat.setTimeZone(tz);
				return dateFormat.parse(date);
			}
		} 
		catch (ParseException e) {
			throw new DataException(e);
		}
	}
	
	@Override
	protected String convertOut(Date value) {

		if (value == null) {
			return null;
		}
		
		TimeZone tz = TimeZone.getDefault(); 
		if (timeZone != null) {
			tz = TimeZone.getTimeZone(timeZone);
		}
				
		if (format == null) {
			return DateHelper.formatDateTime(value);
		}
		else {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			dateFormat.setTimeZone(tz);
			return dateFormat.format(value);
		}
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
}

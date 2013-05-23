package org.oddjob.dido.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.layout.LayoutValueNode;
import org.oddjob.dido.layout.NullReader;

public class DateField 
extends LayoutValueNode<Date>{

	private String format;
	
	private String timeZone;
	
	@Override
	public Class<Date> getType() {
		return Date.class;
	}
	

	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		final TextIn din = dataIn.provide(TextIn.class);

		String date = din.getText().trim();
		
		if (date.length() == 0) {
			return new NullReader();
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
		
		return nextReaderFor(null);
	}
	
	
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {

		final TextOut dout = dataOut.provide(TextOut.class);
		
		return new DataWriter() {
			
			DataWriter nextWriter;
			
			@Override
			public boolean write(Object object) throws DataException {
				
				if (nextWriter == null) {
					
					value(null);
					
					nextWriter = nextWriterFor(null);
				}
				
				if (nextWriter.write(object)) {
					return true;
				}
				
				nextWriter = null;

				Date value = value();
				if (value == null) {
					return false;
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
				
				return false;
			}
		};
	}

	@Override
	public void reset() {
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

package org.oddjob.dido.text;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.layout.LayoutValueNode;

public class NumberField 
extends LayoutValueNode<Number>{

	private String format;
	
	@Override
	public Class<Number> getType() {
		return Number.class;
	}

	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		TextIn din = dataIn.provide(TextIn.class);

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
			value(value);
			
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
				
				nextWriter.close();
				nextWriter = null;
				
				Number value = value();
				
				if (value == null) {
					return false;
				}
				
				if (format == null) {
					dout.append(value.toString());
				}
				else {
					dout.append(new DecimalFormat(format).format(value));
				}
				
				return false;
			}
			
			@Override
			public void close() throws DataException {
			}
		};
	}

	@Override
	public void reset() {
		value(null);
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

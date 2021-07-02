package org.oddjob.dido.stream;

import java.io.InputStream;
import java.io.OutputStream;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataInException;
import org.oddjob.dido.UnsupportedDataOutException;

/**
 * @oddjob.description Provide Dido a way of reading or writing from either 
 * a Java {@code InputStream} or {@code OutputStream} respectively.
 * <p>
 * This is the type to use if you want to read or write files for instance.
 * 
 * @author rob
 *
 */
public class IOStreamData implements DataIn, DataOut, ArooaSessionAware{

	private ArooaValue input;
	
	private ArooaValue output;
	
	private ArooaConverter converter;
	
	@ArooaHidden
	@Override
	public void setArooaSession(ArooaSession session) {
		this.converter = session.getTools().getArooaConverter();
	}
	
	public void setInput(ArooaValue input) {
		this.input = input;
	}
	
	public ArooaValue getInput() {
		return input;
	}
	
	public void setOutput(ArooaValue output) {
		this.output = output;
	}
	
	public ArooaValue getOutput() {
		return output;
	}
	
	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type) 
	throws DataException{
		
		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		if (type.isAssignableFrom(LinesIn.class)) {
			if (input == null) {
				throw new NullPointerException("No input!");
			}
			
			if (converter == null) {
				throw new NullPointerException("No converter. Session not set?");
			}
			
			InputStream inputStream;
			try {
				inputStream = converter.convert(input, InputStream.class);
			} catch (NoConversionAvailableException e) {
				throw new DataException(e);
			} catch (ConversionFailedException e) {
				throw new DataException(e);
			}
			
			return type.cast(new StreamLinesIn(inputStream));
		}
		
		throw new UnsupportedDataInException(this.getClass(), type);
	}
	
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type)
	throws DataException {
		
		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		if (type.isAssignableFrom(LinesOut.class)) {
			if (output == null) {
				throw new NullPointerException("No output!");
			}
			
			if (converter == null) {
				throw new NullPointerException("No converter. Session not set?");
			}
			
			OutputStream outputStream;
			try {
				outputStream = converter.convert(output, OutputStream.class);
			} catch (NoConversionAvailableException e) {
				throw new DataException(e);
			} catch (ConversionFailedException e) {
				throw new DataException(e);
			}
			
			return type.cast(new StreamLinesOut(outputStream));
		}
		
		throw new UnsupportedDataOutException(getClass(), type);
	}
	
	@Override
	public boolean isWrittenTo() {
		throw new UnsupportedOperationException();
	}
	
}

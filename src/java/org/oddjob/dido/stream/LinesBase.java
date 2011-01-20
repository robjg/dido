package org.oddjob.dido.stream;

import org.oddjob.dido.AbstractParentStencil;
import org.oddjob.dido.DataException;
import org.oddjob.dido.Section;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;
import org.oddjob.dido.text.StringTextIn;
import org.oddjob.dido.text.StringTextOut;
import org.oddjob.dido.text.TextIn;
import org.oddjob.dido.text.TextOut;

public abstract class LinesBase 
extends AbstractParentStencil<String, LinesIn, TextIn, LinesOut, TextOut>
implements Section {

	private int lineCount;
		
	private boolean initialised;
	
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	public WhereNextIn<TextIn> in(LinesIn stream) 
	throws DataException {
		
		if (!initialised) {
			lineCount = 0;
			initialised = true;
		}
		
		if (getRequired() > 0 && lineCount >= getRequired()) {
			return null;
		}
		
		if (getRequired() > 0 && lineCount >= getRequired()) {
			return null;
		}
		
		String line = stream.readLine();
		if (line == null) {
			return null;
		}
		
		++lineCount;
		
		if (hasChildren()) {
			return new WhereNextIn<TextIn>(
					childrenToArray(), new StringTextIn(line));
		}
		else {
			setValue(line);
			return new WhereNextIn<TextIn>();
		}		
	}

	@Override
	public void complete(LinesIn in) throws DataException {
		initialised = false;
	}
	
	public WhereNextOut<TextOut> out(final LinesOut out) 
	throws DataException {
		
		if (!initialised) {
			lineCount = 0;
			initialised = true;
		}

		if (getRequired() > 0 && lineCount >= getRequired()) {
			return null;
		}
		
		String value = getValue();
		
		if (value != null) {
			out.writeLine(value);
			
			return new WhereNextOut<TextOut>();			
		}
		else if (hasChildren()) {
			
			StringTextOut line = new StringTextOut() {
				@Override
				public boolean flush() throws DataException {
					out.writeLine(this.toString());
					clear();
					return true;
				}
			};
			
			return new WhereNextOut<TextOut>(
					childrenToArray(), line);
		}
		else {
			return null;			
		}		
	}
	
	@Override
	public void complete(LinesOut out) throws DataException {
		initialised = false;
	}
	
	@Override
	public void flush(LinesOut data, TextOut childData) 
	throws DataException {
		childData.flush();
	}
	
	public void setValue(String value) {
		this.value(value);
	}
	
	public String getValue() {
		return this.value();
	}
	
	abstract public int getRequired();
}

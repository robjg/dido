package org.oddjob.dido.text;

import org.oddjob.dido.AbstractParentStencil;
import org.oddjob.dido.DataException;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;


public class Text 
extends AbstractParentStencil<String, TextIn, TextIn, TextOut, TextOut> {

	private int from;

	private int length;
	
	private boolean raw;
	
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	public WhereNextIn<TextIn> in(TextIn textIn) {

		String inString = textIn.getText();
		String newText;

		
		if (from > inString.length()) {
			newText = "";
		}
		else if (length > 0) {
			int to = from + length;
			if (to > inString.length()) {
				to = inString.length();
			}
			newText = inString.substring(from, to);			
		}
		else {
			newText = inString.substring(from);
		}
		if (!raw) {
			newText = newText.trim();
		}
				
		if (hasChildren()) {
			StringTextIn substr = new StringTextIn(newText);
			
			return new WhereNextIn<TextIn>(
					childrenToArray(), substr);
		}
		else {
			setValue(newText);
			
			return new WhereNextIn<TextIn>();
		}
	}
	
	public WhereNextOut<TextOut> out(final TextOut outgoing) 
	throws DataException {
		
		String value = getValue();
		
		
		if (value != null) {
			outgoing.write(value, from, length == 0 ? value.length() : length);
			return new WhereNextOut<TextOut>();
		}
		
		if (!hasChildren()) {
			return new WhereNextOut<TextOut>();
		}
		else {
			TextOut textOut = new StringTextOut() {
				@Override
				public boolean flush() throws DataException {
					String value = toString();
					outgoing.write(value, from, length == 0 ? value.length() : length);
					return true;
				}
			};
			
			return new WhereNextOut<TextOut>(
					childrenToArray(), textOut);
		}
	}
		
	@Override
	public void flush(TextOut data, TextOut childData) throws DataException {
		childData.flush();
	}
	
	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isRaw() {
		return raw;
	}

	public void setRaw(boolean trim) {
		this.raw = trim;
	}
	
	public void setValue(String value) {
		this.value(value);
	}
	
	public String getValue() {
		return this.value();
	}
}

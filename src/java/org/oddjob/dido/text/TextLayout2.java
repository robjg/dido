package org.oddjob.dido.text;

import org.oddjob.dido.Layout;


/**
 * @oddjob.description A field. 
 * 
 * @author rob
 *
 */
public class TextLayout2 
extends AbstractFieldLayout<String> {

	private boolean raw;
	
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	protected String convertIn(String value) {
		if (raw) {
			return value;
		}
		else {
			return value.trim();
		}
	}
	
	@Override
	protected String convertOut(String value) {
		return value;
	}
	
	public boolean isRaw() {
		return raw;
	}

	public void setRaw(boolean trim) {
		this.raw = trim;
	}
}

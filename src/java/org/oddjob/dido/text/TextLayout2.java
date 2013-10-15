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

	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	protected String convertIn(String value) {
		return value;
	}
	
	@Override
	protected String convertOut(String value) {
		return value;
	}
	
}

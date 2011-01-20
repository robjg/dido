package org.oddjob.dido.text;


public class StringTextIn implements TextIn {

	private final String text;
	
	public StringTextIn(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}	
	
	public String toString() {
		return text;
	}
}

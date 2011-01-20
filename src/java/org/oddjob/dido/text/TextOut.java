package org.oddjob.dido.text;

import org.oddjob.dido.DataOut;

public interface TextOut extends DataOut {

	public void append(String text);
	
	public void write(String text, int from, int length);

}

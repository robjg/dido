package org.oddjob.dido.text;

import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataValueOut;

public interface TextOut extends DataOut, DataValueOut {

	public void append(String text);
	
	public void write(String text, int from, int length);

	public int length();
}

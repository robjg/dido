package org.oddjob.dido.text;

import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataOutProvider;

public interface TextOut extends DataOut, DataOutProvider {

	public void append(String text);
	
	public void write(String text, int from, int length);

	public int length();
}

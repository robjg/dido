package org.oddjob.dido.text;

import org.oddjob.dido.DataIn;

public interface FieldsIn extends DataIn {

	public int columnFor(String heading, boolean optional,
			int column);
	
	public String getColumn(int column);
	
}

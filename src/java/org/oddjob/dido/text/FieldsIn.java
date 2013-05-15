package org.oddjob.dido.text;

import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataInProvider;

public interface FieldsIn extends DataIn, DataInProvider {

	public int columnFor(String heading, boolean optional,
			int column);
	
	public String getColumn(int column);
	
}

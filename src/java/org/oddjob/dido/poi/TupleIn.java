package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.oddjob.dido.DataIn;

public interface TupleIn extends DataIn {
	
	public Cell getCell(int column);
		
	public int indexForHeading(String title);
}

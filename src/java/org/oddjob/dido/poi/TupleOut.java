package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.oddjob.dido.DataOut;

public interface TupleOut extends DataOut, StyleProvider {

	public Cell createCell(int column, int type);
	
	public int indexForHeading(String heading);	
	
}

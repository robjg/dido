package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.CellStyle;

public interface StyleProvider {

	public CellStyle styleFor(String styleName);
	
}

package org.oddjob.dido.poi.style;

import org.apache.poi.ss.usermodel.CellStyle;

public interface StyleProvider {

	public CellStyle styleFor(String styleName);
	
}

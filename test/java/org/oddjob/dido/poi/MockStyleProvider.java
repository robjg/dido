package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.CellStyle;
import org.oddjob.dido.poi.StyleProvider;

public class MockStyleProvider implements StyleProvider {

	@Override
	public CellStyle styleFor(String style) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
}

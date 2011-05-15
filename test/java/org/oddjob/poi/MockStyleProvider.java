package org.oddjob.poi;

import org.apache.poi.ss.usermodel.CellStyle;

public class MockStyleProvider implements StyleProvider {

	@Override
	public CellStyle styleFor(String style) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
}

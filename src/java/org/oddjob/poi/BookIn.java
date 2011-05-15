package org.oddjob.poi;

import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataIn;

public interface BookIn extends DataIn {

	Sheet nextSheet();
	
	Sheet getSheet(String sheetName);
}

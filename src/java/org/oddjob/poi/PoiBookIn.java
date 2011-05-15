package org.oddjob.poi;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class PoiBookIn implements BookIn {

	private final Workbook workbook;
	
	private int sheet = 0;
	
	public PoiBookIn(InputStream input) throws InvalidFormatException, IOException {
		workbook = WorkbookFactory.create(input);
	}
	
	@Override
	public Sheet getSheet(String sheetName) {
		return workbook.getSheet(sheetName);
	}
	
	@Override
	public Sheet nextSheet() {
		return workbook.getSheetAt(sheet++);
	}	
}

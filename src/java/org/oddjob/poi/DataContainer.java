package org.oddjob.poi;

import java.util.Date;

public interface DataContainer {

	public int getCellType();
	
	public double getNumericCellValue();

	public Date getDateCellValue();
	
	public String getStringValue();
	
	public boolean getBooleanCellValue();
	
	public byte getErrorCellValue();
	
	public String getCellFormula();
}

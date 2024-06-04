package dido.poi.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Something that can create a style.
 * 
 * @author rob
 *
 */
public interface StyleFactory {

	public CellStyle createStyle(Workbook workbook);
	
}

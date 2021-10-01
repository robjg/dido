package org.oddjob.dido.poi.data;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.oddjob.dido.poi.HeaderRowOut;
import org.oddjob.dido.poi.RowOut;
import org.oddjob.dido.poi.style.StyleProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PoiRowsOutTest {

	@Test
	public void testCreateCellInWithHeader() {
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();

		StyleProvider styleProvider = Mockito.mock(StyleProvider.class);
		
		PoiRowsOut test = new PoiRowsOut(sheet, styleProvider, 3, 7);
		
		assertThat(test.getLastRow(), is(2));
		assertThat(test.getLastColumn(), is(6));

		HeaderRowOut headerRowOut = test.headerRow(null);

		headerRowOut.setHeader(2, "Quantity");

		assertThat(test.getLastRow(), is(3));
		assertThat(test.getLastColumn(), is(8));

		Row row = sheet.getRow(2);
		assertThat(row.getCell(7).getStringCellValue(), is("Quantity"));

		test.nextRow();

		RowOut rowOut = test.getRowOut();
		Cell cellOut =  rowOut.getCell(2, CellType.NUMERIC);

		cellOut.setCellValue(17);
		
		row = sheet.getRow(3);
		assertThat(row.getCell(7).getNumericCellValue(), is((17.0)));

		assertThat(test.getLastRow(), is(4));
		assertThat(test.getLastColumn(), is(8));
	}
}

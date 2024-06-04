package dido.poi.data;

import dido.poi.data.PoiRowsIn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import dido.poi.RowIn;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PoiRowsInTest {


	@Test
	public void testCreateCellInWithHeader() {
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();

		Row row = sheet.createRow(2);
		row.createCell(6).setCellValue("Fruit");
		row.createCell(7).setCellValue("Quantity");
		row.createCell(8).setCellValue("Price");
		
		PoiRowsIn test = new PoiRowsIn(sheet, 3, 7);
		
		assertThat(test.getLastRow(), is(2));

		assertThat(test.headerRow(), arrayContaining("Fruit", "Quantity", "Price"));

		assertThat(test.getLastRow(), is(3));

		assertThat(test.nextRow(), nullValue());

		row = sheet.createRow(3);
		row.createCell(6).setCellValue("Apple");
		row.createCell(7).setCellValue(17);
		row.createCell(8).setCellValue(24.3);
		
		RowIn rowIn = test.nextRow();
		assertThat(rowIn, notNullValue());

		Cell cell1 = rowIn.getCell(2);

		assertThat(cell1.getColumnIndex(), is(7));
		assertThat(cell1.getNumericCellValue(), is(17.0));

		assertThat(test.getLastRow(), is(4));
	}
}

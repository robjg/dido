package org.oddjob.dido.poi.data;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.oddjob.OurDirs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PoiAssumptionsTest {

	static File workDir;
	
	@BeforeAll
	protected static void setUp() throws Exception {
	
		workDir = OurDirs.workPathDir(PoiAssumptionsTest.class).toFile();
	}
	
	@Test
	void testIndexAssumptions() throws IOException {
		
		Workbook workbook = new XSSFWorkbook();

		assertThat(workbook.getNumberOfSheets(), is(0));
		
		Sheet sheet = workbook.createSheet("Test");

		// This has change from 0 since version 3.
		assertThat(sheet.getLastRowNum(), is(-1));

		Row row0 = sheet.getRow(0);

		assertThat(row0, nullValue());

		sheet.createRow(0);

		assertThat(sheet.getLastRowNum(), is(0));

		Row row4 = sheet.createRow(4);

		assertThat(sheet.getLastRowNum(), is(4));

		assertThat(sheet.getRow(5), nullValue());
		assertThat(sheet.getRow(3), nullValue());

		assertThat(row4.getLastCellNum(), is((short) -1));

		assertThat(row4.getCell(0), nullValue());

		row4.createCell(0);
		
		// Note that last row and last cell are inconsistent!
		assertThat(row4.getLastCellNum(), is((short) 1));

		Cell cell7 = row4.createCell(7);

		assertThat(row4.getLastCellNum(), is((short) 8));

		assertThat(row4.getCell(5), nullValue());

		cell7.setCellValue("TEST");

		assertThat(cell7.getStringCellValue(), is("TEST"));

		// Looks like cell is re-created.
		Cell cell7a = row4.createCell(7);

		assertThat(cell7, not(Matchers.sameInstance(cell7a)));

		assertThat(cell7a.getStringCellValue(), is(""));

		cell7a.setCellValue("Apples");

		OutputStream out = new FileOutputStream(
				new File(workDir, "PoiAssumptions.xlsx"));

		workbook.write(out);
		out.close();
	}

	@Test
	void testMakingACellBlank() {
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Test");
		Row row = sheet.createRow(0);
				
		Cell cell1 = row.createCell(0, CellType.STRING);
		cell1.setCellValue("apples");
				
		cell1.setBlank();

		assertThat(cell1.getRichStringCellValue().toString(), is(""));
		assertThat(cell1.getNumericCellValue(), is(0.0));

		row.removeCell(cell1);

		Cell cell1_ = row.getCell(0);
		assertThat(cell1_, nullValue());
	}

	@Test
	void testWritingAndReadingNulls() {

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Test");
		Row row = sheet.createRow(0);

		Cell cell1 = row.createCell(0, CellType.STRING);
		cell1.setCellValue((String) null);

		assertThat(cell1.getStringCellValue(), is(""));
		assertThat(cell1.getRichStringCellValue().toString(), is(""));
		assertThat(cell1.getNumericCellValue(), is(0.0));

		Cell cell2 = row.createCell(2, CellType.NUMERIC);
		try {
			cell2.setCellValue((Double) null);
			MatcherAssert.assertThat("Should Fail because it calls the double version", false);
		}
		catch (NullPointerException e) {
			// expected
		}

		cell2.setBlank();

		assertThat(cell2.getStringCellValue(), is(""));
		assertThat(cell2.getRichStringCellValue().toString(), is(""));
		assertThat(cell2.getNumericCellValue(), is(0.0));
	}

	@Test
	void testCellRangeAddress() {
		
		CellRangeAddress test = new CellRangeAddress(2, 5, 3, 7);

		assertThat(test.formatAsString(), is("D3:H6"));
	}

}

package org.oddjob.dido.poi.data;

import dido.data.ArrayData;
import dido.data.GenericData;
import dido.poi.CellIn;
import dido.poi.CellOut;
import junit.framework.TestCase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.mockito.Mockito;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.dido.poi.*;
import org.oddjob.dido.poi.layouts.*;
import org.oddjob.dido.poi.style.StyleBean;
import org.oddjob.dido.poi.style.StyleFactoryRegistry;
import org.oddjob.dido.poi.style.StyleProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PoiRowsTest extends TestCase {

	public void testWriteNextAndGetNext() throws IOException {
		
		TextCell text = new TextCell();

		PoiWorkbook workbook = new PoiWorkbook();

		BookOut bookOut = workbook.provideBookOut();

		StyleProvider styleProvider = Mockito.mock(StyleProvider.class);
		RowsOut test1 = new PoiRowsOut(bookOut.getOrCreateSheet(null), styleProvider, 0, 0);
		
		test1.nextRow();
		
		CellOut<String> cellOut = text.provideCellOut(1);

		cellOut.setValue(test1.getRowOut(), ArrayData.of("apples"));

		Cell cell1 = workbook.getWorkbook().getSheetAt(0).getRow(0).getCell(0);
		assertEquals(CellType.STRING, cell1.getCellType());
				
		test1.nextRow();
		
		cellOut.setValue(test1.getRowOut(), ArrayData.of("oranges"));
				
		assertEquals(2, test1.getLastRow());
		
		bookOut.close();
		
		// Read Test
		////////////
		
		BookIn bookIn = workbook.provideBookIn();

		RowsIn test2 = new PoiRowsIn(bookIn.getSheet(null), 0, 0);
		

		RowIn rowIn = test2.nextRow();
		assertThat(rowIn, notNullValue());

		CellIn<String> cellIn = text.provideCellIn(1);

		assertEquals("apples", cellIn.getValue(rowIn));
		
		rowIn = test2.nextRow();
		assertThat(rowIn, notNullValue());

		assertEquals("oranges", cellIn.getValue(rowIn));

		assertThat(test2.nextRow(), nullValue());
	}
	
	public void testDifferentCellTypes() throws IOException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		BookOut bookOut = workbook.provideBookOut();

		StyleProvider styleProvider = Mockito.mock(StyleProvider.class);
		RowsOut test1 = new PoiRowsOut(bookOut.getOrCreateSheet(null), styleProvider,
				0, 0);

		test1.nextRow();
		
		BlankCell blank = new BlankCell();

		CellOut<Void> cellOut1 = blank.provideCellOut(1);
		cellOut1.setValue(test1.getRowOut(), ArrayData.of((Object) null));

		Cell cell1 = workbook.getWorkbook()
				.getSheetAt(0)
				.getRow(0)
				.getCell(0);
		assertEquals(CellType.BLANK, cell1.getCellType());
		
		TextCell text = new TextCell();

		CellOut<String> cellOut2 = text.provideCellOut(1);
		cellOut2.setValue(test1.getRowOut(), ArrayData.of("apples"));

		assertEquals(CellType.STRING, cell1.getCellType());

		try {
			cell1.getCellFormula();
			fail("Shouldn't be possible.");
		}
		catch (IllegalStateException e) {
			// expected
		}
		
		try {
			cell1.getNumericCellValue();
			fail("Shouldn't be possible.");
		}
		catch (IllegalStateException e) {
			// expected
		}
		
		NumericCell<Double> numeric = new NumericCell<>();

		CellOut<Double> cellOut3 = numeric.provideCellOut(1);
		cellOut3.setValue(test1.getRowOut(), ArrayData.of(12.2));

		assertEquals(CellType.NUMERIC, cell1.getCellType());
		
		NumericFormulaCell formula = new NumericFormulaCell();
		formula.setIndex(1);
		formula.setFormula("6/2");

		CellOut<Double> cellOut4 = formula.provideCellOut(1);
		cellOut4.setValue(test1.getRowOut(), ArrayData.of(12.2));
		
		assertEquals(CellType.FORMULA, cell1.getCellType());
		
		bookOut.close();
	}
	
	public void testHeadings() throws IOException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		BookOut bookOut = workbook.provideBookOut();

		TextCell cell1 = new TextCell();
		cell1.setName("Name");
		NumericCell<Double> cell2 = new NumericCell<>();
		cell2.setName("Age");

		StyleProvider styleProvider = Mockito.mock(StyleProvider.class);
		RowsOut testOut = new PoiRowsOut(bookOut.getOrCreateSheet(null),
				styleProvider, 8, 4);

		HeaderRowOut headerRowOut = testOut.headerRow(null);

		RowOut rowOut = testOut.getRowOut();
		
		CellOut<String> cellOut1 = cell1.provideCellOut(1);
		CellOut<Double> cellOut2 = cell2.provideCellOut(2);

		cellOut1.writeHeader(headerRowOut);
		cellOut2.writeHeader(headerRowOut);

		testOut.nextRow();

		GenericData<String> data = ArrayData.of("John", 25.0);

		cellOut1.setValue(rowOut, data);
		cellOut2.setValue(rowOut, data);
		
		bookOut.close();
		
		///
		// Read Part
	
		BookIn bookIn = workbook.provideBookIn();

		RowsIn testIn = new PoiRowsIn(bookIn.getSheet(null),
				8, 4);
				
		String[] headers = testIn.headerRow();

		assertThat(headers, is(new String[] { "Name", "Age"}));

		CellIn<String> cellIn1 = cell1.provideCellIn(1);
		CellIn<Double> cellIn2 = cell2.provideCellIn(2);
		
		RowIn rowIn = testIn.nextRow();
		assertThat(rowIn, notNullValue());

		assertThat(cellIn1.getValue(rowIn), is("John"));
		assertThat(cellIn2.getValue(rowIn), is(25.0));
		
		assertThat(testIn.nextRow(), nullValue());
	}
	
	/**
	 * An old test refactored for changes so much that I'm not quite sure 
	 * what it proves any more.
	 * 
	 */
	@Ignore
	@Disabled
	public void testDateCellTypes() throws ParseException, IOException {

		if (true) {
			return;
		}

		PoiWorkbook workbook = new PoiWorkbook();
		
		StyleBean styleBean = new StyleBean();
		styleBean.setFormat("m/d/yy h:mm");
		
		StyleFactoryRegistry styles = new StyleFactoryRegistry();
		styles.registerStyle("my-date-format", styleBean);
		
		BookOut bookOut = workbook.provideBookOut();

		StyleProvider styleProvider = styles.providerFor(workbook.getWorkbook());

		Sheet sheet = bookOut.getOrCreateSheet(null);

		RowsOut test1 = new PoiRowsOut(sheet, styleProvider,
				0, 0);

		test1.nextRow();
		
		Date theDate = DateHelper.parseDateTime("2010-12-25 12:45");
		
		RowOut rowOut = test1.getRowOut();
		
		BlankCell blank = new BlankCell();

		CellOut<Void> cellOut1 = blank.provideCellOut(1);
		cellOut1.setValue(rowOut, ArrayData.of((Object) null));
		
		Cell cell1 = sheet.getRow(0).getCell(0);
		
		assertEquals(CellType.BLANK, cell1.getCellType());
		
		DateCell dateColumn = new DateCell();
		dateColumn.setStyle("my-date-format");

		CellOut<Date> cellOut2 = dateColumn.provideCellOut(1);
		cellOut2.setValue(rowOut, ArrayData.of(theDate));
		
		assertEquals(CellType.NUMERIC, cell1.getCellType());

		assertEquals(theDate, cell1.getDateCellValue());
				
		try {
			cell1.getStringCellValue();
			fail("Shouldn't be possible.");
		}
		catch (IllegalStateException e) {
			// expected
		}
	}	
}

package dido.poi.data;

import dido.data.DidoData;
import dido.data.ReadSchema;
import dido.data.immutable.ArrayData;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import dido.poi.*;
import dido.poi.layouts.*;
import dido.poi.style.StyleBean;
import dido.poi.style.StyleFactoryRegistry;
import dido.poi.style.StyleProvider;
import dido.poi.utils.DataRowFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.utils.DateHelper;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class PoiRowsTest {

	@Test
	public void testWriteNextAndGetNext() {
		
		TextCell text = new TextCell();

		PoiWorkbook workbook = new PoiWorkbook();

		BookOut bookOut = workbook.provideBookOut();

		StyleProvider styleProvider = mock(StyleProvider.class);
		RowsOut test1 = new PoiRowsOut(bookOut.getOrCreateSheet(null), styleProvider, 0, 0);
		
		test1.nextRow();

		DidoData data = ArrayData.of("apples");
		ReadSchema readSchema = ReadSchema.from(data.getSchema());

		CellOut cellOut = text.provideCellOut(readSchema, 1,
				mock(DidoConversionProvider.class));

		cellOut.setValue(test1.getRowOut(), data);

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

		DataRowFactory rowFactory = DataRowFactory.newInstance(readSchema,
				List.of(text), DefaultConversionProvider.defaultInstance());

		RowIn rowIn = test2.nextRow();
		assertThat(rowIn, notNullValue());

		DidoData data1 = rowFactory.wrap(rowIn);

		assertEquals("apples", data1.getAt(1));
		
		rowIn = test2.nextRow();
		assertThat(rowIn, notNullValue());

		DidoData data2 = rowFactory.wrap(rowIn);

		assertEquals("oranges", data2.getAt(1));

		assertThat(test2.nextRow(), nullValue());
	}

	@Test
	public void testDifferentCellTypes() {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		BookOut bookOut = workbook.provideBookOut();

		StyleProvider styleProvider = mock(StyleProvider.class);
		RowsOut test1 = new PoiRowsOut(bookOut.getOrCreateSheet(null), styleProvider,
				0, 0);

		test1.nextRow();
		
		BlankCell blank = new BlankCell();

		DidoData data1 = ArrayData.of((Object) null);

		CellOut cellOut1 = firstCellFor(blank, data1);

		cellOut1.setValue(test1.getRowOut(), data1);

		Cell cell1 = workbook.getWorkbook()
				.getSheetAt(0)
				.getRow(0)
				.getCell(0);
		assertEquals(CellType.BLANK, cell1.getCellType());
		
		TextCell text = new TextCell();

		DidoData data2 = ArrayData.of("apples");

		CellOut cellOut2 = firstCellFor(text,data2);

		cellOut2.setValue(test1.getRowOut(), data2);

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
		
		NumericCell numeric = new NumericCell();

		DidoData data3 = ArrayData.of(12.2);

		CellOut cellOut3 = firstCellFor(numeric, data3);
		cellOut3.setValue(test1.getRowOut(), data3);

		assertEquals(CellType.NUMERIC, cell1.getCellType());
		
		NumericFormulaCell formula = new NumericFormulaCell();
		formula.setIndex(1);
		formula.setFormula("6/2");

		DidoData data4 = ArrayData.of(12.2);

		CellOut cellOut4 = firstCellFor(formula, data4);
		cellOut4.setValue(test1.getRowOut(), data4);
		
		assertEquals(CellType.FORMULA, cell1.getCellType());
		
		bookOut.close();
	}

	CellOut firstCellFor(CellOutProvider outProvider, DidoData data) {

		ReadSchema readSchema = ReadSchema.from(data.getSchema());

		return outProvider.provideCellOut(readSchema,1, DefaultConversionProvider.defaultInstance());
	}


	public void testHeadings() throws IOException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		BookOut bookOut = workbook.provideBookOut();

		TextCell cell1 = new TextCell();
		cell1.setName("Name");
		NumericCell cell2 = new NumericCell();
		cell2.setName("Age");

		StyleProvider styleProvider = mock(StyleProvider.class);
		RowsOut testOut = new PoiRowsOut(bookOut.getOrCreateSheet(null),
				styleProvider, 8, 4);

		HeaderRowOut headerRowOut = testOut.headerRow(null);

		RowOut rowOut = testOut.getRowOut();

		DidoData data = ArrayData.of("John", 25.0);
		ReadSchema readSchema = ReadSchema.from(data.getSchema());

		CellOut cellOut1 = cell1.provideCellOut(readSchema, 1,
				mock(DidoConversionProvider.class));
		CellOut cellOut2 = cell2.provideCellOut(readSchema, 2,
				mock(DidoConversionProvider.class));

		cellOut1.writeHeader(headerRowOut);
		cellOut2.writeHeader(headerRowOut);

		testOut.nextRow();

		cellOut1.setValue(rowOut, data);
		cellOut2.setValue(rowOut, data);
		
		bookOut.close();
		
		///
		// Read Part
	
		BookIn bookIn = workbook.provideBookIn();

		RowsIn testIn = new PoiRowsIn(bookIn.getSheet(null),
				8, 4);
				
		String[] headings = testIn.headerRow();

		assertThat(headings, is(new String[] { "Name", "Age"}));

		DataRowFactory rowFactory = DataRowFactory.newInstance(readSchema,
				List.of(cell1, cell2),
				DefaultConversionProvider.defaultInstance());

		RowIn rowIn = testIn.nextRow();
		assertThat(rowIn, notNullValue());

		DidoData data1 = rowFactory.wrap(rowIn);

		assertThat(data1.getAt(1), is("John"));
		assertThat(data1.getAt(2), is(25.0));
		
		assertThat(testIn.nextRow(), nullValue());
	}
	
	/**
	 * An old test refactored for changes so much that I'm not quite sure 
	 * what it proves any more.
	 * 
	 */
	void testDateCellTypes() throws ParseException {


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

		DidoData data1 = ArrayData.of((Object) null);

		CellOut cellOut1 = firstCellFor(blank, data1);
		cellOut1.setValue(rowOut, data1);
		
		Cell cell1 = sheet.getRow(0).getCell(0);
		
		assertEquals(CellType.BLANK, cell1.getCellType());
		
		DateCell dateColumn = new DateCell();
		dateColumn.setStyle("my-date-format");

		DidoData data2 = ArrayData.of(theDate);

		CellOut cellOut2 = firstCellFor(dateColumn, data2);
		cellOut2.setValue(rowOut, data2);
		
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

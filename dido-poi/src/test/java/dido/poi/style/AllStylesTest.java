package dido.poi.style;

import dido.data.ArrayData;
import dido.data.DidoData;
import dido.how.DataOut;
import dido.poi.CellIn;
import dido.poi.CellOut;
import dido.poi.HeaderRowOut;
import dido.poi.RowOut;
import dido.poi.data.DataCell;
import dido.poi.data.PoiWorkbook;
import dido.poi.layouts.DataRows;
import dido.poi.layouts.TextCell;
import dido.test.OurDirs;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;

public class AllStylesTest {

	File workDir;
	
	@BeforeEach
	protected void setUp() throws Exception {

		workDir = OurDirs.workPathDir(AllStylesTest.class).toFile();
	}


	static class OurCell implements DataCell<String> {

		@Override
		public int getIndex() {
			return 1;
		}

		@Override
		public CellIn<String> provideCellIn(int columnIndex) {
			return null;
		}

		@Override
		public CellOut<String> provideCellOut(int columnIndex) {
			return new CellOut<>() {
				@Override
				public void writeHeader(HeaderRowOut headerRowOut) {

				}

				@Override
				public void setValue(RowOut rowOut, DidoData data) {
					Cell cell = rowOut.getCell(columnIndex, CellType.STRING);
					String value = data.getStringAt(1);
					cell.setCellStyle(rowOut.styleFor(value));
					cell.setCellValue(value);
				}
			};
		}

		@Override
		public Class<?> getType() {
			return String.class;
		}

		@Override
		public String getName() {
			return null;
		}
	}

	@Test
	public void testColoursAndFills() throws Exception {
		
		PoiWorkbook workbook = new PoiWorkbook();
		workbook.setOutput(new FileOutputStream(
				new File(workDir, "AllStylesFills.xlsx")));
		
		TextCell text = new TextCell();

		DataRows rows = new DataRows();
		rows.setOf(0, text);
		
		StyleBean sb1 = new StyleBean();
		sb1.setColour(IndexedColors.RED);
		sb1.setFillBackgroundColour(IndexedColors.BLUE);		
		sb1.setFillPattern(FillPatternType.NO_FILL);
		rows.setStyles("red-on-blue-no-fill", sb1);
		
		StyleBean sb2 = new StyleBean();
		sb2.setColour(IndexedColors.RED);
		sb2.setFillBackgroundColour(IndexedColors.WHITE);		
		sb2.setFillForegroundColour(IndexedColors.GREEN);		
		sb2.setFillPattern(FillPatternType.DIAMONDS);
		rows.setStyles("green-diamonds", sb2);
						
		StyleBean sb3 = new StyleBean();
		sb3.setColour(IndexedColors.RED);
		sb3.setFillBackgroundColour(IndexedColors.BLUE);		
		sb3.setFillForegroundColour(IndexedColors.GREEN);		
		sb3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		rows.setStyles("solid-green-foreground", sb3);

		StyleBean sb4 = new StyleBean();
		sb4.setColour(IndexedColors.RED);
		sb4.setFillForegroundColour(IndexedColors.GREEN);		
		sb4.setFillPattern(FillPatternType.BRICKS);
		rows.setStyles("green-bricks", sb4);

		StyleBean sb5 = new StyleBean();
		sb5.setColour(IndexedColors.RED);
		sb5.setFillForegroundColour(IndexedColors.GREEN);
		sb5.setFillBackgroundColour(IndexedColors.BLUE);
		sb5.setFillPattern(FillPatternType.SPARSE_DOTS);
		rows.setStyles("blue-green-spots", sb5);
		
		StyleBean sb6 = new StyleBean();
		sb6.setColour(IndexedColors.RED);
		sb6.setFillForegroundColour(IndexedColors.GREEN);
		sb6.setFillBackgroundColour(IndexedColors.BLUE);
		sb6.setFillPattern(FillPatternType.THIN_FORWARD_DIAG);
		rows.setStyles("blue-green-diags", sb6);

		DataOut writer = rows.outTo(workbook);
		
		writer.accept(ArrayData.of("red-on-blue-no-fill"));
		writer.accept(ArrayData.of("green-diamonds"));
		writer.accept(ArrayData.of("solid-green-foreground"));
		writer.accept(ArrayData.of("green-bricks"));
		writer.accept(ArrayData.of("blue-green-spots"));
		writer.accept(ArrayData.of("blue-green-diags"));
		
		writer.close();
		
	}

	@Test
	public void testAllColourAndBackgroundStyles() throws Exception {
		
		PoiWorkbook workbook = new PoiWorkbook();
		workbook.setOutput(new FileOutputStream(
				new File(workDir, "AllStylesColours.xlsx")));
		
		TextCell text = new TextCell();

		DataRows rows = new DataRows();
		rows.setOf(0, text);

		for (IndexedColors colour : IndexedColors.values()) {
			
			StyleBean sb = new StyleBean();
			sb.setFillForegroundColour(colour);		
			sb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			rows.setStyles(colour.toString(),sb);
		}
		
		DataOut writer = rows.outTo(workbook);
		
		for (IndexedColors colour : IndexedColors.values()) {

			writer.accept(ArrayData.of(colour.toString()));
		}
		
		writer.close();		
	}
}

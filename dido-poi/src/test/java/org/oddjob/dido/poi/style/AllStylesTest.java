package org.oddjob.dido.poi.style;

import junit.framework.TestCase;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.oddjob.OurDirs;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.dido.*;
import org.oddjob.dido.bio.SingleBeanBinding;
import org.oddjob.dido.poi.data.PoiWorkbook;
import org.oddjob.dido.poi.layouts.DataBook;
import org.oddjob.dido.poi.layouts.DataRows;
import org.oddjob.dido.poi.layouts.TextCell;

import java.io.File;

public class AllStylesTest extends TestCase {

	File workDir;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		workDir = OurDirs.workPathDir(AllStylesTest.class).toFile();
	}
	
	class OurBinding extends SingleBeanBinding {
		
		@Override
		public void free() {
			throw new RuntimeException("Unexpected.");
		}
		
		@Override
		protected void inject(Object object, Layout boundLayout, DataOut dataOut)
				throws DataException {
			TextCell text = (TextCell) boundLayout;
			String style = (String) object;
			text.setStyle(style);
			text.value(style);
		}
		
		@Override
		protected Object extract(Layout boundLayout, DataIn dataIn)
				throws DataException {
			throw new RuntimeException("Unexpected.");
		}
	}	
	
	public void testColoursAndFills() throws DataException {
		
		ArooaSession session = new StandardArooaSession();
		
		PoiWorkbook workbook = new PoiWorkbook();
		workbook.setArooaSession(session);
		workbook.setOutput(new ArooaObject(
				new File(workDir, "AllStylesFills.xlsx")));
		
		TextCell text = new TextCell();
		text.setArooaSession(session);
		
		DataRows rows = new DataRows();
		rows.setOf(0, text);
		
		DataBook book = new DataBook();
		book.setOf(0, rows);
		
		text.setBinding(new OurBinding());
		
		StyleBean sb1 = new StyleBean();
		sb1.setColour(IndexedColors.RED);
		sb1.setFillBackgroundColour(IndexedColors.BLUE);		
		sb1.setFillPattern(FillPatternType.NO_FILL);
		book.setStyles("red-on-blue-no-fill", sb1);
		
		StyleBean sb2 = new StyleBean();
		sb2.setColour(IndexedColors.RED);
		sb2.setFillBackgroundColour(IndexedColors.WHITE);		
		sb2.setFillForegroundColour(IndexedColors.GREEN);		
		sb2.setFillPattern(FillPatternType.DIAMONDS);
		book.setStyles("green-diamonds", sb2);
						
		StyleBean sb3 = new StyleBean();
		sb3.setColour(IndexedColors.RED);
		sb3.setFillBackgroundColour(IndexedColors.BLUE);		
		sb3.setFillForegroundColour(IndexedColors.GREEN);		
		sb3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		book.setStyles("solid-green-foreground", sb3);

		StyleBean sb4 = new StyleBean();
		sb4.setColour(IndexedColors.RED);
		sb4.setFillForegroundColour(IndexedColors.GREEN);		
		sb4.setFillPattern(FillPatternType.BRICKS);
		book.setStyles("green-bricks", sb4);

		StyleBean sb5 = new StyleBean();
		sb5.setColour(IndexedColors.RED);
		sb5.setFillForegroundColour(IndexedColors.GREEN);
		sb5.setFillBackgroundColour(IndexedColors.BLUE);
		sb5.setFillPattern(FillPatternType.SPARSE_DOTS);
		book.setStyles("blue-green-spots", sb5);
		
		StyleBean sb6 = new StyleBean();
		sb6.setColour(IndexedColors.RED);
		sb6.setFillForegroundColour(IndexedColors.GREEN);
		sb6.setFillBackgroundColour(IndexedColors.BLUE);
		sb6.setFillPattern(FillPatternType.THIN_FORWARD_DIAG);
		book.setStyles("blue-green-diags", sb6);
		
		DataWriter writer = book.writerFor(workbook);
		
		writer.write("red-on-blue-no-fill");
		writer.write("green-diamonds");
		writer.write("solid-green-foreground");
		writer.write("green-bricks");
		writer.write("blue-green-spots");
		writer.write("blue-green-diags");
		
		writer.close();
		
	}
	
	
	public void testAllColourAndBackgroundStyles() throws DataException {
		
		ArooaSession session = new StandardArooaSession();
		
		PoiWorkbook workbook = new PoiWorkbook();
		workbook.setArooaSession(session);
		workbook.setOutput(new ArooaObject(
				new File(workDir, "AllStylesColours.xlsx")));
		
		TextCell text = new TextCell();
		text.setArooaSession(session);
		
		DataRows rows = new DataRows();
		rows.setOf(0, text);
		
		DataBook book = new DataBook();
		book.setOf(0, rows);
		
		text.setBinding(new OurBinding());
		
		for (IndexedColors colour : IndexedColors.values()) {
			
			StyleBean sb = new StyleBean();
			sb.setFillForegroundColour(colour);		
			sb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			book.setStyles(colour.toString(),sb);
		}
		
		DataWriter writer = book.writerFor(workbook);
		
		for (IndexedColors colour : IndexedColors.values()) {

			writer.write(colour.toString());
		}
		
		writer.close();		
	}
}

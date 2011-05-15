package org.oddjob.poi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.oddjob.dido.DataException;
import org.oddjob.dido.MockDataNode;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;
import org.oddjob.dido.text.TextIn;
import org.oddjob.dido.text.TextOut;

public class DataSheetTest extends TestCase {

	class OurSheetStuff 
	extends MockDataNode<SheetIn, TextIn, SheetOut, TextOut> {
		
	}
	
	public void testWriteAndRead() throws DataException, InvalidFormatException, IOException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		BookOut bookOut = new PoiBookOut(output);
		
		DataSheet test = new DataSheet();
		
		OurSheetStuff child = new OurSheetStuff();
		test.setIs(0, child);
		
		WhereNextOut<SheetOut> nextOut = test.out(bookOut);
					
		assertEquals(child, nextOut.getChildren()[0]);
		
		SheetOut sheetOut = nextOut.getChildData();
		assertEquals(-1, sheetOut.getCurrentRow());

		bookOut.flush();

		BookIn bookIn = new PoiBookIn(new ByteArrayInputStream(
				output.toByteArray()));
		
		WhereNextIn<SheetIn> nextIn = test.in(bookIn);
		
		assertEquals(child, nextIn.getChildren()[0]);
		
		SheetIn sheetIn = nextIn.getChildData();
		assertEquals(-1, sheetIn.getCurrentRow());		
		assertTrue(sheetIn.nextRow());
		
	}
}

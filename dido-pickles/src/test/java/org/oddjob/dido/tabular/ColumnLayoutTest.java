package org.oddjob.dido.tabular;

import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.text.SimpleTextFieldsIn;
import org.oddjob.dido.text.SimpleTextFieldsOut;

public class ColumnLayoutTest extends TestCase {

	private static final Logger logger = Logger.getLogger(ColumnLayoutTest.class);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		logger.info("------------------------------  " + getName() + 
				"  -----------------------------");
	}
	
	public void testReadFromTextFieldNoChildrenNoHeadings() throws DataException {
		
		SimpleTextFieldsIn dataIn = new SimpleTextFieldsIn();
		dataIn.setValues(new String[] { "Apples" });
		
		ColumnLayout<String> test = new ColumnLayout<String>();
		
		assertEquals(0, test.getIndex());
		
		test.setBinding(new DirectBinding());
		
		DataReader reader = test.readerFor(dataIn);
		
		assertEquals("Apples", reader.read());
		assertEquals(null, reader.read());
		
		reader.close();
		
		assertEquals(1, test.getIndex());
	}
	
	public void testWriteToTextFieldNoChildrenNoHeadings() throws DataException {
		
		SimpleTextFieldsOut dataOut = new SimpleTextFieldsOut();
		
		ColumnLayout<String> test = new ColumnLayout<String>();
		
		assertEquals(0, test.getIndex());
		
		test.setBinding(new DirectBinding());
		
		DataWriter writer = test.writerFor(dataOut);
		
		assertEquals(false, writer.write("Apples"));
		
		writer.close();
		
		assertEquals("[Apples]", Arrays.toString(dataOut.values()));
		assertEquals(1, test.getIndex());
	}
}

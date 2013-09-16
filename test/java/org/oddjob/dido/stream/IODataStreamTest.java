package org.oddjob.dido.stream;

import java.io.IOException;

import org.oddjob.OddjobDescriptorFactory;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.io.BufferType;

import junit.framework.TestCase;

public class IODataStreamTest extends TestCase {

	public static final String EOL = System.getProperty("line.separator");
	
	public void testOutThrowsExceptionWithNoOutput() throws DataException {
		
		IOStreamData test = new IOStreamData();
		
		try {
			test.provideDataOut(LinesOut.class);
			fail("Should throw NPE.");
		}
		catch (NullPointerException e) {
			// expected.
		}
	}
	
	public void testInThrowsExceptionWithNoInput() throws DataException {
		
		IOStreamData test = new IOStreamData();
		
		try {
			test.provideDataIn(LinesIn.class);
			fail("Should throw NPE.");
		}
		catch (NullPointerException e) {
			// expected.
		}
	}
	
	public void testWriteAsLinesOut() throws DataException {
		
		BufferType buffer = new BufferType();
		buffer.configured();
		
		IOStreamData test = new IOStreamData();
		test.setArooaSession(new StandardArooaSession(
				new OddjobDescriptorFactory().createDescriptor(
						getClass().getClassLoader())));
		test.setOutput(buffer);
		
		LinesOut out = test.provideDataOut(LinesOut.class);
		
		out.writeLine("Apples");
		out.writeLine("Oranges");
		out.writeLine("Pears");
		
		out.close();
		
		assertEquals("Apples" + EOL + "Oranges" + EOL + "Pears" + EOL,
				buffer.getText());
	}
	
	public void testReadLinesIn() throws DataException, IOException {
		
		BufferType buffer = new BufferType();
		buffer.setText("Apples" + EOL + "Oranges" + EOL + "Pears" + EOL);
		buffer.configured();
		
		IOStreamData test = new IOStreamData();
		test.setArooaSession(new StandardArooaSession(
				new OddjobDescriptorFactory().createDescriptor(
						getClass().getClassLoader())));
		test.setInput(buffer);
		
		LinesIn in = test.provideDataIn(LinesIn.class);
		
		assertEquals("Apples", in.readLine());
		assertEquals("Oranges", in.readLine());
		assertEquals("Pears", in.readLine());
		
		in.close();
	}
}

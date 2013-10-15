package org.oddjob.dido.other;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.text.TextFieldsIn;
import org.oddjob.dido.text.TextFieldsOut;
import org.oddjob.dido.text.TextLayout2;

public class CaseTest extends TestCase {

	public void testRead() throws DataException {
		
		Case<String> test = 
			new Case<String>();

		TextLayout2 text = new TextLayout2();
		text.setName("delimiter");
		text.setIndex(1);
		text.setLength(1);
		
		When when1 = new When(); 
		when1.setValue("1");
		
		TextLayout2 text1 = new TextLayout2();
		text1.setName("person");
		text1.setIndex(2);
		text1.setLength(10);
		
		when1.setOf(0, text1);
		
		When when2 = new When(); 
		when2.setValue("2");
		
		TextLayout2 text2 = new TextLayout2();
		text1.setName("fruit");
		text2.setIndex(2);
		text2.setLength(10);
		
		
		when2.setOf(0, text2);
		
		test.setOf(0, text);
		test.setOf(1, when1);
		test.setOf(2, when2);
		
		TextFieldsIn textIn = new TextFieldsIn();
		textIn.setText("1John");
		
		DataReader reader = test.readerFor(textIn);
		
		reader.read();
		
		assertEquals("John", text1.getValue());
		
		test.reset();

		textIn.setText("2Apple");
		
		reader = test.readerFor(textIn);
		
		reader.read();
		
		assertEquals("Apple", text2.getValue());
	}
	
	public void testWrite() throws DataException {
		
		Case<String> test = 
			new Case<String>();

		TextLayout2 text = new TextLayout2();
		text.setIndex(1);
		text.setLength(1);
		
		When when1 = new When(); 
		when1.setValue("1");
		
		final TextLayout2 text1 = new TextLayout2();
		text1.setIndex(2);
		text1.setLength(10);
		
		when1.setOf(0, text1);
		
		When when2 = new When(); 
		when2.setValue("2");
		
		TextLayout2 text2 = new TextLayout2();
		text2.setIndex(2);
		text2.setLength(10);
				
		when2.setOf(0, text2);
		
		test.setOf(0, text);
		test.setOf(1, when1);
		test.setOf(2, when2);
		
		TextFieldsOut textOut = new TextFieldsOut();
		
		text1.bind(new DirectBinding());
		
		DataWriter writer = test.writerFor(textOut);
		
		writer.write("John");
		
		assertEquals("1John      ", textOut.getText());
		
	}
}

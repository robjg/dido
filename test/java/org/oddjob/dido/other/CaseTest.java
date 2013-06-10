package org.oddjob.dido.other;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.text.StringTextIn;
import org.oddjob.dido.text.StringTextOut;
import org.oddjob.dido.text.TextLayout;

public class CaseTest extends TestCase {

	public void testRead() throws DataException {
		
		Case<String> test = 
			new Case<String>();

		TextLayout text = new TextLayout();
		text.setFrom(0);
		text.setLength(1);
		
		When when1 = new When(); 
		when1.setValue("1");
		
		TextLayout text1 = new TextLayout();
		text1.setFrom(1);
		text1.setLength(10);
		
		when1.setOf(0, text1);
		
		When when2 = new When(); 
		when2.setValue("2");
		
		TextLayout text2 = new TextLayout();
		text2.setFrom(1);
		text2.setLength(10);
		
		
		when2.setOf(0, text2);
		
		test.setOf(0, text);
		test.setOf(1, when1);
		test.setOf(2, when2);
		
		StringTextIn textIn = new StringTextIn("1John");
		
		DataReader reader = test.readerFor(textIn);
		
		reader.read();
		
		assertEquals("John", text1.getValue());
		
		textIn = new StringTextIn("2Apple");
		
		reader = test.readerFor(textIn);
		
		reader.read();
		
		assertEquals("Apple", text2.getValue());
	}
	
	public void testWrite() throws DataException {
		
		Case<String> test = 
			new Case<String>();

		TextLayout text = new TextLayout();
		text.setFrom(0);
		text.setLength(1);
		
		When when1 = new When(); 
		when1.setValue("1");
		
		final TextLayout text1 = new TextLayout();
		text1.setFrom(1);
		text1.setLength(10);
		
		when1.setOf(0, text1);
		
		When when2 = new When(); 
		when2.setValue("2");
		
		TextLayout text2 = new TextLayout();
		text2.setFrom(1);
		text2.setLength(10);
		
		
		when2.setOf(0, text2);
		
		test.setOf(0, text);
		test.setOf(1, when1);
		test.setOf(2, when2);
		
		StringTextOut textOut = new StringTextOut();
		
		text1.bind(new DirectBinding());
		
		DataWriter writer = test.writerFor(textOut);

		
		writer.write("John");
		
		assertEquals("1John      ", textOut.toValue(String.class));
		
	}
}

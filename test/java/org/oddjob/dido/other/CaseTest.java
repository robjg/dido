package org.oddjob.dido.other;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.io.DataLinkOut;
import org.oddjob.dido.io.DataReaderImpl;
import org.oddjob.dido.io.DataWriterImpl;
import org.oddjob.dido.io.LinkOutEvent;
import org.oddjob.dido.other.Case;
import org.oddjob.dido.other.When;
import org.oddjob.dido.text.StringTextIn;
import org.oddjob.dido.text.StringTextOut;
import org.oddjob.dido.text.Text;
import org.oddjob.dido.text.TextIn;
import org.oddjob.dido.text.TextOut;

public class CaseTest extends TestCase {

	public void testRead() throws DataException {
		
		Case<String, TextIn, TextOut> test = 
			new Case<String, TextIn, TextOut>();

		Text text = new Text();
		text.setFrom(0);
		text.setLength(1);
		
		When<TextIn, TextOut> when1 = new When<TextIn, TextOut>(); 
		when1.setValue("1");
		
		Text text1 = new Text();
		text1.setFrom(1);
		text1.setLength(10);
		
		when1.setIs(0, text1);
		
		When<TextIn, TextOut> when2 = new When<TextIn, TextOut>(); 
		when2.setValue("2");
		
		Text text2 = new Text();
		text2.setFrom(1);
		text2.setLength(10);
		
		
		when2.setIs(0, text2);
		
		test.setIs(0, text);
		test.setIs(1, when1);
		test.setIs(2, when2);
		
		StringTextIn textIn = new StringTextIn("1John");
		
		DataReaderImpl<TextIn> reader = new DataReaderImpl<TextIn>(test, textIn);
		
		reader.read();
		
		assertEquals("John", text1.getValue());
		
		textIn = new StringTextIn("2Apple");
		
		reader = new DataReaderImpl<TextIn>(test, textIn);
		
		reader.read();
		
		assertEquals("Apple", text2.getValue());
	}
	
	public void testWrite() throws DataException {
		
		Case<String, TextIn, TextOut> test = 
			new Case<String, TextIn, TextOut>();

		Text text = new Text();
		text.setFrom(0);
		text.setLength(1);
		
		When<TextIn, TextOut> when1 = new When<TextIn, TextOut>(); 
		when1.setValue("1");
		
		final Text text1 = new Text();
		text1.setFrom(1);
		text1.setLength(10);
		
		when1.setIs(0, text1);
		
		When<TextIn, TextOut> when2 = new When<TextIn, TextOut>(); 
		when2.setValue("2");
		
		Text text2 = new Text();
		text2.setFrom(1);
		text2.setLength(10);
		
		
		when2.setIs(0, text2);
		
		test.setIs(0, text);
		test.setIs(1, when1);
		test.setIs(2, when2);
		
		StringTextOut textOut = new StringTextOut() {
			@Override
			public boolean flush() throws DataException {
				return false;
			}
		};
		
		DataWriterImpl<TextOut> writer = new DataWriterImpl<TextOut>(test, textOut);

		writer.setLinkOut(when1, new DataLinkOut() {
			
			@Override
			public void lastOut(LinkOutEvent event) {
			}
			
			@Override
			public boolean dataOut(LinkOutEvent event, Object bean) {
				text1.setValue("John");
				return true;
			}
		});
		
		writer.write(new Object());
		writer.complete();
		
		assertEquals("1John      ", textOut.toString());
		
	}
}

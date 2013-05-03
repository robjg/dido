package org.oddjob.dido.io;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.DataPlan;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.stream.InputStreamIn;
import org.oddjob.dido.stream.Lines;
import org.oddjob.dido.stream.LinesIn;
import org.oddjob.dido.stream.LinesOut;
import org.oddjob.dido.text.Delimited;
import org.oddjob.dido.text.Field;
import org.oddjob.dido.text.TextIn;
import org.oddjob.dido.text.TextOut;


public class DataReaderImplTest extends TestCase {

	StringBuffer result = new StringBuffer();
	
	String EOL = System.getProperty("line.separator");
	
	class PrettyPrint implements DIDOListener {
		
		int level = 0;
		
		@Override
		public void startNode(DIDOEvent event) {
			DataNode<?, ?, ?, ?> node = event.getNode();
			result.append(pad());
			String name = node.getName();
			if (name == null) {
				result.append(node.getClass().getSimpleName());
			}
			else {
				result.append(name);
			}
			if (node instanceof ValueNode<?>) {
				Object value = ((ValueNode<?>) node).value();
				if (value != null) {
					result.append('=');
					result.append(value.toString());
				}
			}
			result.append(EOL);
			++level;
		}
		
		@Override
		public void endNode(DIDOEvent data) {
			--level;
		}
		
		String pad() {
			char[] pad = new char[level];
			Arrays.fill(pad, ' ');
			return new String(pad);
		}
	}
	
	
	public void testReadingSeveralDelimitedLines() throws DataException {
	
		String text = 
			"john,23,apples" + EOL +
			"jane,32,oranges" + EOL;
		
		Lines lines = new Lines();
		
		Delimited delimited = new Delimited();
		
		Field name = new Field();
		name.setName("Name");
		
		Field age = new Field();
		age.setName("Age");
		
		Field fruit = new Field();
		fruit.setName("Fruit");
		
		delimited.setIs(0, name);
		delimited.setIs(1, age);
		delimited.setIs(2, fruit);
		
		lines.setIs(0, delimited);
		
		InputStreamIn dataIn = 
			new InputStreamIn(new ByteArrayInputStream(text.getBytes()));
		
		DataReaderImpl<LinesIn> reader = 
			new DataReaderImpl<LinesIn>(
					new DataPlan<LinesIn, TextIn, LinesOut, TextOut>(
							new StandardArooaSession(), lines),
					dataIn);
		
		PrettyPrint printer = new PrettyPrint();
		
		reader.addListener(printer);
		
		assertNull(reader.read());
		
		String expected =
			"Lines" + EOL +
			" Delimited" + EOL +
			"  Name=john" + EOL +
			"  Age=23" + EOL +
			"  Fruit=apples" + EOL +
			"Lines" + EOL +
			" Delimited" + EOL +
			"  Name=jane" + EOL +
			"  Age=32" + EOL +
			"  Fruit=oranges" + EOL;
		
		assertEquals(expected, result.toString());
		
		reader.removeListener(printer);
	}
	
}

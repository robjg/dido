package org.oddjob.dido.io;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.DataDriver;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.DataPlan;
import org.oddjob.dido.MockBoundedDataNode;
import org.oddjob.dido.MockDataNode;
import org.oddjob.dido.Stencil;
import org.oddjob.dido.SupportsChildren;
import org.oddjob.dido.WhereNext;
import org.oddjob.dido.WhereNextOut;
import org.oddjob.dido.stream.Lines;
import org.oddjob.dido.stream.LinesIn;
import org.oddjob.dido.stream.LinesOut;
import org.oddjob.dido.stream.OutputStreamOut;
import org.oddjob.dido.text.Delimited;
import org.oddjob.dido.text.Field;
import org.oddjob.dido.text.StringTextOut;
import org.oddjob.dido.text.TextIn;
import org.oddjob.dido.text.TextOut;


public class DataWriterTest extends TestCase {

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
			if (node instanceof Stencil<?>) {
				Object value = ((Stencil<?>) node).value();
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
		
	public void testWritingSeveralDelimitedLines() throws DataException {
	
		Lines lines = new Lines();
		
		Delimited delimited = new Delimited();
		
		final Field name = new Field();
		name.setName("Name");
		
		final Field age = new Field();
		age.setName("Age");
		
		final Field fruit = new Field();
		fruit.setName("Fruit");
		
		delimited.setIs(0, name);
		delimited.setIs(1, age);
		delimited.setIs(2, fruit);
		
		lines.setIs(0, delimited);
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		OutputStreamOut dataOut = 
			new OutputStreamOut(output);
		
		DataWriter<LinesOut> writer = 
			new DataWriter<LinesOut>(
					new DataPlan<LinesIn, TextIn, LinesOut, TextOut>(
							new StandardArooaSession(), lines), 
					dataOut);
		
		PrettyPrint printer = new PrettyPrint();
		
		writer.addListener(printer);
		writer.setLinkOut(delimited, new DataLinkOut() {
			@Override
			public boolean dataOut(LinkOutEvent event, Object bean) {
				String[] values = (String[]) bean;
				name.setValue(values[0]);
				age.setValue(values[1]);
				fruit.setValue(values[2]);
				return true;
			}
			@Override
			public void lastOut(LinkOutEvent event) {
				
			}
		});
		
		writer.write(new String[] { "John", "23", "apples" });
		writer.write(new String[] { "Jane", "32", "oranges" });
		writer.complete();
		
		String expected =
			"Lines" + EOL +
			" Delimited" + EOL +
			"  Name=John" + EOL +
			"  Age=23" + EOL +
			"  Fruit=apples" + EOL +
			"Lines" + EOL +
			" Delimited" + EOL +
			"  Name=Jane" + EOL +
			"  Age=32" + EOL +
			"  Fruit=oranges" + EOL;
		
		assertEquals(expected, result.toString());
		
		writer.removeListener(printer);
		
		String text = 
			"John,23,apples" + EOL +
			"Jane,32,oranges" + EOL;
		
		assertEquals(text, new String(output.toByteArray()));
	}

	List<String> results = new ArrayList<String>();
	
	class Root extends MockDataNode<TextIn, TextIn, TextOut, TextOut> {

		Middle child = new Middle();
		
		@Override
		public WhereNext<DataNode<?, ?, TextOut, ?>, TextOut> out(
				TextOut outgoing) throws DataException {
			
			results.add("Root out.");

			TextOut out = new StringTextOut() {
				@Override
				public boolean flush() throws DataException {
					return false;
				}
			};
			
			child.begin(out);
			
			Middle[] children = { child };
			
			return new WhereNextOut<TextOut>(children, out);
		}
		
		@Override
		public void flush(TextOut data, TextOut childData) throws DataException {
			results.add("Root flushed.");
		}
		
		@Override
		public void complete(TextOut textOut) throws DataException {
			results.add("Root complete.");
			child.end((TextOut) null);
		}
	}
	
	class Middle extends MockBoundedDataNode<TextIn, TextIn, TextOut, TextOut> 
	implements DataDriver, SupportsChildren {
		
		Leaf[] children = { new Leaf("One"), new Leaf("Two") };
		
		int count;
		
		@Override
		public DataNode<?, ?, ?, ?>[] childrenToArray() {
			return children;
		}
		
		@Override
		public void begin(TextOut out) {
			results.add("Middle begin.");
			for (Leaf child : children) {
				child.begin(out);
			}
		}
		
		@Override
		public void end(TextOut out) {
			for (Leaf child : children) {
				child.end(out);
			}
			results.add("Middle end.");
		}
		
		@Override
		public WhereNext<DataNode<?, ?, TextOut, ?>, TextOut> out(
				TextOut outgoing) throws DataException {
			
			results.add("Middle out.");
			
			return new WhereNextOut<TextOut>(children, 
					new StringTextOut() {
				@Override
				public boolean flush() throws DataException {
					return false;
				}
			});
		}
		
		@Override
		public void flush(TextOut data, TextOut childData) throws DataException {
			results.add("Middle flushed.");
		}
		
		@Override
		public void complete(TextOut out) throws DataException {
			results.add("Middle complete.");
		}
	}
	
	class Leaf extends MockBoundedDataNode<TextIn, TextIn, TextOut, TextOut> {
		
		String name;
		
		Leaf(String name) {
			this.name = name;
		}
		
		@Override
		public void begin(TextOut out) {
			results.add("Leaf " + name + " begin.");
		}
		
		@Override
		public void end(TextOut out) {
			results.add("Leaf " + name + " end.");
		}
		
		@Override
		public WhereNext<DataNode<?, ?, TextOut, ?>, TextOut> out(
				TextOut outgoing) throws DataException {
			results.add("Leaf " + name + " out.");
			
			return new WhereNextOut<TextOut>();
		}
		
		@Override
		public void complete(TextOut out) throws DataException {
			results.add("Leaf " + name + " complete.");
		}
	}
	
	public void testWritingBoundedNodes() throws DataException {
		
		Root root = new Root();
		
		TextOut dataOut = 
			new StringTextOut() {
			@Override
			public boolean flush() throws DataException {
				return false;
			}
		};
		
		DataWriter<TextOut> writer = 
			new DataWriter<TextOut>(
					new DataPlan<TextIn, TextIn, TextOut, TextOut>(
							new StandardArooaSession(), root), 
					dataOut);
		writer.setLinkOut(root.child, new DataLinkOut() {
			@Override
			public boolean dataOut(LinkOutEvent event, Object bean) {
				return true;
			}
			@Override
			public void lastOut(LinkOutEvent event) {
			}
		});
				
		writer.write(new Object());
		writer.write(new Object());
		writer.complete();

//		for (String s : results) {
//			System.out.println(s);
//		}
		
		assertEquals("Root out.", results.get(0));
		assertEquals("Middle begin.", results.get(1));
		assertEquals("Leaf One begin.", results.get(2));
		assertEquals("Leaf Two begin.", results.get(3));
		assertEquals("Middle out.", results.get(4));
		assertEquals("Leaf One out.", results.get(5));
		assertEquals("Leaf Two out.", results.get(6));
		assertEquals("Middle flushed.", results.get(7));
		assertEquals("Middle out.", results.get(8));
		assertEquals("Leaf One out.", results.get(9));
		assertEquals("Leaf Two out.", results.get(10));
		assertEquals("Middle flushed.", results.get(11));
		assertEquals("Leaf One complete.", results.get(12));
		assertEquals("Leaf Two complete.", results.get(13));
		assertEquals("Middle complete.", results.get(14));
		assertEquals("Root flushed.", results.get(15));
		assertEquals("Root complete.", results.get(16));
		assertEquals("Leaf One end.", results.get(17));
		assertEquals("Leaf Two end.", results.get(18));
		assertEquals("Middle end.", results.get(19));
		
		assertEquals(20, results.size());
	}
}

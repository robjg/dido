package org.oddjob.dido.bio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.layout.ChildReader;
import org.oddjob.dido.layout.ChildWriter;
import org.oddjob.dido.stream.InputStreamIn;
import org.oddjob.dido.stream.LinesLayout;
import org.oddjob.dido.stream.OutputStreamOut;
import org.oddjob.dido.text.DelimitedLayout;
import org.oddjob.dido.text.FieldLayout;

/**
 * Test the binding theory for situations where there is more than one
 * bean for a line and more than one line for a bean.
 * <p>
 * Reading works OK - but writing still needs some work!
 * 
 * @author rob
 *
 */
public class BeanBindingTheoryTest extends TestCase {

	private static class Fruit {
		
		private String colour;
		
		private String type;
	}
	
	private class TwoLinesPerObjectBinding implements Binding {
		
		Fruit fruit;
		
		@Override
		public DataReader readerFor(final Layout boundLayout, DataIn dataIn)
				throws DataException {
			
			return new DataReader() {
				
				@Override
				public Object read() throws DataException {
					
					@SuppressWarnings("unchecked")
					ValueNode<String> vn = (ValueNode<String>)boundLayout;
					
					if (fruit == null) {
						fruit = new Fruit();
						fruit.colour = vn.value();
						return null;
					}
					if (fruit.type == null) {
						fruit.type = vn.value();
						return fruit;
					}	
					else {
						fruit = null;
						return null;
					}
				}
				
				@Override
				public void close() throws DataException {
					// TODO Auto-generated method stub
					
				}
			};
		}
		
		@Override
		public DataWriter writerFor(final Layout boundLayout, DataOut dataOut)
				throws DataException {
		
			return new DataWriter() {

				int step = 1;
				
				@Override
				public boolean write(Object object) throws DataException {
					
					@SuppressWarnings("unchecked")
					ValueNode<String> vn = (ValueNode<String>)boundLayout;					
					
					Fruit fruit = (Fruit) object;
					
					if (step == 1) {
						vn.value(fruit.colour);
						step = 2;
					}
					else if (step == 2) {
						vn.value(fruit.type);
						step = 3;
					}
					else {
						// do nothing. Writer will be closed.
					}

					return false;
				}
				
				@Override
				public void close() throws DataException {
				}
			};
		}
		
		
		@Override
		public void free() {
		}
	}
	
	static final String EOL = System.getProperty("line.separator");
	
	public void testReadAndWriteTwoLinesForOneObject() throws DataException {
		
		String text = 
				"Green" + EOL +
				"Apple" + EOL + 
				"Yellow" + EOL +
				"Banana" + EOL;
		
		LinesLayout lines = new LinesLayout();
		lines.bind(new TwoLinesPerObjectBinding());
		
		DataReader reader = lines.readerFor(
				new InputStreamIn(new ByteArrayInputStream(text.getBytes())));
		
		Fruit fruit1 = (Fruit) reader.read();
		
		assertEquals("Green", fruit1.colour);
		assertEquals("Apple", fruit1.type);
		
		Fruit fruit2 = (Fruit) reader.read();
		
		assertEquals("Yellow", fruit2.colour);
		assertEquals("Banana", fruit2.type);
		
		reader.close();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		DataWriter writer = lines.writerFor(new OutputStreamOut(output));
		
		writer.write(fruit1);
		writer.write(fruit2);
		
		writer.close();
		
		String expected = 
				"Green" + EOL +
				"Apple" + EOL +
				"Yellow" + EOL +
				"Banana" + EOL;
		
		assertEquals(expected, output.toString());
	}
	
	
	private class ChildBinding implements Binding {

		boolean take;
		
		@Override
		public DataReader readerFor(Layout boundLayout, DataIn dataIn)
				throws DataException {
			throw new RuntimeException("Unexpected.");
		}
		
		@Override
		public DataWriter writerFor(final Layout boundLayout, DataOut dataOut)
				throws DataException {
			
			return new DataWriter() {
				
				@SuppressWarnings("unchecked")
				@Override
				public boolean write(Object object) throws DataException {
					
					if (take) {
						
						((ValueNode<String>) boundLayout).value((
								String) object);
						
						take = false;
					}
					
					return false;
				}
				
				@Override
				public void close() throws DataException {
					// TODO Auto-generated method stub
					
				}
			};
		}
		
		@Override
		public void free() {
		}
	}
	
	private class TwoObjectsPerLineBinding implements Binding {
		
		@Override
		public DataReader readerFor(final Layout boundLayout, 
				final DataIn dataIn)
		throws DataException {
			
			return new DataReader() {
				
				int step = 1;

				@Override
				public Object read() throws DataException {
					if (step == 1) {
						
						new ChildReader(boundLayout.childLayouts(), 
								dataIn).read();
						
						step = 2;
						
						@SuppressWarnings("unchecked")
						ValueNode<String> vn = 
								(ValueNode<String> ) boundLayout.childLayouts().get(0);
						
						return vn.value();
					}
					else if (step == 2) {
						step = 3; 
						
						@SuppressWarnings("unchecked")
						ValueNode<String> vn = 
								(ValueNode<String> ) boundLayout.childLayouts().get(1);
						
						return vn.value();
					}
					else {
						return null;
					}
				}
				
				@Override
				public void close() throws DataException {
				}
			};
		}
		
		@Override
		public DataWriter writerFor(final Layout boundLayout, 
				final DataOut dataOut)
		throws DataException {
			
			final ChildBinding binding1 = new ChildBinding();
			final ChildBinding binding2 = new ChildBinding();
			
			List<Layout> childLayouts = boundLayout.childLayouts();
			
			Layout child1 = childLayouts.get(0);
			child1.bind(binding1);
			
			Layout child2 = childLayouts.get(1);
			child2.bind(binding2);
			
			return new DataWriter() {
				
				int step = 1;
				
				
				
				@Override
				public boolean write(Object object) throws DataException {
					
					DataWriter nextWriter = new ChildWriter(boundLayout.childLayouts(), 
								(ValueNode<?>) boundLayout, dataOut);

					if (step == 1) {
						
						binding1.take = true;
						
						step = 2;
						
						assertFalse(nextWriter.write(object));
						
						return true;
					}
					else if (step == 2) {
						
						binding2.take = true;
						
						step = 3;
						
						assertFalse(nextWriter.write(object));
						
						return false;
					}
					else {
						return false;
					}
					
				}
				
				@Override
				public void close() throws DataException {
				}
			};
		}
		
		@Override
		public void free() {
		}
	}
	
	public void testReadAndWriteTwoObjectPerLine() throws DataException {
		
		String text = 
				"Green,Apple" + EOL + 
				"Yellow,Banana" + EOL;
		
		DelimitedLayout layout = new DelimitedLayout();
		
		FieldLayout field1 = new FieldLayout();
		layout.setOf(0,  field1);
		
		FieldLayout field2 = new FieldLayout();
		layout.setOf(1,  field2);
		
		layout.bind(new TwoObjectsPerLineBinding());
		
		DataReader reader = layout.readerFor(
				new InputStreamIn(new ByteArrayInputStream(text.getBytes())));
		
		String colour1 = (String) reader.read();
		
		assertEquals("Green", colour1);
		
		String type1 = (String) reader.read();
		
		assertEquals("Apple", type1);
		
		String colour2 = (String) reader.read();
		
		assertEquals("Yellow", colour2);
		
		String type2 = (String) reader.read();
		
		assertEquals("Banana", type2);
		
		reader.close();
		
		layout.reset();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		DataWriter writer = layout.writerFor(new OutputStreamOut(output));
		
		writer.write("Green");
		writer.write("Apple");
		writer.write("Yellow");
		writer.write("Banana");
		
		writer.close();
		
		String expected = 
				"Green,Apple" + EOL + 
				"Yellow,Banana" + EOL;
		
		assertEquals(expected, output.toString());
		
		
	}
	
}

package org.oddjob.dido.other;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataPlanType;
import org.oddjob.dido.DataPlan;
import org.oddjob.dido.DataReadJob;
import org.oddjob.dido.DataWriteJob;
import org.oddjob.dido.WhereNextOut;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.stream.StreamIn;
import org.oddjob.dido.stream.StreamOut;
import org.oddjob.dido.text.Field;
import org.oddjob.dido.text.FieldsIn;
import org.oddjob.dido.text.FieldsOut;
import org.oddjob.dido.text.HeadingsFieldsOut;
import org.oddjob.dido.text.MappedFieldsOut;

public class WhenTest extends TestCase {

	String EOL = System.getProperty("line.separator");
	
	public static class Person {
		
		private String name;
		private String city;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
	}
	
	public static class Fruit {
		
		private String variety;
		private String type;
		private String colour;
		
		public String getVariety() {
			return variety;
		}
		public void setVariety(String variety) {
			this.variety = variety;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getColour() {
			return colour;
		}
		public void setColour(String colour) {
			this.colour = colour;
		}
	}
	
	
	public void testWritingSequenceTheory() throws DataException {
		
		Field on = new Field();
		on.setValue("1");
		
		Field child = new Field();
		child.setValue("apple");
		
		When<FieldsIn, FieldsOut> test = 
			new When<FieldsIn, FieldsOut>();
		test.setValue("1");
		test.setIs(0, child);
				
		HeadingsFieldsOut headingsOut = new HeadingsFieldsOut(
				new MappedFieldsOut.FieldsWriter() {
					@Override
					public void write(String[] values) {
						throw new RuntimeException("Unexpected.");
					}
		}, false);

		
		on.begin(headingsOut);
		
		test.begin(headingsOut);
		
		final AtomicReference<String[]> ref = new AtomicReference<String[]>();
		
		MappedFieldsOut dataOut = new MappedFieldsOut(
				new MappedFieldsOut.FieldsWriter() {
					@Override
					public void write(String[] values) {
						ref.set(values);
					}
		});
		
		// null won't be written out.
		on.out(dataOut);
		
		WhereNextOut<FieldsOut> whereNext = test.out(dataOut);
		assertEquals(child, whereNext.getChildren()[0]);
		assertNotNull(whereNext);
		
		child.out(dataOut);
		
		dataOut.flush();
		
		on.complete(dataOut);
		child.complete(dataOut);
		test.complete(dataOut);
		
		assertEquals(2, ref.get().length);
		assertEquals("1", ref.get()[0]);
		assertEquals("apple", ref.get()[1]);
	}
	
	String delimitedConfig = 
		"<lines>" +
		" <is>" +
		"  <delimited>" +
		"   <is>" +
		"    <case>" +
		"     <is>" +
		"      <field id='descriminator' column='1'/>" +
		"      <when name='people' value='1'>" +
		"       <is>" +
		"        <field name='name' column='2'/>" +
		"        <field name='city' column='3'/>" +
		"       </is>" +
		"      </when>" +
		"      <when name='fruit' value='2'>" +
		"       <is>" +
		"        <field name='variety' column='2'/>" +
	    "        <field name='type' column='3'/>" +
	    "        <field name='colour' column='4'/>" +
	    "       </is>" +
	    "      </when>" +
		"     </is>" +
		"    </case>" +
		"   </is>" +
		"  </delimited>" +
		" </is>" +
		"</lines>";
		
	StandardArooaSession session = new StandardArooaSession();
	
	BeanBindingBean personBinding = new BeanBindingBean();
	{
		personBinding.setArooaSession(session);
		personBinding.setNode("people");
		personBinding.setType(Person.class);
	}
	
	BeanBindingBean fruitBinding = new BeanBindingBean(); 
	{
		fruitBinding.setArooaSession(session);
		fruitBinding.setNode("fruit");
		fruitBinding.setType(Fruit.class);
	}
	
	public void testDelimitedReadWrite() throws ArooaConversionException {
		
		String data = 
			"1,John,London" + EOL +
			"1,Harry,Manchester" + EOL +
			"2,Cox,Apple,Red" + EOL +
			"2,Granny Smith,Apple,Green" + EOL;
	
		DataPlanType definition = new DataPlanType();
		definition.setArooaSession(session);
		definition.setConfiguration(new XMLConfiguration("XML", delimitedConfig));
		
		DataReadJob readJob = new DataReadJob();
		readJob.setPlan((DataPlan<StreamIn, ?, ?, ?>) definition.toValue());
		readJob.setBindings(0, personBinding);
		readJob.setBindings(1, fruitBinding);
		readJob.setInput(new ByteArrayInputStream(data.getBytes()));
		
		readJob.run();
		
		Object[] beans = readJob.getBeans();
		assertEquals(4, beans.length);
		
		Person person1 = (Person) beans[0];
		assertEquals("John", person1.getName());
		assertEquals("London", person1.getCity());
		
		Person person2 = (Person) beans[1];
		assertEquals("Harry", person2.getName());
		assertEquals("Manchester", person2.getCity());
		
		Fruit fruit1 = (Fruit) beans[2];
		assertEquals("Cox", fruit1.getVariety());
		assertEquals("Apple", fruit1.getType());
		assertEquals("Red", fruit1.getColour());
		
		Fruit fruit2 = (Fruit) beans[3];
		assertEquals("Granny Smith", fruit2.getVariety());
		assertEquals("Apple", fruit2.getType());
		assertEquals("Green", fruit2.getColour());
				
		DataWriteJob writeJob = new DataWriteJob();
		writeJob.setPlan((DataPlan<?, ?, StreamOut, ?>) definition.toValue());
		writeJob.setBindings(0, personBinding);
		writeJob.setBindings(1, fruitBinding);		
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		writeJob.setOutput(output);
		
		writeJob.run();
		
		assertEquals(data, new String(output.toByteArray()));
	}
	
	public void testDelimitedWriteRead() throws ArooaConversionException {
		
		DataPlanType definition = new DataPlanType();
		definition.setArooaSession(session);
		definition.setConfiguration(new XMLConfiguration("XML", delimitedConfig));
		
		Person person1 = new Person();
		person1.setName("John");
		person1.setCity("London");
		
		Person person2 = new Person();
		person2.setName("Harry");
		person2.setCity("Manchester");
		
		Fruit fruit1 = new Fruit();
		fruit1.setVariety("Cox");
		fruit1.setType("Apple");
		fruit1.setColour("Red");
		
		Fruit fruit2 = new Fruit();
		fruit2.setVariety("Granny Smith");
		fruit2.setType("Apple");
		fruit2.setColour("Green");
		
		DataWriteJob writeJob = new DataWriteJob();
		writeJob.setPlan((DataPlan<?, ?, StreamOut, ?>) definition.toValue());
		writeJob.setBindings(0, personBinding);
		writeJob.setBindings(1, fruitBinding);		
		
		List<Object> beans = new ArrayList<Object>();
		beans.add(person1);
		beans.add(person2);
		beans.add(fruit1);
		beans.add(fruit2);
		
		writeJob.setBeans(beans);
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		writeJob.setOutput(output);
		
		writeJob.run();
		
		DataReadJob readJob = new DataReadJob();
		readJob.setPlan((DataPlan<StreamIn, ?, ?, ?>) definition.toValue());
		readJob.setBindings(0, personBinding);
		readJob.setBindings(1, fruitBinding);
		readJob.setInput(new ByteArrayInputStream(output.toByteArray()));
		
		readJob.run();
		
		Object[] results = readJob.getBeans();
		assertEquals(4, results.length);
		
		person1 = (Person) results[0];
		assertEquals("John", person1.getName());
		assertEquals("London", person1.getCity());
		
		person2 = (Person) results[1];
		assertEquals("Harry", person2.getName());
		assertEquals("Manchester", person2.getCity());
		
		fruit1 = (Fruit) results[2];
		assertEquals("Cox", fruit1.getVariety());
		assertEquals("Apple", fruit1.getType());
		assertEquals("Red", fruit1.getColour());
		
	}
	
	String fixedConfig = 
		"<lines xmlns:dido='http://rgordon.co.uk/oddjob/dido'>" +
		" <is>" +
		"  <case>" +
		"   <is>" +
		"    <text from='0' length='1'/>" +
		"    <when name='people' value='1'>" +
		"     <is>" +
		"      <text name='name' from='1' length='7'/>" +
	    "      <text name='city' from='8' length='12'/>" +
	    "     </is>" +
	    "    </when>" +
		"    <when name='fruit' value='2'>" +
		"     <is>" +
		"      <text name='variety' from='1' length='12'/>" +
	    "      <text name='type' from='13' length='12'/>" +
	    "      <text name='colour' from='25' length='9'/>" +
	    "     </is>" +
		"    </when>" +
		"   </is>" +
		"  </case>" +
		" </is>" +
		"</lines>";
		
	public void testFixedReadWrite() throws ArooaConversionException {
		
		String data = 
			"1John   London      " + EOL +
			"1Harry  Manchester  " + EOL +
//                     1         2         3			
//           0123456789012345678901234567890123456789
			"2Cox         Apple       Red      " + EOL +
			"2Granny SmithApple       Green    " + EOL;
	
		DataPlanType definition = new DataPlanType();
		definition.setArooaSession(session);
		definition.setConfiguration(new XMLConfiguration("XML", fixedConfig));
		
		DataReadJob readJob = new DataReadJob();
		readJob.setPlan((DataPlan<StreamIn, ?, ?, ?>) definition.toValue());
		readJob.setBindings(0, personBinding);
		readJob.setBindings(1, fruitBinding);
		readJob.setInput(new ByteArrayInputStream(data.getBytes()));
		
		readJob.run();
		
		Object[] beans = readJob.getBeans();
		assertEquals(4, beans.length);
		
		Person person1 = (Person) beans[0];
		assertEquals("John", person1.getName());
		assertEquals("London", person1.getCity());
		
		Person person2 = (Person) beans[1];
		assertEquals("Harry", person2.getName());
		assertEquals("Manchester", person2.getCity());
		
		Fruit fruit1 = (Fruit) beans[2];
		assertEquals("Cox", fruit1.getVariety());
		assertEquals("Apple", fruit1.getType());
		assertEquals("Red", fruit1.getColour());
		
		Fruit fruit2 = (Fruit) beans[3];
		assertEquals("Granny Smith", fruit2.getVariety());
		assertEquals("Apple", fruit2.getType());
		assertEquals("Green", fruit2.getColour());
				
		DataWriteJob writeJob = new DataWriteJob();
		writeJob.setPlan((DataPlan<?, ?, StreamOut, ?>) definition.toValue());
		writeJob.setBindings(0, personBinding);
		writeJob.setBindings(1, fruitBinding);		
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		writeJob.setOutput(output);
		
		writeJob.run();
		
		assertEquals(data, new String(output.toByteArray()));
		
	}

	public void testMixedExample() throws ArooaConversionException {
		
		String xml = 
			"<lines>" +
			" <is>" +
			"  <case>" +
			"   <is>" +
			"    <text id='descriminator' from='0' length='1'/>" +
			"    <when name='people' value='1'>" +
			"     <is>" +
			"      <text name='name' from='1' length='7'/>" +
		    "      <text name='city' from='8' length='12'/>" +
		    "     </is>" +
		    "    </when>" +
			"    <when name='fruit' value='2'>" +
			"     <is>" +
			"      <text name='all' from='1'>" +
			"       <is>" +
			"        <delimited>" +
			"         <is>" +
			"          <field name='variety' column='1'/>" +
		    "          <field name='type' column='2'/>" +
		    "          <field name='colour' column='3'/>" +
		    "         </is>" +
			"        </delimited>" +
			"       </is>" +
			"      </text>" +
		    "     </is>" +
			"    </when>" +
			"   </is>" +
			"  </case>" +
			" </is>" +
			"</lines>";
			
			
		String data = 
			"1John   London      " + EOL +
			"1Harry  Manchester  " + EOL +
			"2Cox,Apple,Red" + EOL +
			"2Granny Smith,Apple,Green" + EOL;
	
		
		DataPlanType definition = new DataPlanType();
		definition.setArooaSession(session);
		definition.setConfiguration(new XMLConfiguration("XML", xml));
		
		DataReadJob readJob = new DataReadJob();
		readJob.setPlan((DataPlan<StreamIn, ?, ?, ?>) definition.toValue());
		readJob.setBindings(0, personBinding);
		readJob.setBindings(1, fruitBinding);
		readJob.setInput(new ByteArrayInputStream(data.getBytes()));
		
		readJob.run();
		
		Object[] beans = readJob.getBeans();
		assertEquals(4, beans.length);
		
		Person person1 = (Person) beans[0];
		assertEquals("John", person1.getName());
		assertEquals("London", person1.getCity());
		
		Person person2 = (Person) beans[1];
		assertEquals("Harry", person2.getName());
		assertEquals("Manchester", person2.getCity());
		
		Fruit fruit1 = (Fruit) beans[2];
		assertEquals("Cox", fruit1.getVariety());
		assertEquals("Apple", fruit1.getType());
		assertEquals("Red", fruit1.getColour());
		
		Fruit fruit2 = (Fruit) beans[3];
		assertEquals("Granny Smith", fruit2.getVariety());
		assertEquals("Apple", fruit2.getType());
		assertEquals("Green", fruit2.getColour());
				
		DataWriteJob writeJob = new DataWriteJob();
		writeJob.setPlan((DataPlan<?, ?, StreamOut, ?>) definition.toValue());
		writeJob.setBindings(0, personBinding);
		writeJob.setBindings(1, fruitBinding);
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		writeJob.setOutput(output);
		
		writeJob.run();
		
		assertEquals(data, new String(output.toByteArray()));
		
	}
}

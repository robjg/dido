package org.oddjob.dido.other;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ImportType;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReadJob;
import org.oddjob.dido.DataWriteJob;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.bio.ValueBinding;
import org.oddjob.dido.text.FieldLayout;
import org.oddjob.dido.text.SimpleFieldsOut;

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
		
		FieldLayout on = new FieldLayout();
		on.bind(new ValueBinding());
		
		FieldLayout child = new FieldLayout();
		child.bind(new ValueBinding());
		
		When test = 
			new When();
		test.setValue("1");
		test.setOf(0, child);
				
		SimpleFieldsOut fieldsOut = new SimpleFieldsOut();

		DataWriter writer = on.writerFor(fieldsOut);

		writer.write("1");

		writer = test.writerFor(fieldsOut);
		
		writer.write("apple");
		
		String[] values = fieldsOut.values();
		assertEquals(2, values.length);
		assertEquals("1", values[0]);
		assertEquals("apple", values[1]);
	}
	
	String delimitedConfig = 
		"<lines xmlns='oddjob:dido'>" +
		" <of>" +
		"  <delimited>" +
		"   <of>" +
		"    <case>" +
		"     <of>" +
		"      <field name='descriminator' column='1'/>" +
		"      <when name='people' value='1'>" +
		"       <of>" +
		"        <field name='name' column='2'/>" +
		"        <field name='city' column='3'/>" +
		"       </of>" +
		"      </when>" +
		"      <when name='fruit' value='2'>" +
		"       <of>" +
		"        <field name='variety' column='2'/>" +
	    "        <field name='type' column='3'/>" +
	    "        <field name='colour' column='4'/>" +
	    "       </of>" +
	    "      </when>" +
		"     </of>" +
		"    </case>" +
		"   </of>" +
		"  </delimited>" +
		" </of>" +
		"</lines>";
		
	StandardArooaSession session = new StandardArooaSession();
	
	BeanBindingBean personBinding = new BeanBindingBean();
	{
		personBinding.setArooaSession(session);
		personBinding.setType(new SimpleArooaClass(Person.class));
	}
	
	BeanBindingBean fruitBinding = new BeanBindingBean(); 
	{
		fruitBinding.setArooaSession(session);
		fruitBinding.setType(new SimpleArooaClass(Fruit.class));
	}
	
	public void testDelimitedReadWrite() throws ArooaConversionException, IOException {
		
		String data = 
			"1,John,London" + EOL +
			"1,Harry,Manchester" + EOL +
			"2,Cox,Apple,Red" + EOL +
			"2,Granny Smith,Apple,Green" + EOL;
	
		ImportType importType = new ImportType();
		importType.setArooaSession(new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader())));
		importType.setXml(delimitedConfig);
		
		Layout layout = (Layout) importType.toObject();
		
		DataReadJob readJob = new DataReadJob();
		readJob.setPlan(layout);
		readJob.setBindings("people", personBinding);
		readJob.setBindings("fruit", fruitBinding);
		readJob.setInput(new ByteArrayInputStream(data.getBytes()));
		
		Collection<Object> results = new ArrayList<Object>();
		
		readJob.setBeans(results);
		
		readJob.run();
		
		Object[] beans = results.toArray();
		
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
		writeJob.setPlan(layout);
		writeJob.setBindings("people", personBinding);
		writeJob.setBindings("fruit", fruitBinding);		
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		writeJob.setOutput(output);
		
		writeJob.run();
		
		assertEquals(data, new String(output.toByteArray()));
	}
	
	public void testDelimitedWriteRead() throws ArooaConversionException, IOException {
		
		ImportType importType = new ImportType();
		importType.setArooaSession(new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader())));
		importType.setXml(delimitedConfig);
		
		Layout layout = (Layout) importType.toObject();
		
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
		writeJob.setPlan(layout);
		writeJob.setBindings("people", personBinding);
		writeJob.setBindings("fruit", fruitBinding);		
		
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
		readJob.setPlan(layout);
		readJob.setBindings("people", personBinding);
		readJob.setBindings("fruit", fruitBinding);
		readJob.setInput(new ByteArrayInputStream(output.toByteArray()));
		readJob.setBeans(new ArrayList<Object>());
		readJob.run();
		
		Object[] results = readJob.getBeans().toArray();
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
		"<lines xmlns='oddjob:dido'>" +
		" <of>" +
		"  <case>" +
		"   <of>" +
		"    <text from='0' length='1'/>" +
		"    <when name='people' value='1'>" +
		"     <of>" +
		"      <text name='name' from='1' length='7'/>" +
	    "      <text name='city' from='8' length='12'/>" +
	    "     </of>" +
	    "    </when>" +
		"    <when name='fruit' value='2'>" +
		"     <of>" +
		"      <text name='variety' from='1' length='12'/>" +
	    "      <text name='type' from='13' length='12'/>" +
	    "      <text name='colour' from='25' length='9'/>" +
	    "     </of>" +
		"    </when>" +
		"   </of>" +
		"  </case>" +
		" </of>" +
		"</lines>";
		
	public void testFixedReadWrite() throws ArooaConversionException, IOException {
		
		String data = 
			"1John   London      " + EOL +
			"1Harry  Manchester  " + EOL +
//                     1         2         3			
//           0123456789012345678901234567890123456789
			"2Cox         Apple       Red      " + EOL +
			"2Granny SmithApple       Green    " + EOL;
	
		ImportType importType = new ImportType();
		importType.setArooaSession(new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader())));
		importType.setXml(fixedConfig);
		
		Layout layout = (Layout) importType.toObject();
		
		DataReadJob readJob = new DataReadJob();
		readJob.setPlan(layout);
		readJob.setBindings("people", personBinding);
		readJob.setBindings("fruit", fruitBinding);
		readJob.setInput(new ByteArrayInputStream(data.getBytes()));
		readJob.setBeans(new ArrayList<Object>());
		
		readJob.run();
		
		Object[] beans = readJob.getBeans().toArray();
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
		writeJob.setPlan(layout);
		writeJob.setBindings("people", personBinding);
		writeJob.setBindings("fruit", fruitBinding);		
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		writeJob.setOutput(output);
		
		writeJob.run();
		
		assertEquals(data, new String(output.toByteArray()));
		
	}

	public void testMixedExample() throws ArooaConversionException, IOException {
		
		String xml = 
			"<lines xmlns='oddjob:dido'>" +
			" <of>" +
			"  <case>" +
			"   <of>" +
			"    <text name='descriminator' from='0' length='1'/>" +
			"    <when name='people' value='1'>" +
			"     <of>" +
			"      <text name='name' from='1' length='7'/>" +
		    "      <text name='city' from='8' length='12'/>" +
		    "     </of>" +
		    "    </when>" +
			"    <when name='fruit' value='2'>" +
			"     <of>" +
			"      <text name='all' from='1'>" +
			"       <of>" +
			"        <delimited>" +
			"         <of>" +
			"          <field name='variety' column='1'/>" +
		    "          <field name='type' column='2'/>" +
		    "          <field name='colour' column='3'/>" +
		    "         </of>" +
			"        </delimited>" +
			"       </of>" +
			"      </text>" +
		    "     </of>" +
			"    </when>" +
			"   </of>" +
			"  </case>" +
			" </of>" +
			"</lines>";
			
			
		String data = 
			"1John   London      " + EOL +
			"1Harry  Manchester  " + EOL +
			"2Cox,Apple,Red" + EOL +
			"2Granny Smith,Apple,Green" + EOL;
	
		
		ImportType importType = new ImportType();
		importType.setArooaSession(new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader())));
		importType.setXml(xml);
		
		Layout layout = (Layout) importType.toObject();
		
		DataReadJob readJob = new DataReadJob();
		readJob.setPlan(layout);
		readJob.setBindings("people", personBinding);
		readJob.setBindings("fruit", fruitBinding);
		readJob.setInput(new ByteArrayInputStream(data.getBytes()));
		readJob.setBeans(new ArrayList<Object>());
		
		readJob.run();
		
		Object[] beans = readJob.getBeans().toArray();
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
		writeJob.setPlan(layout);
		writeJob.setBindings("people", personBinding);
		writeJob.setBindings("fruit", fruitBinding);
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		writeJob.setOutput(output);
		
		writeJob.run();
		
		assertEquals(data, new String(output.toByteArray()));
		
	}
}

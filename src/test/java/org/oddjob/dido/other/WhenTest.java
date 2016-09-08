package org.oddjob.dido.other;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.types.ImportType;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReadJob;
import org.oddjob.dido.DataWriteJob;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.stream.IOStreamData;
import org.oddjob.dido.text.SimpleTextFieldsOut;
import org.oddjob.dido.text.TextLayout;

public class WhenTest extends TestCase {

	private static final Logger logger = Logger.getLogger(WhenTest.class);
	
	protected void setUp() throws Exception {
		super.setUp();
		
		logger.info("------------------   " + 
				getName() + "   ----------------------");
	}
	
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
		
		TextLayout on = new TextLayout();
		on.setBinding(new DirectBinding());
		
		TextLayout child = new TextLayout();
		child.setBinding(new DirectBinding());
		
		When test = 
			new When();
		test.setValue("1");
		test.setOf(0, child);
				
		SimpleTextFieldsOut fieldsOut = new SimpleTextFieldsOut();

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
		"      <text name='descriminator' index='1'/>" +
		"      <when name='people' value='1'>" +
		"       <of>" +
		"        <text name='name' index='2'/>" +
		"        <text name='city' index='3'/>" +
		"       </of>" +
		"      </when>" +
		"      <when name='fruit' value='2'>" +
		"       <of>" +
		"        <text name='variety' index='2'/>" +
	    "        <text name='type' index='3'/>" +
	    "        <text name='colour' index='4'/>" +
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
	
	public void testDelimitedReadWrite() throws ArooaConversionException, IOException, DataException {
		
		String data = 
			"1,John,London" + EOL +
			"1,Harry,Manchester" + EOL +
			"2,Cox,Apple,Red" + EOL +
			"2,Granny Smith,Apple,Green" + EOL;
	
		ArooaSession session = new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader()));
		
		ImportType importType = new ImportType();
		importType.setArooaSession(session);
		importType.setXml(delimitedConfig);
		
		IOStreamData ioData = new IOStreamData();
		ioData.setArooaSession(session);
		ioData.setInput(new ArooaObject(
				new ByteArrayInputStream(data.getBytes())));
		
		Layout layout = (Layout) importType.toObject();
		
		DataReadJob readJob = new DataReadJob();
		readJob.setArooaSession(session);
		readJob.setLayout(layout);
		readJob.setBindings("people", personBinding);
		readJob.setBindings("fruit", fruitBinding);
		readJob.setData(ioData);
		
		Collection<Object> results = new ArrayList<Object>();
		
		readJob.setBeans(results);
		
		readJob.call();
		
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
		writeJob.setArooaSession(session);
		writeJob.setLayout(layout);
		writeJob.setBindings("people", personBinding);
		writeJob.setBindings("fruit", fruitBinding);		
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ioData.setOutput(new ArooaObject(output));
		writeJob.setData(ioData);
		
		writeJob.call();
		
		assertEquals(data, new String(output.toByteArray()));
	}
	
	public void testDelimitedWriteRead() throws ArooaConversionException, IOException, DataException {
		
		ArooaSession session = new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader()));
		
		ImportType importType = new ImportType();
		importType.setArooaSession(session);
		importType.setXml(delimitedConfig);
		
		IOStreamData ioData = new IOStreamData();
		ioData.setArooaSession(session);
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		ioData.setOutput(new ArooaObject(output));
				
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
		writeJob.setArooaSession(session);
		writeJob.setLayout(layout);
		writeJob.setBindings("people", personBinding);
		writeJob.setBindings("fruit", fruitBinding);		
		
		List<Object> beans = new ArrayList<Object>();
		beans.add(person1);
		beans.add(person2);
		beans.add(fruit1);
		beans.add(fruit2);
		
		writeJob.setBeans(beans);
		
		writeJob.setData(ioData);
		
		writeJob.call();
		
		DataReadJob readJob = new DataReadJob();
		readJob.setArooaSession(session);
		readJob.setLayout(layout);
		readJob.setBindings("people", personBinding);
		readJob.setBindings("fruit", fruitBinding);
		readJob.setBeans(new ArrayList<Object>());
		
		ioData.setInput(new ArooaObject(
				new ByteArrayInputStream(output.toByteArray())));
		readJob.setData(ioData);
		
		readJob.call();
		
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
		"<fixed xmlns='oddjob:dido'>" +
		" <of>" +
		"  <case>" +
		"   <of>" +
		"    <text index='1' length='1'/>" +
		"    <when name='people' value='1'>" +
		"     <of>" +
		"      <text name='name' index='2' length='7'/>" +
	    "      <text name='city' index='9' length='12'/>" +
	    "     </of>" +
	    "    </when>" +
		"    <when name='fruit' value='2'>" +
		"     <of>" +
		"      <text name='variety' index='2' length='12'/>" +
	    "      <text name='type' index='14' length='12'/>" +
	    "      <text name='colour' index='26' length='9'/>" +
	    "     </of>" +
		"    </when>" +
		"   </of>" +
		"  </case>" +
		" </of>" +
		"</fixed>";
		
	public void testFixedReadWrite() throws ArooaConversionException, IOException, DataException {
		
		String data = 
			"1John   London      " + EOL +
			"1Harry  Manchester  " + EOL +
//                     1         2         3			
//           123456789012345678901234567890123456789
			"2Cox         Apple       Red      " + EOL +
			"2Granny SmithApple       Green    " + EOL;
	
		ArooaSession session = new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader()));
		
		ImportType importType = new ImportType();
		importType.setArooaSession(session);
		importType.setXml(fixedConfig);
		
		IOStreamData ioData = new IOStreamData();
		ioData.setArooaSession(session);
		ioData.setInput(new ArooaObject(
				new ByteArrayInputStream(data.getBytes())));

		Layout layout = (Layout) importType.toObject();
		
		DataReadJob readJob = new DataReadJob();
		readJob.setArooaSession(session);
		readJob.setLayout(layout);
		readJob.setBindings("people", personBinding);
		readJob.setBindings("fruit", fruitBinding);
		readJob.setData(ioData);
		readJob.setBeans(new ArrayList<Object>());
		
		readJob.call();
		
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
		writeJob.setArooaSession(session);
		writeJob.setLayout(layout);
		writeJob.setBindings("people", personBinding);
		writeJob.setBindings("fruit", fruitBinding);		
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ioData.setOutput(new ArooaObject(output));
		
		writeJob.setData(ioData);
		
		writeJob.call();
		
		assertEquals(data, new String(output.toByteArray()));
		
	}

	public void testMixedExample() throws ArooaConversionException, IOException, DataException {
		
		String xml = 
			"<fixed xmlns='oddjob:dido'>" +
			" <of>" +
			"  <case>" +
			"   <of>" +
			"    <text name='descriminator' index='1' length='1'/>" +
			"    <when name='people' value='1'>" +
			"     <of>" +
			"      <text name='name' index='2' length='7'/>" +
		    "      <text name='city' index='9' length='12'/>" +
		    "     </of>" +
		    "    </when>" +
			"    <when name='fruit' value='2'>" +
			"     <of>" +
			"      <text name='all' index='2'>" +
			"       <of>" +
			"        <delimited>" +
			"         <of>" +
			"          <text name='variety' index='1'/>" +
		    "          <text name='type' index='2'/>" +
		    "          <text name='colour' index='3'/>" +
		    "         </of>" +
			"        </delimited>" +
			"       </of>" +
			"      </text>" +
		    "     </of>" +
			"    </when>" +
			"   </of>" +
			"  </case>" +
			" </of>" +
			"</fixed>";			
			
		String data = 
			"1John   London      " + EOL +
			"1Harry  Manchester  " + EOL +
			"2Cox,Apple,Red" + EOL +
			"2Granny Smith,Apple,Green" + EOL;
	
		ArooaSession session = new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader()));
		
		ImportType importType = new ImportType();
		importType.setArooaSession(session);
		importType.setXml(xml);
		
		IOStreamData ioData = new IOStreamData();
		ioData.setArooaSession(session);
		ioData.setInput(new ArooaObject(
				new ByteArrayInputStream(data.getBytes())));
		
		Layout layout = (Layout) importType.toObject();
		
		DataReadJob readJob = new DataReadJob();
		readJob.setArooaSession(session);
		readJob.setLayout(layout);
		readJob.setBindings("people", personBinding);
		readJob.setBindings("fruit", fruitBinding);
		readJob.setData(ioData);
		readJob.setBeans(new ArrayList<Object>());
		
		readJob.call();
		
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
		writeJob.setArooaSession(session);
		writeJob.setLayout(layout);
		writeJob.setBindings("people", personBinding);
		writeJob.setBindings("fruit", fruitBinding);
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ioData.setOutput(new ArooaObject(output));
		
		writeJob.setData(ioData);
		
		writeJob.call();
		
		assertEquals(data, new String(output.toByteArray()));
		
	}
}

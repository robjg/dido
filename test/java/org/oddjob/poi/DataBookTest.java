package org.oddjob.poi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ImportType;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.dido.DataReadJob;
import org.oddjob.dido.DataWriteJob;
import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.bio.ValueBinding;
import org.oddjob.io.TeeType;

public class DataBookTest extends TestCase {
	private static final Logger logger = Logger.getLogger(DataBookTest.class);

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		logger.info("-------------------   " + getName() + "   --------------");
	}
	
	public static class Person {
		
		private String name;
		private Date dateOfBirth;
		private Double salary;
		
		public Person(String name,
				Date dateOfBirth,
				Double salery) {
			this.name = name;
			this.dateOfBirth = dateOfBirth;
			this.salary = salery;
		}
		
		public Person() {
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Date getDateOfBirth() {
			return dateOfBirth;
		}
		public void setDateOfBirth(Date dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}
		public Double getSalary() {
			return salary;
		}
		public void setSalary(Double salery) {
			this.salary = salery;
		}
	}
		
	public static class PersonAnd extends Person {
		
		private Double percentage;

		private Double bonus;
		
		public Double getPercentage() {
			return percentage;
		}

		public void setPercentage(Double percentage) {
			this.percentage = percentage;
		}
		
		public Double getBonus() {
			return bonus;
		}
		public void setBonus(Double bonus) {
			this.bonus = bonus;
		}	
	}
	
	public void testWriteReadWithHeadings() throws ParseException, ArooaConversionException, IOException {
		
		doWriteRead("org/oddjob/poi/SimpleWithHeadings.xml");
	}

	public void testWriteReadWithoutHeadings() throws ParseException, ArooaConversionException, IOException {
		
		doWriteRead("org/oddjob/poi/SimpleWithoutHeadings.xml");
	}
	
	public void doWriteRead(String resource) throws ParseException, ArooaConversionException, IOException {
		
		StandardArooaSession session = new StandardArooaSession();
		
		List<Object> beans = new ArrayList<Object>();
		beans.add(new Person("John", DateHelper.parseDate("1970-03-25"), 45000.0));
		beans.add(new Person("Jane", DateHelper.parseDate("1982-11-14"), 28000.0));
		beans.add(new Person("Fred", DateHelper.parseDate("1986-08-07"), 22500.0));

		BeanBindingBean bindingBean = new BeanBindingBean();
		bindingBean.setArooaSession(session);
		bindingBean.setType(new SimpleArooaClass(Person.class));
		
		ValueBinding valueBinding = new ValueBinding();
		valueBinding.setArooaSession(session);
		valueBinding.setValue("0.1");
		
		ImportType importType = new ImportType();
		importType.setArooaSession(new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader())));
		importType.setResource(resource);
		
		Layout layout = (Layout) importType.toObject();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		TeeType teeType = new TeeType();
		teeType.setOutputs(0, output);
		teeType.setOutputs(1, new FileOutputStream(new File("BookTest.xslx")));
		
		
		DataWriteJob write = new DataWriteJob();
		write.setPlan(layout);
		write.setBeans(beans);
		write.setBindings("person", bindingBean);
		write.setBindings("percentage", valueBinding);
		
		write.setOutput(teeType.toValue());
		
		write.run();
		
		bindingBean.setType(new SimpleArooaClass(PersonAnd.class));
		
		DataReadJob read =  new DataReadJob();
		read.setInput(new ByteArrayInputStream(output.toByteArray()));
		read.setPlan(layout);
		read.setBindings("person", bindingBean);
		read.setBeans(new ArrayList<Object>());
		
		read.run();
		
		Object[] results = read.getBeans().toArray();

		assertEquals(3, results.length);
		
		PersonAnd person1 = (PersonAnd) results[0];
		assertEquals("John", person1.getName());
		assertEquals(DateHelper.parseDate("1970-03-25"), person1.getDateOfBirth());
		assertEquals(45000.0, person1.getSalary());
		assertEquals(0.1, person1.getPercentage());
		assertEquals(10.0, person1.getBonus());

		PersonAnd person2 = (PersonAnd) results[1];
		assertEquals("Jane", person2.getName());
		assertEquals(DateHelper.parseDate("1982-11-14"), person2.getDateOfBirth());
		assertEquals(28000.0, person2.getSalary());
		assertEquals(0.1, person2.getPercentage());
		assertEquals(10.0, person2.getBonus());
		
		PersonAnd person3 = (PersonAnd) results[2];
		assertEquals("Fred", person3.getName());
		assertEquals(DateHelper.parseDate("1986-08-07"), person3.getDateOfBirth());
		assertEquals(22500.0, person3.getSalary());
		assertEquals(0.1, person3.getPercentage());
		assertEquals(10.0, person3.getBonus());
		
		assertEquals(3, results.length);
	}
	
	public void testNoData() throws ParseException, ArooaConversionException, IOException {
		
		ArooaSession session = new StandardArooaSession();
		
		List<Object> beans = new ArrayList<Object>();

		BeanBindingBean bindingBean = new BeanBindingBean();
		bindingBean.setArooaSession(session);
		bindingBean.setType(new SimpleArooaClass(Person.class));
		
		ImportType importType = new ImportType();
		importType.setArooaSession(new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader())));
		importType.setResource("org/oddjob/poi/SimpleWithHeadings.xml");
		
		Layout layout = (Layout) importType.toObject();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		TeeType teeType = new TeeType();
		teeType.setOutputs(0, output);
		teeType.setOutputs(1, new FileOutputStream(new File("BookTest.xlsx")));
		
		DataWriteJob write = new DataWriteJob();		
		write.setPlan(layout);
		write.setBeans(beans);
		write.setBindings("person", bindingBean);
		
		write.setOutput(teeType.toValue());
		
		write.run();
		
		bindingBean.setType(new SimpleArooaClass(PersonAnd.class));
		
		DataReadJob read =  new DataReadJob();
		read.setInput(new ByteArrayInputStream(output.toByteArray()));
		read.setPlan(layout);
		read.setBindings("person", bindingBean);
		read.setBeans(new ArrayList<Object>());
		
		read.run();
		
		Object[] results = read.getBeans().toArray();
				
		assertEquals(0, results.length);
	}
}

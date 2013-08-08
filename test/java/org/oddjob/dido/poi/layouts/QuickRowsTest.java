package org.oddjob.dido.poi.layouts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.oddjob.OurDirs;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.BeanViewBean;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.types.ImportType;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReadJob;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriteJob;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.io.Nodes;
import org.oddjob.dido.poi.SheetIn;
import org.oddjob.dido.poi.SheetOut;
import org.oddjob.dido.poi.data.PoiSheetIn;
import org.oddjob.dido.poi.data.PoiSheetOut;
import org.oddjob.dido.poi.data.PoiWorkbook;
import org.oddjob.dido.poi.style.DefaultStyleProivderFactory;
import org.oddjob.dido.poi.style.StyleProvider;

public class QuickRowsTest extends TestCase {

	File workDir;
	
	private static final Logger logger = Logger.getLogger(QuickRowsTest.class);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		logger.info("----------------------------    " + getName() + 
				"   -------------------------");

		workDir = new OurDirs().relative("work");
	}
	
	public static class Person {

		private String name;
		private Date dateOfBirth;
		private Double salary;

		public Person(String name, Date dateOfBirth, Double salery) {
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
		
		@Override
		public String toString() {
			return "Person: name=" + name + ", dateOfBirth=" +
				dateOfBirth + ", salary=" + salary;
		}
	}

	public void testIdea() throws DataException, InvalidFormatException,
			IOException, ParseException {

		StandardArooaSession session = new StandardArooaSession();

		BeanViewBean beanView = new BeanViewBean();
		beanView.setProperties("name, dateOfBirth, salary");

		BeanBindingBean binding = new BeanBindingBean();
		binding.setArooaSession(session);
		binding.setType(new SimpleArooaClass(Person.class));
		binding.setBeanView(beanView.toValue());
		
		DataRows test = new DataRows();
		test.setWithHeadings(true);
		test.bind(binding);

		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
	
		StyleProvider styleProvider = new DefaultStyleProivderFactory().providerFor(
				workbook);
		
		SheetOut sheetOut = new PoiSheetOut(sheet, styleProvider);
		
		Person person = new Person();
		person.setName("John");
		person.setDateOfBirth(DateHelper.parseDate("1970-03-25"));
		person.setSalary(45000.0);

		DataWriter writer = test.writerFor(sheetOut);

		writer.write(person);

		writer.close();
		
		Nodes nodes = new Nodes(test);
		assertNotNull(nodes.getNode("name"));
		assertNotNull(nodes.getNode("dateOfBirth"));
		assertNotNull(nodes.getNode("salary"));
		
		assertEquals(1, sheet.getLastRowNum());
		assertEquals(3, sheet.getRow(1).getLastCellNum());

		assertEquals("name", sheet.getRow(0).getCell(0).toString());
		assertEquals("dateOfBirth", sheet.getRow(0).getCell(1).toString());
		assertEquals("salary", sheet.getRow(0).getCell(2).toString());
		assertEquals("John", sheet.getRow(1).getCell(0).toString());
		assertEquals("25-Mar-1970", sheet.getRow(1).getCell(1).toString());
		assertEquals("45000.0", sheet.getRow(1).getCell(2).toString());
				
		binding.free();
		test.reset();
		
		SheetIn sheetIn = new PoiSheetIn(sheet);

		DataReader reader = test.readerFor(sheetIn);

		Person result = (Person) reader.read();
		
		assertEquals("John", result.getName());
		assertEquals(DateHelper.parseDate("1970-03-25"), result.getDateOfBirth());
		assertEquals(45000.0, result.getSalary());
	}

	public void testWriteReadWithHeadings() throws ParseException,
			ArooaConversionException, IOException {

		doWriteRead("org/oddjob/dido/poi/QuickRowsWithHeadings.xml");
	}

	public void doWriteRead(String resource) throws ParseException,
			ArooaConversionException, IOException {

		ArooaSession session = new StandardArooaSession();
		
		List<Object> beans = new ArrayList<Object>();
		beans.add(new Person("John", DateHelper.parseDate("1970-03-25"),
				45000.0));
		beans.add(new Person("Jane", DateHelper.parseDate("1982-11-14"),
				28000.0));
		beans.add(new Person("Fred", DateHelper.parseDate("1986-08-07"),
				22500.0));

		BeanBindingBean bindingBean = new BeanBindingBean();
		bindingBean.setArooaSession(session);
		bindingBean.setType(new SimpleArooaClass(Person.class));

		PoiWorkbook workbook = new PoiWorkbook();
		workbook.setArooaSession(session);
		
		workbook.setOutput(new ArooaObject(
				new FileOutputStream(new File(workDir, 
						"QuickRowsTest.xlsx"))));

		ImportType importType = new ImportType();
		importType.setArooaSession(new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader())));
		importType.setResource(resource);
		
		Layout layout = (Layout) importType.toObject();
		
		DataWriteJob write = new DataWriteJob();
		write.setPlan(layout);
		write.setBeans(beans);
		write.setBindings("person", bindingBean);

		write.setData(workbook);

		write.run();

		// Read Side
		////
		
		bindingBean.setType(new SimpleArooaClass(Person.class));

		DataReadJob read = new DataReadJob();
		read.setData(workbook);
		read.setPlan(layout);
		read.setBindings("person", bindingBean);
		read.setBeans(new ArrayList<Object>());
		read.run();

		Object[] results = read.getBeans().toArray();

		Person person1 = (Person) results[0];
		assertEquals("John", person1.getName());
		assertEquals(DateHelper.parseDate("1970-03-25"),
				person1.getDateOfBirth());
		assertEquals(45000.0, person1.getSalary());

		Person person2 = (Person) results[1];
		assertEquals("Jane", person2.getName());
		assertEquals(DateHelper.parseDate("1982-11-14"),
				person2.getDateOfBirth());
		assertEquals(28000.0, person2.getSalary());

		Person person3 = (Person) results[2];
		assertEquals("Fred", person3.getName());
		assertEquals(DateHelper.parseDate("1986-08-07"),
				person3.getDateOfBirth());
		assertEquals(22500.0, person3.getSalary());

		assertEquals(3, results.length);
	}
}

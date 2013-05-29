package org.oddjob.poi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.BeanViewBean;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataPlan;
import org.oddjob.dido.DataPlanType;
import org.oddjob.dido.DataReadJob;
import org.oddjob.dido.DataWriteJob;
import org.oddjob.dido.Morphicness;
import org.oddjob.dido.MorphicnessFactory;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.io.ConfigurationType;
import org.oddjob.dido.io.Nodes;
import org.oddjob.dido.stream.StreamIn;
import org.oddjob.dido.stream.StreamOut;
import org.oddjob.io.TeeType;

public class QuickRowsTest extends TestCase {

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

		QuickRows test = new QuickRows();
		
		Morphicness morphicness = new MorphicnessFactory(
				session.getTools().getPropertyAccessor()
				).writeMorphicnessFor(new SimpleArooaClass(Person.class), 
						beanView.toValue());
				
		test.beFor(morphicness);

		Nodes nodes = new Nodes(test);
		assertNotNull(nodes.getNode("name"));
		assertNotNull(nodes.getNode("dateOfBirth"));
		assertNotNull(nodes.getNode("salary"));
		
		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
	
		StyleProvider styleProvider = new DefaultStyleFactory().providerFor(
				workbook);
		
		SheetOut sheetOut = new PoiSheetOut(sheet, styleProvider);
		
		WhereNextOut<SheetOut> whereOut2 = test.out(sheetOut);

		assertEquals(3, whereOut2.getChildren().length);
		assertSame(sheetOut, whereOut2.getChildData());

		DataCell<String> cell1 = (DataCell<String>) whereOut2.getChildren()[0];
		DataCell<Date> cell2 = (DataCell<Date>) whereOut2.getChildren()[1];
		DataCell<Double> cell3 = (DataCell<Double>) whereOut2.getChildren()[2];

		cell1.value("John");
		cell2.value(DateHelper.parseDate("1970-03-25"));
		cell3.value(45000.0);

		cell1.out(sheetOut);
		cell2.out(sheetOut);
		cell3.out(sheetOut);

		cell1.complete(sheetOut);
		cell2.complete(sheetOut);
		cell3.complete(sheetOut);

		test.complete(sheetOut);

		assertEquals(1, sheet.getLastRowNum());
		assertEquals(3, sheet.getRow(1).getLastCellNum());

		assertEquals("name", sheet.getRow(0).getCell(0).toString());
		assertEquals("dateOfBirth", sheet.getRow(0).getCell(1).toString());
		assertEquals("salary", sheet.getRow(0).getCell(2).toString());
		assertEquals("John", sheet.getRow(1).getCell(0).toString());
		assertEquals("25-Mar-1970", sheet.getRow(1).getCell(1).toString());
		assertEquals("45000.0", sheet.getRow(1).getCell(2).toString());
		
		morphicness = new MorphicnessFactory(
				session.getTools().getPropertyAccessor()
				).readMorphicnessFor(new SimpleArooaClass(Person.class), 
						beanView.toValue());
		
		test.beFor(morphicness);
		
		SheetIn sheetIn = new PoiSheetIn(sheet);

		WhereNextIn<SheetIn> whereIn2 = test.in(sheetIn);

		assertEquals(3, whereIn2.getChildren().length);

		cell1 = (DataCell<String>) whereIn2.getChildren()[0];
		cell2 = (DataCell<Date>) whereIn2.getChildren()[1];
		cell3 = (DataCell<Double>) whereIn2.getChildren()[2];

		cell1.in(sheetIn);
		cell2.in(sheetIn);
		cell3.in(sheetIn);

		assertEquals("John", cell1.value());
		assertEquals(DateHelper.parseDate("1970-03-25"), cell2.value());
		assertEquals(45000.0, cell3.value());

		cell1.complete(sheetIn);
		cell2.complete(sheetIn);
		cell3.complete(sheetIn);

		test.complete(sheetIn);

	}

	public void testWriteReadWithHeadings() throws ParseException,
			ArooaConversionException, FileNotFoundException {

		doWriteRead("org/oddjob/poi/QuickRowsWithHeadings.xml");
	}

	@SuppressWarnings("unchecked")
	public void doWriteRead(String resource) throws ParseException,
			ArooaConversionException, FileNotFoundException {

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

		DataPlanType guide = new DataPlanType();
		guide.setArooaSession(session);
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		TeeType teeType = new TeeType();
		teeType.setOutputs(0, output);
		teeType.setOutputs(1, new FileOutputStream(new File("BookTest.xslx")));

		guide.setConfiguration(new XMLConfiguration(resource, getClass()
				.getClassLoader()));

		DataWriteJob write = new DataWriteJob();
		write.setPlan(null);
		write.setBeans(beans);
		write.setBindings("person", bindingBean);

		write.setOutput(teeType.toValue());

		write.run();

		bindingBean.setType(new SimpleArooaClass(Person.class));

		DataReadJob read = new DataReadJob();
		read.setInput(new ByteArrayInputStream(output.toByteArray()));
		read.setPlan(null);
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

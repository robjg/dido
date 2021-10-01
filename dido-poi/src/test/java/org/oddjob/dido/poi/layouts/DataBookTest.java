package org.oddjob.dido.poi.layouts;

import dido.data.GenericData;
import dido.oddjob.bean.FromBeanArooa;
import dido.oddjob.bean.ToBeanArooa;
import dido.oddjob.beanbus.DataInDriver;
import dido.oddjob.beanbus.DataOutDestination;
import dido.poi.BookInProvider;
import dido.poi.BookOutProvider;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.OddjobSessionFactory;
import org.oddjob.OurDirs;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.types.ImportType;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.poi.data.PoiWorkbook;
import org.oddjob.dido.poi.test.Fruit;
import org.oddjob.dido.poi.test.Person;
import org.oddjob.dido.poi.test.PersonBonus;
import org.oddjob.state.ParentState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

public class DataBookTest extends TestCase {
	private static final Logger logger = Logger.getLogger(DataBookTest.class);

	File workDir;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		logger.info("-------------------   " + getName() + "   --------------");
		
		workDir = OurDirs.workPathDir(DataBookTest.class).toFile();
	}
	
	public void testWriteReadWithHeadings() throws Exception {
		
		doWriteRead("org/oddjob/dido/poi/DataBookWithHeadings.xml");
	}

	public void testWriteReadWithoutHeadings() throws Exception {
		
		doWriteRead("org/oddjob/dido/poi/DataBookWithoutHeadings.xml");
	}
	
	public void doWriteRead(String resource) throws Exception {
		
		ArooaSession session = new OddjobSessionFactory().createSession();
		
		List<Person> beans = new ArrayList<>();
		beans.add(new Person("John", DateHelper.parseDate("1970-03-25"), 45000.0));
		beans.add(new Person("Jane", DateHelper.parseDate("1982-11-14"), 28000.0));
		beans.add(new Person("Fred", DateHelper.parseDate("1986-08-07"), 22500.0));

		BeanBindingBean bindingBean = new BeanBindingBean();
		bindingBean.setArooaSession(session);
		bindingBean.setType(new SimpleArooaClass(Person.class));
		
		ImportType importType = new ImportType();
		importType.setArooaSession(new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader())));
		importType.setResource(resource);
		
		DataRows layout = (DataRows) importType.toObject();

		PoiWorkbook workbook = new PoiWorkbook();

		workbook.setOutput(new FileOutputStream(new File(workDir, "BookTest.xlsx")));
				
		DataOutDestination<String, BookOutProvider> write = new DataOutDestination<>();
		write.setArooaSession(session);
		write.setTo(new ArooaObject(workbook));
		write.setHow(layout);

		write.run();

		Function<Person, GenericData<String>> transformerFromBean =
				FromBeanArooa.fromSession(session)
				.ofClass(Person.class);

		beans.stream().map(transformerFromBean).forEach(write);

		write.close();

		// Read Side
		/////

		Function<GenericData<String>, PersonBonus> transformerToBean =
				ToBeanArooa.fromSession(session)
						.ofClass(PersonBonus.class);

		List<PersonBonus> results = new ArrayList<>(3);

		DataInDriver<String, BookInProvider> read =  new DataInDriver<>();
		read.setArooaSession(session);
		read.setFrom(new ArooaObject(workbook));
		read.setHow(layout);
		read.setTo(data -> results.add(transformerToBean.apply(data)));

		read.run();
		
		assertEquals(3, results.size());
		
		PersonBonus person1 = results.get(0);
		assertEquals("John", person1.getName());
		assertEquals(DateHelper.parseDate("1970-03-25"), person1.getDateOfBirth());
		assertEquals(45000.0, person1.getSalary());
		assertEquals(0.1, person1.getPercentage());
		assertEquals(4500.0, person1.getBonus());

		PersonBonus person2 = results.get(1);
		assertEquals("Jane", person2.getName());
		assertEquals(DateHelper.parseDate("1982-11-14"), person2.getDateOfBirth());
		assertEquals(28000.0, person2.getSalary());
		assertEquals(0.1, person2.getPercentage());
		assertEquals(2800.0, person2.getBonus());
		
		PersonBonus person3 =  results.get(2);
		assertEquals("Fred", person3.getName());
		assertEquals(DateHelper.parseDate("1986-08-07"), person3.getDateOfBirth());
		assertEquals(22500.0, person3.getSalary());
		assertEquals(0.1, person3.getPercentage());
		assertEquals(2250.0, person3.getBonus());
	}
	
	public void testNoData() throws Exception {
		
		List<Object> beans = new ArrayList<Object>();

		ArooaSession session = new StandardArooaSession(
				new ClassPathDescriptorFactory(
				).createDescriptor(getClass().getClassLoader()));

		ImportType importType = new ImportType();
		importType.setArooaSession(session);
		importType.setResource("org/oddjob/dido/poi/DataBookWithHeadings.xml");
		
		DataRows layout = (DataRows) importType.toObject();
		

		PoiWorkbook workbook = new PoiWorkbook();

		workbook.setOutput(new FileOutputStream(
				new File(workDir, "NoDataBookTest.xlsx")));
		
		DataOutDestination<String, BookOutProvider> write = new DataOutDestination<>();
		write.setArooaSession(session);
		write.setHow(layout);
		write.setTo(new ArooaObject(workbook));
		
		write.run();
		write.close();

		// Read Side
		/////

		List<GenericData<String>> results = new ArrayList<>(3);

		DataInDriver<String, BookInProvider> read =  new DataInDriver<>();
		read.setArooaSession(session);
		read.setFrom(new ArooaObject(workbook));
		read.setHow(layout);
		read.setTo(results::add);

		read.run();
		
		assertEquals(0, results.size());
	}
	
	public void testDataBookWithHeadingsInOddjob() {
		
		Properties properties = new Properties();
		properties.setProperty("work.dir", workDir + "/");
		properties.setProperty("layout.resource", 
				"org/oddjob/dido/poi/DataBookWithHeadings.xml");
		
		String config = getClass().getResource("OddjobWrite.xml").getFile();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setProperties(properties);
		oddjob.setFile(new File(config));
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());

	}
	
	public void testSimpleWriteReadExample() throws ArooaPropertyException, ArooaConversionException, ParseException, IOException {
		
		Properties properties = new Properties();
		properties.setProperty("work.dir", OurDirs.workPathDir(DataBookTest.class).toString());
		
		File file = new File(getClass().getResource(
				"DataBookWriteReadExample1.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		oddjob.setProperties(properties);
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		@SuppressWarnings("unchecked")
		List<Fruit> fruits = lookup.lookup("data-book-read.beans",
				List.class);
		
		Fruit fruit3 = fruits.get(2);
		
		assertEquals("Orange", fruit3.getFruit());
		assertEquals(DateHelper.parseDate("2013-12-05"), fruit3.getBestBefore());
		assertEquals("", fruit3.getColour());
		assertEquals(5, fruit3.getQuantity());
		assertEquals(215.0, fruit3.getPrice());
		
		assertEquals(5, fruits.size());
		
		oddjob.destroy();
	}
}

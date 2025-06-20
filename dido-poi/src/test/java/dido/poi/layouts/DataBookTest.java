package dido.poi.layouts;

import dido.data.DidoData;
import dido.oddjob.bean.FromBeanArooa;
import dido.oddjob.bean.ToBeanArooa;
import dido.oddjob.beanbus.DataInDriver;
import dido.oddjob.beanbus.DataOutDestination;
import dido.operators.transform.FieldViews;
import dido.operators.transform.TransformationFactory;
import dido.poi.BookInProvider;
import dido.poi.BookOutProvider;
import dido.poi.data.PoiWorkbook;
import dido.poi.test.Fruit;
import dido.poi.test.Person;
import dido.poi.test.PersonBonus;
import dido.test.OurDirs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.OddjobSessionFactory;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.types.ImportType;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.state.ParentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataBookTest {

	private static final Logger logger = LoggerFactory.getLogger(DataBookTest.class);

	File workDir;
	
	@BeforeEach
	protected void setUp(TestInfo testInfo) throws Exception {

        logger.info("-------------------   {}   --------------", testInfo.getDisplayName());
		
		workDir = OurDirs.workPathDir(DataBookTest.class).toFile();
	}

	@Test
	public void testWriteReadWithHeadings() throws Exception {
		
		doWriteRead("dido/poi/DataBookWithHeadings.xml");
	}

	@Test
	public void testWriteReadWithoutHeadings() throws Exception {
		
		doWriteRead("dido/poi/DataBookWithoutHeadings.xml");
	}
	
	public void doWriteRead(String resource) throws Exception {
		
		ArooaSession session = new OddjobSessionFactory().createSession();
		
		List<Person> beans = new ArrayList<>();
		beans.add(new Person("John", DateHelper.parseDate("1970-03-25"), 45000.0));
		beans.add(new Person("Jane", DateHelper.parseDate("1982-11-14"), 28000.0));
		beans.add(new Person("Fred", DateHelper.parseDate("1986-08-07"), 22500.0));

		ImportType importType = new ImportType();
		importType.setArooaSession(new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader())));
		importType.setResource(resource);
		
		DataRows layout = (DataRows) importType.toObject();

		PoiWorkbook workbook = new PoiWorkbook();

		workbook.setOutput(new FileOutputStream(new File(workDir, "BookTest.xlsx")));
				
		DataOutDestination<BookOutProvider> write = new DataOutDestination<>();
		write.setArooaSession(session);
		write.setTo(new ArooaObject(workbook));
		write.setHow(layout);

		write.run();

		Function<Person, DidoData> transformerFromBean =
				FromBeanArooa.fromSession(session)
				.ofClass(Person.class);

		TransformationFactory transformationFactory = new TransformationFactory();
		transformationFactory.setWithCopy(true);
		transformationFactory.setOf(0, FieldViews.setNamed("percentage", 0.1).asFieldWrite());

		beans.stream()
				.map(transformerFromBean)
				.map(transformationFactory.get())
				.forEach(write);

		write.close();

		// Read Side
		/////

		Function<DidoData, PersonBonus> transformerToBean =
				ToBeanArooa.fromSession(session)
						.ofClass(PersonBonus.class);

		List<PersonBonus> results = new ArrayList<>(3);

		DataInDriver<BookInProvider> read =  new DataInDriver<>();
		read.setArooaSession(session);
		read.setFrom(new ArooaObject(workbook));
		read.setHow(layout);
		read.setTo(data -> results.add(transformerToBean.apply(data)));

		read.run();

		assertThat(results.size(), is(3));
		
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

	@Test
	public void testNoData() throws Exception {
		
		ArooaSession session = new StandardArooaSession(
				new ClassPathDescriptorFactory(
				).createDescriptor(getClass().getClassLoader()));

		ImportType importType = new ImportType();
		importType.setArooaSession(session);
		importType.setResource("dido/poi/DataBookWithHeadings.xml");
		
		DataRows layout = (DataRows) importType.toObject();
		

		PoiWorkbook workbook = new PoiWorkbook();

		workbook.setOutput(new FileOutputStream(
				new File(workDir, "NoDataBookTest.xlsx")));
		
		DataOutDestination<BookOutProvider> write = new DataOutDestination<>();
		write.setArooaSession(session);
		write.setHow(layout);
		write.setTo(new ArooaObject(workbook));
		
		write.run();
		write.close();

		// Read Side
		/////

		List<DidoData> results = new ArrayList<>(3);

		DataInDriver<BookInProvider> read =  new DataInDriver<>();
		read.setArooaSession(session);
		read.setFrom(new ArooaObject(workbook));
		read.setHow(layout);
		read.setTo(results::add);

		read.run();
		
		assertEquals(0, results.size());
	}

	@Test
	public void testDataBookWithHeadingsInOddjob() {
		
		Properties properties = new Properties();
		properties.setProperty("work.dir", workDir + "/");
		properties.setProperty("layout.resource", 
				"dido/poi/DataBookWithHeadings.xml");
		
		String config = Objects.requireNonNull(getClass().getResource("OddjobWrite.xml"))
				.getFile();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setProperties(properties);
		oddjob.setFile(new File(config));
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());

	}

	@Test
	public void testSimpleWriteReadExample() throws ArooaPropertyException, ArooaConversionException, IOException, ParseException {
		
		Properties properties = new Properties();
		properties.setProperty("work.dir", OurDirs.workPathDir(DataBookTest.class).toString());
		
		File file = new File(Objects.requireNonNull(getClass().getResource(
				"DataBookWriteReadExample1.xml")).getFile());
		
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
		assertEquals(LocalDate.parse("2013-12-05"), fruit3.getBestBefore().toLocalDate());
		assertEquals("", fruit3.getColour());
		assertEquals(5, fruit3.getQuantity());
		assertEquals(215.0, fruit3.getPrice());
		
		assertEquals(5, fruits.size());
		
		oddjob.destroy();
	}
}

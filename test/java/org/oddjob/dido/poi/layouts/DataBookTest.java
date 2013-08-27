package org.oddjob.dido.poi.layouts;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.Oddjob;
import org.oddjob.OddjobSessionFactory;
import org.oddjob.OurDirs;
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
import org.oddjob.dido.poi.data.PoiWorkbook;
import org.oddjob.dido.poi.test.Person;
import org.oddjob.dido.poi.test.PersonBonus;
import org.oddjob.io.FileType;
import org.oddjob.state.ParentState;

public class DataBookTest extends TestCase {
	private static final Logger logger = Logger.getLogger(DataBookTest.class);

	File workDir;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		logger.info("-------------------   " + getName() + "   --------------");
		
		workDir = new OurDirs().relative("work");
	}
	
	public void testWriteReadWithHeadings() throws ParseException, ArooaConversionException, IOException {
		
		doWriteRead("org/oddjob/dido/poi/DataBookWithHeadings.xml");
	}

	public void testWriteReadWithoutHeadings() throws ParseException, ArooaConversionException, IOException {
		
		doWriteRead("org/oddjob/dido/poi/DataBookWithoutHeadings.xml");
	}
	
	public void doWriteRead(String resource) throws ParseException, ArooaConversionException, IOException {
		
		ArooaSession session = new OddjobSessionFactory().createSession();
		
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

		PoiWorkbook workbook = new PoiWorkbook();
		workbook.setArooaSession(session);
		
		FileType file = new FileType();
		file.setFile(new File(workDir, "BookTest.xlsx"));
		
		workbook.setOutput(file);
				
		DataWriteJob write = new DataWriteJob();
		write.setLayout(layout);
		write.setBeans(beans);
		write.setBindings("person", bindingBean);
		write.setBindings("percentage", valueBinding);
		
		write.setData(workbook);
		
		write.run();
		
		// Read Side
		/////
		
		bindingBean.setType(new SimpleArooaClass(PersonBonus.class));
		
		DataReadJob read =  new DataReadJob();
		read.setData(workbook);
		read.setLayout(layout);
		read.setBindings("person", bindingBean);
		read.setBeans(new ArrayList<Object>());
		
		read.run();
		
		Object[] results = read.getBeans().toArray();

		assertEquals(3, results.length);
		
		PersonBonus person1 = (PersonBonus) results[0];
		assertEquals("John", person1.getName());
		assertEquals(DateHelper.parseDate("1970-03-25"), person1.getDateOfBirth());
		assertEquals(45000.0, person1.getSalary());
		assertEquals(0.1, person1.getPercentage());
		assertEquals(10.0, person1.getBonus());

		PersonBonus person2 = (PersonBonus) results[1];
		assertEquals("Jane", person2.getName());
		assertEquals(DateHelper.parseDate("1982-11-14"), person2.getDateOfBirth());
		assertEquals(28000.0, person2.getSalary());
		assertEquals(0.1, person2.getPercentage());
		assertEquals(10.0, person2.getBonus());
		
		PersonBonus person3 = (PersonBonus) results[2];
		assertEquals("Fred", person3.getName());
		assertEquals(DateHelper.parseDate("1986-08-07"), person3.getDateOfBirth());
		assertEquals(22500.0, person3.getSalary());
		assertEquals(0.1, person3.getPercentage());
		assertEquals(10.0, person3.getBonus());
		
		assertEquals(3, results.length);
	}
	
	public void testNoData() throws ParseException, ArooaConversionException, IOException {
		
		ArooaSession session = new OddjobSessionFactory().createSession();
		
		List<Object> beans = new ArrayList<Object>();

		BeanBindingBean bindingBean = new BeanBindingBean();
		bindingBean.setArooaSession(session);
		bindingBean.setType(new SimpleArooaClass(Person.class));
		
		ImportType importType = new ImportType();
		importType.setArooaSession(new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader())));
		importType.setResource("org/oddjob/dido/poi/DataBookWithHeadings.xml");
		
		Layout layout = (Layout) importType.toObject();
		

		PoiWorkbook workbook = new PoiWorkbook();
		workbook.setArooaSession(session);
		
		FileType file = new FileType();
		file.setFile(new File(workDir, "BookTest.xlsx"));
		
		workbook.setOutput(file);
		
		DataWriteJob write = new DataWriteJob();		
		write.setLayout(layout);
		write.setBeans(beans);
		write.setBindings("person", bindingBean);
		
		write.setData(workbook);
		
		write.run();
		
		// Read Side
		/////
		
		bindingBean.setType(new SimpleArooaClass(PersonBonus.class));
		
		DataReadJob read =  new DataReadJob();
		read.setData(workbook);
		read.setLayout(layout);
		read.setBindings("person", bindingBean);
		read.setBeans(new ArrayList<Object>());
		
		read.run();
		
		Object[] results = read.getBeans().toArray();
				
		assertEquals(0, results.length);
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
}

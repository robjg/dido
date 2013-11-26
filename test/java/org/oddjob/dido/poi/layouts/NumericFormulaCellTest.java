package org.oddjob.dido.poi.layouts;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.poi.data.PoiWorkbook;

public class NumericFormulaCellTest extends TestCase {

	public void testReadWrite() throws DataException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		NumericFormulaCell test = new NumericFormulaCell();
		test.setArooaSession(new StandardArooaSession());
		test.setFormula("2 + 2");
				
		DataRows rows = new DataRows();
		rows.setOf(0, test);
		
		DataBook book = new DataBook();		
		book.setOf(0, rows);
		
		test.setBinding(new DirectBinding());
		
		DataWriter writer = book.writerFor(workbook);
		
		writer.write(new Object());
		
		assertEquals(1, test.getIndex());
		
		writer.close();
		
		// read side.
		
		book.reset();
		
		DataReader reader = book.readerFor(workbook);
		
		Object result = reader.read();
		
		assertEquals(new Double(4), result);
		
		assertEquals(null, reader.read());
		
		reader.close();
	}
	
	/**
	 * This was tracking down a weird feature where the 
	 * {@link FormulaCell} wasn't public and so the formula property 
	 * couldn't be seen. However this had worked on my laptop. 
	 * Different version of java maybe? 
	 */
	public void testFormulaType() {
		
		ClassPathDescriptorFactory descriptorFactory = 
			new ClassPathDescriptorFactory();
		
		ArooaDescriptor descriptor = 
			descriptorFactory.createDescriptor(getClass().getClassLoader());
		
		StandardArooaSession session = new StandardArooaSession(descriptor);

		PropertyAccessor propertyAccessor = 
			session.getTools().getPropertyAccessor(); 
		
		BeanOverview overview = propertyAccessor.getBeanOverview(
				NumericFormulaCell.class);
		
		Set<String> properties = new HashSet<String>(
				Arrays.asList(overview.getProperties()));
		
		assertTrue(properties.contains("formula"));
		
		assertTrue(overview.hasWriteableProperty("formula"));
		
		ArooaBeanDescriptor beanDescriptor = 
			session.getArooaDescriptor().getBeanDescriptor(
				new SimpleArooaClass(NumericFormulaCell.class), 
				propertyAccessor);
		
		assertEquals(ConfiguredHow.HIDDEN, 
				beanDescriptor.getConfiguredHow("arooaSession"));
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				beanDescriptor.getConfiguredHow("name"));
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				beanDescriptor.getConfiguredHow("label"));
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				beanDescriptor.getConfiguredHow("style"));
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				beanDescriptor.getConfiguredHow("formula"));
	}
}

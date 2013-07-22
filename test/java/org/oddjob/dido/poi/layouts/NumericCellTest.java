package org.oddjob.dido.poi.layouts;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.poi.SheetIn;
import org.oddjob.dido.poi.SheetOut;
import org.oddjob.dido.poi.data.PoiSheetIn;
import org.oddjob.dido.poi.data.PoiSheetOut;
import org.oddjob.dido.poi.layouts.NumericCell;

public class NumericCellTest extends TestCase {
	private static final Logger logger = Logger.getLogger(NumericCellTest.class);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		logger.info("-------------------------   " + getName()  + 
				"   ----------------------");
	}
	
	public void testReadWrite() throws DataException {
		
		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
		
		NumericCell test1 = new NumericCell();
		test1.setArooaSession(new StandardArooaSession());
		test1.bind(new DirectBinding());
		
		SheetOut out = new PoiSheetOut(sheet);
		out.nextRow();
		
		DataWriter writer = test1.writerFor(out); 
		
		writer.write(12.3);
		
		assertEquals(0, test1.getIndex());
		
		NumericCell test2 = new NumericCell();
		test2.setArooaSession(new StandardArooaSession());
		test2.bind(new DirectBinding());

		SheetIn in = new PoiSheetIn(sheet);
		assertTrue(in.nextRow());

		DataReader reader = test2.readerFor(in);

		Object result = reader.read();
		
		assertEquals(new Double(12.3), result);
	}
	
	/**
	 * Tracking down a really weird problem where tests from and failed to
	 * recognise a that value was an attribute but test from eclipse worked
	 * fine.
	 * <p>
	 * All to do with a Jave bug. See {@link ValueNode}.
	 * 
	 * @throws IntrospectionException 
	 */
	public void testValueConfiguredHow() throws IntrospectionException {
		
		logger.info("java.version=" + System.getProperty("java.version"));
		logger.info("java.specification.version=" + System.getProperty("java.specification.version"));
		logger.info("java.vm.version=" + System.getProperty("java.vm.version"));
		logger.info("java.home=" + System.getProperty("java.home"));
		
		BeanInfo beanInfo = Introspector.getBeanInfo(NumericCell.class);
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

		for (PropertyDescriptor descriptor : descriptors) {
			logger.info(descriptor.getPropertyType() + " " + descriptor.getName());
		}
		
		StandardArooaSession session = new StandardArooaSession();
		
		PropertyAccessor accessor = 
			session.getTools().getPropertyAccessor();
		
		ArooaClass arooaClass = new SimpleArooaClass(NumericCell.class);
		
		BeanOverview overview = arooaClass.getBeanOverview(accessor);
		
		assertEquals(Double.class, overview.getPropertyType("value"));
		
		ArooaBeanDescriptor descriptor = 			
			session.getArooaDescriptor().getBeanDescriptor(
				arooaClass, accessor);
		
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				descriptor.getConfiguredHow("value"));
	}
}

package org.oddjob.poi;

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

public class NumericCellTest extends TestCase {
	private static final Logger logger = Logger.getLogger(NumericCellTest.class);
	
	public void testReadWrite() throws DataException {
		
		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
		
		NumericCell test1 = new NumericCell();
		test1.setArooaSession(new StandardArooaSession());
		test1.setValue(12.3);
		
		SheetOut out = new PoiSheetOut(sheet);
		out.nextRow();
		
		test1.begin(out);
		test1.out(out);
		test1.end(out);
		
		assertEquals(0, test1.getColumn());
		
		NumericCell test2 = new NumericCell();
		test2.setArooaSession(new StandardArooaSession());

		SheetIn in = new PoiSheetIn(sheet);
		assertTrue(in.nextRow());
		
		test2.begin(in);
		test2.in(in);
		test2.end(in);
		
		assertEquals(new Double(12.3), test2.getValue());
	}
	
	/**
	 * Tracking down a really weird problem where tests from and failed to
	 * recognise a that value was an attribute but test from eclipse worked
	 * fine.
	 * <p>
	 * All to do with a jave bug. See Stencil.
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

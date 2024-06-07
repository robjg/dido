package dido.poi.layouts;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.SchemaBuilder;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.poi.data.PoiWorkbook;
import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class NumericCellTest extends TestCase {

	private static final Logger logger = LoggerFactory.getLogger(NumericCellTest.class);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		logger.info("-------------------------   " + getName()  + 
				"   ----------------------");
	}
	
	public void testWriteAndRead() throws Exception {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		NumericCell<Double> test = new NumericCell<>();

		DataRows rows = new DataRows();
		rows.setOf(0, test);

		DataOut writer = rows.outTo(workbook);
		
		writer.accept(ArrayData.of(12.3));
		
		writer.close();
		
		// Read side.
		
		DataIn reader = rows.inFrom(workbook);

		GenericData<String> result = reader.get();
		
		assertThat(result.getDoubleAt(1), is(12.3));
		
		assertNull(reader.get());
		
		reader.close();
	}

	public void testWriteAndReadOtherNumericTypes() throws Exception {

		PoiWorkbook workbook = new PoiWorkbook();

		NumericCell<Integer> test = new NumericCell<>();
		test.setType(int.class);

		DataRows rows = new DataRows();
		rows.setOf(0, test);

		DataOut writer = rows.outTo(workbook);

		writer.accept(ArrayData.of(12));

		writer.close();

		// Read side.

		DataIn reader = rows.inFrom(workbook);

		GenericData<String> result = reader.get();

		assertThat(result.getAtAs(1, Integer.class), is(12));

		assertNull(reader.get());

		reader.close();
	}

	public void testWriteAndReadNull() throws Exception {

		PoiWorkbook workbook = new PoiWorkbook();

		DataSchema<String> schema = SchemaBuilder.forStringFields()
				.add(Double.class)
				.build();

		DataRows rows = new DataRows();
		rows.setSchema(schema);

		DataOut writer = rows.outTo(workbook);

		writer.accept(ArrayData.of((Double) null));

		writer.close();

		// Read side.

		DataIn reader = rows.inFrom(workbook);

		GenericData<String> result = reader.get();

		assertThat(result.getAtAs(1, Double.class), nullValue());
		assertThat(result.hasIndex(1), is(false));

		try {
			result.getDoubleAt(1);
			MatcherAssert.assertThat("Should throw NPE", false);
		} catch (NullPointerException e) {
			// expected.
		}

		assertNull(reader.get());

		reader.close();
	}


	public static class ParameterisedBean<T> {
		
		public T getFoo() {
			return null;
		}
		
		public void setFoo(T foo) {
			
		}
	}
	
	public static class StringBean extends ParameterisedBean<String> {
		
		public String getFoo() {
			return null;
		}
		
		public void setFoo(String foo) {
			
		}
	}
	
	/**
	 * Tracking down a really weird problem where tests from Ant failed to
	 * recognise a that value was an attribute but test from eclipse worked
	 * fine.
	 * <p>
	 * All to do with <a href="http://bugs.sun.com/view_bug.do?bug-id=6528714">this bug</a>.
	 * 
	 * @throws IntrospectionException 
	 */
	public void testValueConfiguredHow() throws IntrospectionException {
		
		String javaVersion = System.getProperty("java.version");
		
		logger.info("java.version=" + javaVersion);
		logger.info("java.specification.version=" + System.getProperty("java.specification.version"));
		logger.info("java.vm.version=" + System.getProperty("java.vm.version"));
		logger.info("java.home=" + System.getProperty("java.home"));
		
		BeanInfo beanInfo = Introspector.getBeanInfo(StringBean.class);
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

		for (PropertyDescriptor descriptor : descriptors) {
			logger.info(descriptor.getPropertyType() + " " + descriptor.getName());
		}
		
		StandardArooaSession session = new StandardArooaSession();
		
		PropertyAccessor accessor = 
			session.getTools().getPropertyAccessor();
		
		ArooaClass arooaClass = new SimpleArooaClass(StringBean.class);
		
		BeanOverview overview = arooaClass.getBeanOverview(accessor);
		
		ArooaBeanDescriptor descriptor = 			
				session.getArooaDescriptor().getBeanDescriptor(
					arooaClass, accessor);
			
		// Looks like this bug is fixed in 1.7!
		if ("1.7".compareTo(javaVersion) > 0) {
			assertEquals(Object.class, overview.getPropertyType("foo"));
			
			assertEquals(ConfiguredHow.ELEMENT, 
					descriptor.getConfiguredHow("foo"));
		}
		else {
			assertEquals(String.class, overview.getPropertyType("foo"));
			
			assertEquals(ConfiguredHow.ATTRIBUTE, 
					descriptor.getConfiguredHow("foo"));
		}
	}
}

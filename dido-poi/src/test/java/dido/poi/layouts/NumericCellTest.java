package dido.poi.layouts;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.NamedData;
import dido.data.SchemaBuilder;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.poi.data.PoiWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NumericCellTest {

	private static final Logger logger = LoggerFactory.getLogger(NumericCellTest.class);
	
	@BeforeEach
	protected void setUp(TestInfo testInfo) throws Exception {

		logger.info("-------------------------   " + testInfo.getDisplayName()  +
				"   ----------------------");
	}

	@Test
	public void testWriteAndRead() throws Exception {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		NumericCell<Double> test = new NumericCell<>();

		DataRows rows = new DataRows();
		rows.setOf(0, test);

		DataOut writer = rows.outTo(workbook);
		
		writer.accept(ArrayData.of(12.3));
		
		writer.close();
		
		// Read side.
		
		DataIn<NamedData> reader = rows.inFrom(workbook);

		NamedData result = reader.get();
		
		assertThat(result.getDoubleAt(1), is(12.3));
		
		assertNull(reader.get());
		
		reader.close();
	}

	@Test
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

		DataIn<NamedData> reader = rows.inFrom(workbook);

		NamedData result = reader.get();

		assertThat(result.getAt(1), is(12));

		assertNull(reader.get());

		reader.close();
	}

	// Used to be null, but now all fields have names, the cell is created.
	@Test
	public void testWriteAndReadNull() throws Exception {

		PoiWorkbook workbook = new PoiWorkbook();

		DataSchema schema = SchemaBuilder.newInstance()
				.add(Double.class)
				.build();

		DataRows rows = new DataRows();
		rows.setSchema(schema);

		DataOut writer = rows.outTo(workbook);

		writer.accept(ArrayData.of((Double) null));

		writer.close();

		// Read side.

		DataIn<NamedData> reader = rows.inFrom(workbook);

		NamedData result = reader.get();

		assertThat(result.getAt(1), is(0.0));
		assertThat(result.hasIndex(1), is(true));

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
	@Test
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

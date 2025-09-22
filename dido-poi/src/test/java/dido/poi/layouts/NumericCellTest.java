package dido.poi.layouts;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.immutable.ArrayData;
import dido.data.immutable.SingleData;
import dido.data.schema.SchemaBuilder;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumericCellTest {

	private static final Logger logger = LoggerFactory.getLogger(NumericCellTest.class);
	
	@BeforeEach
	protected void setUp(TestInfo testInfo) {

        logger.info("-------------------------   {}   ----------------------",
				testInfo.getDisplayName());
	}

	@Test
	public void testWriteAndRead() {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		NumericCell test = new NumericCell();

		DataRows rows = new DataRows();
		rows.setOf(0, test);

		try (DataOut writer = rows.outTo(workbook)) {

			writer.accept(ArrayData.of(12.3));
		}

		// Read side.

		try (DataIn reader = rows.inFrom(workbook)) {

			List<DidoData> results = reader.stream()
					.collect(Collectors.toList());

			assertThat(results, contains(SingleData.type(Double.class).of(12.3)));
		}
	}

	@Test
	public void testWriteAndReadOtherNumericTypes() {

		DataSchema schema = SchemaBuilder.newInstance()
				.add(int.class)
				.build();

		PoiWorkbook workbook = new PoiWorkbook();

		NumericCell test = new NumericCell();

		DataRows rows = new DataRows();
		rows.setSchema(schema);
		rows.setOf(0, test);

		// Write

		try (DataOut writer = rows.outTo(workbook)) {

			writer.accept(ArrayData.of(12));
			writer.accept(ArrayData.of((Object) null));
			writer.accept(ArrayData.of(42.0));
		}

		// Read side.

		try (DataIn reader = rows.inFrom(workbook)) {

			List<DidoData> results = reader.stream()
					.collect(Collectors.toList());

			assertThat(results.get(0), is(SingleData.ofInt(12)));
			assertThat(results.get(1), is(SingleData.ofInt(0)));
			assertThat(results.get(2), is(SingleData.ofInt(42)));
		}

	}

	// Used to be null, but now all fields have names, the cell is created.
	@Test
	public void testWriteAndReadNull() {

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

		try (DataIn reader = rows.inFrom(workbook)) {

			List<DidoData> results = reader.stream()
					.collect(Collectors.toList());

			assertThat(results, contains(SingleData.type(Double.class).of(0.0)));
		}
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
			super.setFoo(foo);
		}
	}
	
	/**
	 * Tracking down a really weird problem where tests from Ant failed to
	 * recognise a that value was an attribute but test from eclipse worked
	 * fine.
	 * <p>
	 * All to do with <a href="http://bugs.sun.com/view_bug.do?bug-id=6528714">this bug</a>.
	 * 
	 * @throws IntrospectionException If we can't inspect the bean
	 */
	@Test
	public void testValueConfiguredHow() throws IntrospectionException {
		
		String javaVersion = System.getProperty("java.version");

        logger.info("java.version={}", javaVersion);
        logger.info("java.specification.version={}", System.getProperty("java.specification.version"));
        logger.info("java.vm.version={}", System.getProperty("java.vm.version"));
        logger.info("java.home={}", System.getProperty("java.home"));
		
		BeanInfo beanInfo = Introspector.getBeanInfo(StringBean.class);
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

		for (PropertyDescriptor descriptor : descriptors) {
            logger.info("{} {}", descriptor.getPropertyType(), descriptor.getName());
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

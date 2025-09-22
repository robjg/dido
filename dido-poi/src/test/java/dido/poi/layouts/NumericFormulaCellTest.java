package dido.poi.layouts;

import dido.data.DidoData;
import dido.data.immutable.ArrayData;
import dido.data.immutable.SingleData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.poi.data.PoiWorkbook;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaSession;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class NumericFormulaCellTest {

	@Test
	public void testReadWrite() {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		NumericFormulaCell test = new NumericFormulaCell();
		test.setFormula("2 + 2");
				
		DataRows rows = new DataRows();
		rows.setOf(0, test);

		DataOut writer = rows.outTo(workbook);

		writer.accept(ArrayData.of());

		writer.close();
		
		// read side.

		try (DataIn reader = rows.inFrom(workbook)) {

			List<DidoData> results = reader.stream()
					.collect(Collectors.toList());

			DidoData expected = SingleData.ofDouble(4.0);
			assertThat(results.get(0).getSchema(), is(expected.getSchema()));

			assertThat(results, contains(expected));
		}
	}
	
	/**
	 * This was tracking down a weird feature where the 
	 * {@link FormulaCell} wasn't public and so the formula property
	 * couldn't be seen. However, this had worked on my laptop.
	 * Different version of java maybe? 
	 */
	@Test
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
		
		Set<String> properties = new HashSet<>(
				Arrays.asList(overview.getProperties()));
		
		assertThat(properties.contains("formula"), is(true));
		
		assertThat(overview.hasWriteableProperty("formula"), is(true));
		
		ArooaBeanDescriptor beanDescriptor = 
			session.getArooaDescriptor().getBeanDescriptor(
				new SimpleArooaClass(NumericFormulaCell.class), 
				propertyAccessor);
		
		assertThat(beanDescriptor.getConfiguredHow("name"),
				is(ConfiguredHow.ATTRIBUTE));
		assertThat(beanDescriptor.getConfiguredHow("style"),
				is(ConfiguredHow.ATTRIBUTE));
		assertThat(beanDescriptor.getConfiguredHow("formula"),
				is(ConfiguredHow.ATTRIBUTE));
	}
}

package dido.poi.layouts;

import dido.data.DidoData;
import dido.how.DataIn;
import dido.poi.data.PoiWorkbook;
import dido.test.OurDirs;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.state.ParentState;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NullAndBlankCellsTest {
	
	public static class Fruit {
		
		private String fruit;
		
		private String colour;
		
		private Double quantity;

		public String getFruit() {
			return fruit;
		}

		public void setFruit(String fruit) {
			this.fruit = fruit;
		}

		public String getColour() {
			return colour;
		}

		public void setColour(String colour) {
			this.colour = colour;
		}

		public Double getQuantity() {
			return quantity;
		}

		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}
	}
	
	@Test
	public void testNullCells() throws Exception {

		TextCell column1 = new TextCell();
		
		NumericCell column2 = new NumericCell();
		
		DataRows rows = new DataRows();
		rows.setOf(0, column1);
		rows.setOf(1, column2);

		PoiWorkbook workbook = new PoiWorkbook();
		workbook.provideBookOut().getOrCreateSheet(null);

		try (DataIn reader = rows.inFrom(workbook)) {

			List<DidoData> results = reader.stream()
					.collect(Collectors.toList());

			assertThat(results, empty());
		}

	}

	@Disabled
	public void testWriteAndReadBlankCell() throws ArooaPropertyException, ArooaConversionException, IOException {
		if (true) {
			return;
		}

		File file = new File(getClass().getResource(
				"NullAndBlankCellsTest.xml").getFile());

		Properties properties = new Properties();
		properties.setProperty("work.dir", OurDirs.workPathDir(NullAndBlankCellsTest.class).toString());

		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		oddjob.setProperties(properties);
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		@SuppressWarnings("unchecked")
		List<Fruit> beans = (List<Fruit>) lookup.lookup("poi-read.beans", List.class);
		
		assertEquals("apple", beans.get(0).getFruit());
		assertEquals("red", beans.get(0).getColour());
		MatcherAssert.assertThat(beans.get(0).getQuantity(), Matchers.is(0.0));
		
		assertEquals("banana", beans.get(1).getFruit());
		assertEquals("", beans.get(1).getColour());
		MatcherAssert.assertThat(beans.get(1).getQuantity(), Matchers.is(17.0));
		
		assertEquals(2, beans.size());
		
		oddjob.destroy();
	}
}

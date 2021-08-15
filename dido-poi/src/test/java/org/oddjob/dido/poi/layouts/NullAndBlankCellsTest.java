package org.oddjob.dido.poi.layouts;

import junit.framework.TestCase;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.OurDirs;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.poi.SheetIn;
import org.oddjob.state.ParentState;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class NullAndBlankCellsTest extends TestCase {
	
	public static class Fruit {
		
		private String fruit;
		
		private String colour;
		
		private Integer quantity;

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

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
	}
	
	class OurSheetIn implements SheetIn {
		
		Sheet sheet;
		
		public OurSheetIn() {
			Workbook workbook = new XSSFWorkbook();
			sheet = workbook.createSheet();
			sheet.createRow(0);
		}
		
		@Override
		public Sheet getTheSheet() {
			return sheet;
		}
		
		@Override
		public <T extends DataIn> T provideDataIn(Class<T> type)
				throws DataException {
			if (type.isAssignableFrom(SheetIn.class)) {
				return type.cast(this);
			}
			throw new RuntimeException("Unexpected");
		}
	}
	
	public void nullCells() throws DataException {
		
		ArooaSession session = new StandardArooaSession();
		
		BeanBindingBean binding = new BeanBindingBean();
		binding.setArooaSession(session);
		binding.setType(new SimpleArooaClass(Fruit.class));
		
		TextCell column1 = new TextCell();
		
		NumericCell column2 = new NumericCell();
		
		DataRows rows = new DataRows();
		
		rows.setBinding(binding);
		rows.setOf(0, column1);
		rows.setOf(1, column2);
		
		DataReader reader = rows.readerFor(new OurSheetIn());
		
		Object result = reader.read();
		
		assertEquals(Fruit.class, result.getClass());
		
		assertNull(reader.read());
		
		reader.close();
	}
	
	
	public void testWriteAndReadBlankCell() throws ArooaPropertyException, ArooaConversionException, IOException {
		
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
		assertEquals(new Integer(0), beans.get(0).getQuantity());
		
		assertEquals("banana", beans.get(1).getFruit());
		assertEquals("", beans.get(1).getColour());
		assertEquals(new Integer(17), beans.get(1).getQuantity());
		
		assertEquals(2, beans.size());
		
		oddjob.destroy();
	}
	
}

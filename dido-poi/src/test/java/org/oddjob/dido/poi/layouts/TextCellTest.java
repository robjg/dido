package org.oddjob.dido.poi.layouts;

import dido.data.ArrayData;
import dido.how.DataIn;
import dido.how.DataOut;
import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.OurDirs;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.dido.poi.data.PoiWorkbook;
import org.oddjob.state.ParentState;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class TextCellTest extends TestCase {

	public void testWriteRead() throws Exception {

		TextCell test = new TextCell();
		test.setName("Fruit");
		test.setIndex(2);

		DataRows rows = new DataRows();
		rows.setOf(0, test);
		rows.setWithHeader(true);

		PoiWorkbook workbook = new PoiWorkbook();

		DataOut<String> out = rows.outTo(workbook);

		out.accept(ArrayData.of(null, "Apple"));
		out.accept(ArrayData.of(null, "Orange"));

		out.close();

		DataIn<String> in = rows.inFrom(workbook);

		MatcherAssert.assertThat(in.get().getStringAt(2), Matchers.is("Apple"));
		MatcherAssert.assertThat(in.get().getStringAt(2), Matchers.is("Orange"));
		MatcherAssert.assertThat(in.get(), Matchers. nullValue());
	}

	@Disabled
	@Ignore
	public void testWriteAndReadTextCellOfNamedValues() throws ArooaPropertyException, ArooaConversionException, IOException {

		if (true) {
			return;
		}

		File file = new File(getClass().getResource(
				"TextCellOfNamedValues.xml").getFile());

		Properties properties = new Properties();
		properties.setProperty("work.dir", OurDirs.workPathDir(TextCellTest.class).toString());

		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		oddjob.setProperties(properties);
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		String[] expected = lookup.lookup("text-read.data.input", String[].class);
		
		String[] result = lookup.lookup("text-write.data.output", String[].class);
		
		assertEquals(expected[0], result[0]);
		assertEquals(expected[1], result[1]);
		assertEquals(expected[2], result[2]);
		
		assertEquals(expected.length, result.length);
		
		oddjob.destroy();
	}

}

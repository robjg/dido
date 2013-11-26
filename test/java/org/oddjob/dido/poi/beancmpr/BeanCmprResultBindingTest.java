package org.oddjob.dido.poi.beancmpr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import junit.framework.TestCase;

import org.oddjob.OurDirs;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.poi.data.PoiWorkbook;
import org.oddjob.dido.poi.layouts.DataBook;
import org.oddjob.dido.poi.layouts.DataRows;

public class BeanCmprResultBindingTest extends TestCase {

	File workDir;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		workDir = new OurDirs().relative("work");
	}
	
	public void testsimple() throws DataException, FileNotFoundException {
		
		ArooaSession session = new StandardArooaSession();
		
		DataBook book = new DataBook();
		
		DataRows rows = new DataRows();
		rows.setWithHeadings(true);
		rows.setAutoFilter(true);
		rows.setAutoWidth(true);
		book.setOf(0, rows);
		
		BeanCmprResultBinding test = new BeanCmprResultBinding();
		test.setArooaSession(session);
		
		rows.setBinding(test);

		PoiWorkbook workbook = new PoiWorkbook();
		workbook.setArooaSession(session);
		workbook.setOutput(new ArooaObject(new FileOutputStream(
				new File(workDir, "BeanCmprResultsTest.xlsx"))));
		
		DataWriter writer = book.writerFor(workbook);
		
		writer.write(beanCmprDifferenceExample()); 
		writer.write(beanCmprXMissingExample()); 
		writer.write(beanCmprSameExample()); 
		writer.write(beanCmprYMissingExample()); 
		
		writer.close();
	}
	
	private Object beanCmprDifferenceExample() {
		
		FakeResultsBean.Builder builder = 
				new FakeResultsBean.Builder(3);
		
		builder.addKey("id", new Integer(10));
		builder.addKey("region", "UK");

		builder.addComparison("quantity", new Integer(15), 
				new Integer(16), -1);
		
		builder.addComparison("price", new Double(22.4), 
				new Float(22.4), 0);

		builder.addComparison("colour", null, 
				null, 0);
		
		return builder.build();
	}
	
	private Object beanCmprSameExample() {
		
		FakeResultsBean.Builder builder = 
				new FakeResultsBean.Builder(0);
		
		builder.addKey("id", new Integer(11));
		builder.addKey("region", "France");

		builder.addComparison("quantity", new Integer(12), 
				new Integer(12), 0);
		
		builder.addComparison("price", new Double(16.7), 
				new Float(16.7), 0);

		builder.addComparison("colour", "Yellow", "Yellow", 0);
		
		return builder.build();
	}

	private Object beanCmprXMissingExample() {
		
		FakeResultsBean.Builder builder = 
				new FakeResultsBean.Builder(1);
		
		builder.addKey("id", new Integer(14));
		builder.addKey("region", "Germany");

		builder.addComparison("quantity", null, 
				new Integer(97), -1);
		
		builder.addComparison("price", null, 
				new Float(18.5), -1);

		builder.addComparison("colour", null, "Blue", -1);
		
		return builder.build();
	}
	
	private Object beanCmprYMissingExample() {
		
		FakeResultsBean.Builder builder = 
				new FakeResultsBean.Builder(2);
		
		builder.addKey("id", new Integer(15));
		builder.addKey("region", "Ireland");

		builder.addComparison("quantity", 
				new Integer(25), null, -1);
		
		builder.addComparison("price",
				new Float(42.9), null, -1);

		builder.addComparison("colour", "Green", null, -1);
		
		return builder.build();
	}
}

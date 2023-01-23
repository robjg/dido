package org.oddjob.dido.poi.layouts;

import dido.data.GenericData;
import dido.data.MapData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.oddjob.beanbus.DataInDriver;
import dido.oddjob.beanbus.DataOutDestination;
import dido.poi.BookInProvider;
import dido.poi.BookOutProvider;
import dido.test.OurDirs;
import junit.framework.TestCase;
import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.types.ImportType;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.dido.poi.data.PoiWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class QuickRowsTest extends TestCase {

    File workDir;

    private static final Logger logger = LoggerFactory.getLogger(QuickRowsTest.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        logger.info("----------------------------    " + getName() +
                "   -------------------------");

        workDir = OurDirs.workPathDir(QuickRowsTest.class).toFile();
    }

    public static class Person {

        private String name;
        private Date dateOfBirth;
        private Double salary;

        public Person(String name, Date dateOfBirth, Double salery) {
            this.name = name;
            this.dateOfBirth = dateOfBirth;
            this.salary = salery;
        }

        public Person() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(Date dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public Double getSalary() {
            return salary;
        }

        public void setSalary(Double salery) {
            this.salary = salery;
        }

        @Override
        public String toString() {
            return "Person: name=" + name + ", dateOfBirth=" +
                    dateOfBirth + ", salary=" + salary;
        }
    }

    public void testIdea() throws Exception {

        DataRows test = new DataRows();
        test.setWithHeader(true);

        PoiWorkbook workbook = new PoiWorkbook();

        GenericData<String> person = MapData.newBuilderNoSchema()
                .setString("name", "John")
                .set("dateOfBirth", DateHelper.parseDate("1970-03-25"))
                .setDouble("salary", 45000.0)
                .build();

        DataOut<String> writer = test.outTo(workbook);

        writer.accept(person);

        writer.close();

        Sheet sheet = workbook.getWorkbook().getSheetAt(0);

        assertEquals(1, sheet.getLastRowNum());
        assertEquals(3, sheet.getRow(1).getLastCellNum());

        assertEquals("name", sheet.getRow(0).getCell(0).toString());
        assertEquals("dateOfBirth", sheet.getRow(0).getCell(1).toString());
        assertEquals("salary", sheet.getRow(0).getCell(2).toString());
        assertEquals("John", sheet.getRow(1).getCell(0).toString());
        assertEquals("25-Mar-1970", sheet.getRow(1).getCell(1).toString());
        assertEquals("45000.0", sheet.getRow(1).getCell(2).toString());

        DataIn<String> reader = test.inFrom(workbook);

        GenericData<String> result = reader.get();

        assertEquals("John", result.getString("name"));
        assertThat(result.getAs("dateOfBirth", Date.class), is(DateHelper.parseDate("1970-03-25")));
        assertEquals(45000.0, result.getDouble("salary"));
    }

    public void testWriteReadWithHeadings() throws Exception {

        doWriteRead("org/oddjob/dido/poi/QuickRowsWithHeadings.xml");
    }

    public void doWriteRead(String resource) throws Exception {

        ArooaSession session = new StandardArooaSession(
                new ClassPathDescriptorFactory(
                ).createDescriptor(getClass().getClassLoader()));

        List<GenericData<String>> beans = new ArrayList<>();
        beans.add(MapData.newBuilderNoSchema()
                .setString("name", "John")
                .set("dateOfBirth", DateHelper.parseDate("1970-03-25"))
                .setDouble("salary", 45000.0)
                .build());
        beans.add(MapData.newBuilderNoSchema()
                .setString("name", "Jane")
                .set("dateOfBirth", DateHelper.parseDate("1982-11-14"))
                .setDouble("salary", 28000.0)
                .build());
        beans.add(MapData.newBuilderNoSchema()
                .setString("name", "Fred")
                .set("dateOfBirth", DateHelper.parseDate("1986-08-07"))
                .setDouble("salary", 22500.0)
                .build());

        PoiWorkbook workbook = new PoiWorkbook();

        workbook.setOutput(
                new FileOutputStream(new File(workDir,
                        "QuickRowsTest.xlsx")));

        ImportType importType = new ImportType();
        importType.setArooaSession(session);
        importType.setResource(resource);

        DataRows layout = (DataRows) importType.toObject();

        DataOutDestination<String, BookOutProvider> write = new DataOutDestination<>();
        write.setHow(layout);
        write.setArooaSession(session);
        write.setTo(new ArooaObject(workbook));

        write.run();

        beans.forEach(write);

        write.close();

        // Read Side
        ////

        List<GenericData<String>> results = new ArrayList<>(3);

        DataInDriver<String, BookInProvider> read = new DataInDriver<>();
        read.setArooaSession(session);
        read.setFrom(new ArooaObject(workbook));
        read.setHow(layout);
        read.setTo(results::add);
        read.run();

        read.close();

        GenericData<String> person1 = results.get(0);
        assertEquals("John", person1.getString("name"));
        assertEquals(DateHelper.parseDate("1970-03-25"),
                person1.get("dateOfBirth"));
        assertEquals(45000.0, person1.getDouble("salary"));

        GenericData<String> person2 = results.get(1);
        assertEquals("Jane", person2.getString("name"));
        assertEquals(DateHelper.parseDate("1982-11-14"),
                person2.get("dateOfBirth"));
        assertEquals(28000.0, person2.getDouble("salary"));

        GenericData<String> person3 = results.get(2);
        assertEquals("Fred", person3.getString("name"));
        assertEquals(DateHelper.parseDate("1986-08-07"),
                person3.get("dateOfBirth"));
        assertEquals(22500.0, person3.getDouble("salary"));

        assertEquals(3, results.size());
    }
}

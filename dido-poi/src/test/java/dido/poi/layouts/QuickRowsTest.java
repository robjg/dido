package dido.poi.layouts;

import dido.data.DidoData;
import dido.data.MapData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.how.conversion.DefaultConversionProvider;
import dido.oddjob.beanbus.DataInDriver;
import dido.oddjob.beanbus.DataOutDestination;
import dido.poi.BookInProvider;
import dido.poi.BookOutProvider;
import dido.poi.data.PoiWorkbook;
import dido.test.OurDirs;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.types.ImportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QuickRowsTest {

    File workDir;

    private static final Logger logger = LoggerFactory.getLogger(QuickRowsTest.class);

    @BeforeEach
    protected void setUp(TestInfo testInfo) throws IOException {

        logger.info("----------------------------    {}   -------------------------", testInfo.getDisplayName());

        workDir = OurDirs.workPathDir(QuickRowsTest.class).toFile();
    }

    public static class Person {

        private String name;
        private LocalDateTime dateOfBirth;
        private Double salary;

        public Person(String name, LocalDateTime dateOfBirth, Double salery) {
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

        public LocalDateTime getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(LocalDateTime dateOfBirth) {
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

    @Test
    void testIdea() {

        DataRows test = new DataRows();
        test.setWithHeader(true);
        test.setConversionProvider(DefaultConversionProvider.defaultInstance());

        PoiWorkbook workbook = new PoiWorkbook();

        DidoData person = MapData.builder()
                .withString("name", "John")
                .with("dateOfBirth", LocalDateTime.parse("1970-03-25T00:00:00"))
                .withDouble("salary", 45000.0)
                .build();

        try (DataOut writer = test.outTo(workbook)) {

            writer.accept(person);

        }

        Sheet sheet = workbook.getWorkbook().getSheetAt(0);

        assertEquals(1, sheet.getLastRowNum());
        assertEquals(3, sheet.getRow(1).getLastCellNum());

        assertEquals("name", sheet.getRow(0).getCell(0).toString());
        assertEquals("dateOfBirth", sheet.getRow(0).getCell(1).toString());
        assertEquals("salary", sheet.getRow(0).getCell(2).toString());
        assertEquals("John", sheet.getRow(1).getCell(0).toString());
        assertEquals("25-Mar-1970", sheet.getRow(1).getCell(1).toString());
        assertEquals("45000.0", sheet.getRow(1).getCell(2).toString());

        List<DidoData> results;
        try (DataIn reader = test.inFrom(workbook)) {
            results = reader.stream().collect(Collectors.toList());
        }

        assertThat(results.size(), is(1));

        DidoData result = results.get(0);

        assertEquals("John", result.getStringNamed("name"));
        assertThat(result.getNamed("dateOfBirth"), is(LocalDateTime.parse("1970-03-25T00:00:00")));
        assertEquals(45000.0, result.getDoubleNamed("salary"));
    }

    @Test
    void testWriteReadWithHeadings() throws Exception {

        doWriteRead("dido/poi/QuickRowsWithHeadings.xml");
    }

    public void doWriteRead(String resource) throws Exception {

        ArooaSession session = new StandardArooaSession(
                new ClassPathDescriptorFactory(
                ).createDescriptor(getClass().getClassLoader()));

        List<DidoData> beans = new ArrayList<>();
        beans.add(MapData.builder()
                .withString("name", "John")
                .with("dateOfBirth", LocalDateTime.parse("1970-03-25T00:00:00"))
                .withDouble("salary", 45000.0)
                .build());
        beans.add(MapData.builder()
                .withString("name", "Jane")
                .with("dateOfBirth", LocalDateTime.parse("1982-11-14T00:00:00"))
                .withDouble("salary", 28000.0)
                .build());
        beans.add(MapData.builder()
                .withString("name", "Fred")
                .with("dateOfBirth", LocalDateTime.parse("1986-08-07T00:00:00"))
                .withDouble("salary", 22500.0)
                .build());

        PoiWorkbook workbook = new PoiWorkbook();

        workbook.setOutput(
                new FileOutputStream(new File(workDir,
                        "QuickRowsTest.xlsx")));

        ImportType importType = new ImportType();
        importType.setArooaSession(session);
        importType.setResource(resource);

        DataRows layout = (DataRows) importType.toObject();

        DataOutDestination<BookOutProvider> write = new DataOutDestination<>();
        write.setHow(layout);
        write.setArooaSession(session);
        write.setTo(new ArooaObject(workbook));

        write.run();

        beans.forEach(write);

        write.close();

        // Read Side
        ////

        List<DidoData> results = new ArrayList<>(3);

        DataInDriver<BookInProvider> read = new DataInDriver<>();
        read.setArooaSession(session);
        read.setFrom(new ArooaObject(workbook));
        read.setHow(layout);
        read.setTo(results::add);
        read.run();

        read.close();

        DidoData person1 = results.get(0);
        assertEquals("John", person1.getStringNamed("name"));
        assertEquals(LocalDateTime.parse("1970-03-25T00:00:00"),
                person1.getNamed("dateOfBirth"));
        assertEquals(45000.0, person1.getDoubleNamed("salary"));

        DidoData person2 = results.get(1);
        assertEquals("Jane", person2.getStringNamed("name"));
        assertEquals(LocalDateTime.parse("1982-11-14T00:00:00"),
                person2.getNamed("dateOfBirth"));
        assertEquals(28000.0, person2.getDoubleNamed("salary"));

        DidoData person3 = results.get(2);
        assertEquals("Fred", person3.getStringNamed("name"));
        assertEquals(LocalDateTime.parse("1986-08-07T00:00:00"),
                person3.getNamed("dateOfBirth"));
        assertEquals(22500.0, person3.getDoubleNamed("salary"));

        assertEquals(3, results.size());
    }
}

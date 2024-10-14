package dido.poi.layouts;

import dido.data.ArrayData;
import dido.data.DidoData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.poi.data.PoiWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.utils.DateHelper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@Disabled
class DateCellTest {

    @Disabled
    @Test
    void testWriteAndRead() throws Exception {

        PoiWorkbook workbook = new PoiWorkbook();

        DateCell test = new DateCell();

        DataRows rows = new DataRows();
        rows.setOf(0, test);

        DataOut writer = rows.outTo(workbook);

        writer.accept(ArrayData.of(DateHelper.parseDate("2021-09-22")));

        writer.close();

        // Check what we wrote

        Cell dobCell = workbook.getWorkbook().getSheetAt(0).getRow(0).getCell(0);
        assertThat(dobCell.getCellType(), is(CellType.NUMERIC));

        CellStyle cellStyle = dobCell.getCellStyle();
        assertThat(cellStyle.getDataFormatString(), is("d/m/yyyy"));

        assertThat(dobCell.toString(), is("22-Sep-2021"));

        // Read side.

        DataIn<DidoData> reader = rows.inFrom(workbook);

        DidoData result = reader.get();

        assertThat(result.getAt(1), is(DateHelper.parseDate("2021-09-22")));

        assertThat(reader.get(), nullValue());

        reader.close();
    }
}

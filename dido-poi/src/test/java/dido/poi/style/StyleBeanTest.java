package dido.poi.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class StyleBeanTest {

    @Test
    void testDateFormat() {

        StyleBean styleBean = new StyleBean();
        styleBean.setFormat(DefaultStyleProivderFactory.DATE_FORMAT);

        CellStyle cellStyle = styleBean.createStyle(new XSSFWorkbook());

        assertThat(cellStyle.getDataFormat(), is((short) 0xe));
    }
}
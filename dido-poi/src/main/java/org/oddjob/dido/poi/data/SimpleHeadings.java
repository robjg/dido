package org.oddjob.dido.poi.data;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.oddjob.dido.poi.RowsIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Manage Heading for {@link RowsIn}.
 *
 * @author rob
 */
class SimpleHeadings {
    private static final Logger logger = LoggerFactory.getLogger(SimpleHeadings.class);

    private final Map<String, Integer> headings;

    private final Map<Integer, String> headingsByColumn =
            new HashMap<>();

    private int columnCursor = 0;

    /**
     * Create a new Instance.
     *
     * @param row    The row with the headings on.
     * @param offset The column offset. 0 for none which means the first
     *               column will have a position of 1 but come from Poi column 0.
     */
    public SimpleHeadings(Row row, int offset) {

        headings = new LinkedHashMap<>();

        for (int columnPosition = 1; true; ++columnPosition) {

            Cell cell = row.getCell(offset + columnPosition - 1);

            if (cell == null) {
                break;
            }

            if (cell.getCellType() == CellType.BLANK) {
                break;
            }

            String title = cell.getStringCellValue();
            Integer existing = headings.get(title);

            if (existing != null) {
                logger.warn("Heading " + title + " is duplicated in columns " +
                        existing + " and " + columnPosition +
                        ". The first first column will be ignored.");
            }

            headings.put(title, columnPosition);
            headingsByColumn.put(columnPosition, title);
        }

        if (logger.isDebugEnabled()) {
            StringBuilder text = new StringBuilder();
            for (Map.Entry<String, Integer> entry : headings.entrySet()) {
                if (text.length() > 0) {
                    text.append(", ");
                }
                text.append(entry.getKey());
                text.append("=");
                text.append(entry.getValue());
            }
            logger.debug("Processed Headings: " + text);
        }
    }

    /**
     * Get the column position for the given heading. If heading is null
     * then the next column position is used.
     *
     * @param heading The heading.
     * @return The column position of the heading. 0 if the heading
     * isn't in the row.
     */
    public int position(String heading) {
        if (heading == null || this.headings == null) {
            return ++columnCursor;
        }
        Integer column = headings.get(heading);
        return Objects.requireNonNullElse(column, 0);
    }

    public String[] getHeadings() {
        int maxColumn = 0;
        for (Integer i : headingsByColumn.keySet() ) {
            int index = i;
            if (index > maxColumn) {
                maxColumn = index;
            }
        }

        String[] a = new String[maxColumn];

        for (int i = 0; i < maxColumn; ++i) {
            String thing = headingsByColumn.get(i + 1);
            a[i] = thing;
        }

        return a;

    }
}

package dido.poi.layouts;

import dido.data.DataSchema;
import dido.data.DidoData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * @oddjob.description Define a date column. Nests within a {@link DataRows}.
 *
 * @author rob
 */
public class DateCell extends AbstractDataCell<Date> {

	public static final String DEFAULT_DATE_STYLE = "date";

	private volatile Date value;

	@Override
	public Class<Date> getType() {
		return Date.class;
	}
	
	@Override
	public CellType getCellType() {
		return CellType.NUMERIC;
	}
	
	@Override
	public Date extractCellValue(Cell cell) {
		return cell.getDateCellValue();
	}

	@Override
	void insertValueInto(Cell cell, int index, DidoData data) {

		Date value = this.value;
		if (value == null) {

			DataSchema schema = data.getSchema();
			Class<?> dataType = schema.getTypeAt(index);

			if (Date.class.isAssignableFrom(dataType)) {
				value = (Date) data.getAt(index);
			}
			else if (String.class.isAssignableFrom(dataType)) {
				value = Optional.ofNullable(data.getStringAt(index))
						.map(s -> {
							try {
								return new SimpleDateFormat("yyyy-MM-dd").parse(s);
							} catch (ParseException e) {
								throw new IllegalArgumentException(s, e);
							}
						})
						.orElse(null);
			}
			else {
				throw new IllegalArgumentException("Can't extract Date from " + dataType);
			}
		}

		if (value == null) {
			cell.setBlank();
		}
		else {
			cell.setCellValue(value);
		}
	}
	
	@Override
	public String getDefaultStyle() {
		return DEFAULT_DATE_STYLE;
	}

	public Date getValue() {
		return value;
	}

	public void setValue(Date value) {
		this.value = value;
	}
}

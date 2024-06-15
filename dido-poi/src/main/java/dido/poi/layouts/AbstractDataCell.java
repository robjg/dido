package dido.poi.layouts;

import dido.data.DidoData;
import dido.poi.CellIn;
import dido.poi.CellOut;
import dido.poi.HeaderRowOut;
import dido.poi.RowOut;
import dido.poi.data.DataCell;
import dido.poi.data.PoiWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Shared implementation base class for cells.
 * 
 * @author rob
 *
 * @param <T> The type of the cell.
 */
abstract public class AbstractDataCell<T> implements DataCell<T> {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractDataCell.class);

	/**
	 * @oddjob.property
	 * @oddjob.description The name of this layout. The name is the main
	 * identification of a layout node. It is commonly used by bindings to
	 * associate with the property name of a Java Object.
	 * @oddjob.required No.
	 */
	private String name;

	/**
	 * @oddjob.property
	 * @oddjob.description The name of the style to use. The style will have
	 * been defined with the {@link PoiWorkbook} definition.
	 * @oddjob.required No.
	 */
	private String style;
		
	/**
	 * @oddjob.property
	 * @oddjob.description The 1 based column index of this layout.
	 * @oddjob.required Read only.
	 */
	private int index;

	/**
	 * @oddjob.property
	 * @oddjob.description The Excel reference of the last row of this
	 * column that has been written.
	 * @oddjob.required Read only.
	 */
	private String reference;

	@Override
	public CellIn<T> provideCellIn(int index) {

		return rowIn -> {

			Cell cell = rowIn.getCell(index);

			return extractCellValue(cell);
		};
	}

	@Override
	public CellOut<T> provideCellOut(int index) {

		String header = Optional.ofNullable(getName()).orElse("Unnamed");

		return new CellOut<>() {

			@Override
			public void writeHeader(HeaderRowOut headerRowOut) {
				headerRowOut.setHeader(index, header);
			}

			@Override
			public void setValue(RowOut rowOut, DidoData data) {

				Cell cell = rowOut.getCell(index, getCellType());

				String style = Optional.ofNullable(getStyle()).orElse(getDefaultStyle());

				if (style != null) {
					CellStyle cellStyle = rowOut.styleFor(style);

					if (cellStyle == null) {
						throw new IllegalArgumentException("No style available of name [" +
								style + "] from cell [" + this + "]");
					}

					cell.setCellStyle(cellStyle);
				}

				insertValueInto(cell, index, data);
			}
		};
	}

	/**
	 * Extract the value from the cell.
	 *
	 * @param cell The Excel Cell
	 * @return The extracted value. May be null.
	 */
	abstract T extractCellValue(Cell cell);

	/**
	 * Write a value into the cell.
	 *
	 * @param cell The Excel Cell.
	 * @param index The cell index.
	 * @param data   The data. May be null.
	 *
	 */
	abstract void insertValueInto(Cell cell, int index,  DidoData data);

	/**
	 * @oddjob.property cellType
	 * @oddjob.description The Excel type of this column.
	 * @oddjob.required Read only.
	 */
	abstract public CellType getCellType();


	/**
	 * Setter for name.
	 *
	 * @param name The optional name which will be the field name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * @oddjob.property defaultStyle
	 * @oddjob.description The default style for the cell. This
	 * maybe overridden at the {@link PoiWorkbook} level.
	 * @oddjob.required Read only.
	 * 
	 * @return The default style.
	 */
	public String getDefaultStyle() {
		return null;
	}

	/**
	 * @oddjob.property type
	 * @oddjob.description The Java type of the column.
	 * @oddjob.required Read only.
	 *
	 * @return The type that must be of Class&lt;T&gt;
	 */
	abstract public Class<T> getType();

	public String getReference() {
		return reference;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int column) {
		this.index = column;
	}	
	
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}	
	
	public String toString() {
		if (name == null) {
			return super.toString();
		}
		else {
			return super.toString() + ", name=" + name;
		}
	}

}

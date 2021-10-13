package org.oddjob.dido.poi.layouts;

import dido.data.GenericData;
import dido.poi.CellIn;
import dido.poi.CellOut;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.oddjob.dido.poi.HeaderRowOut;
import org.oddjob.dido.poi.RowOut;
import org.oddjob.dido.poi.data.DataCell;
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
	 * been defined with the {@link org.oddjob.dido.poi.data.PoiWorkbook} definition.
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
			public void setValue(RowOut rowOut, GenericData<String> data) {

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
	abstract void insertValueInto(Cell cell, int index,  GenericData<String> data);

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
	 * @oddjob.description The default style for the cell. This is a 
	 * visible property of the layout so that users can see which style
	 * name to use if they wish to override the style at the
	 * {@link org.oddjob.dido.poi.data.PoiWorkbook} level.
	 * @oddjob.required Read only.
	 * 
	 * @return
	 */
	public String getDefaultStyle() {
		return null;
	}

	/**
	 * Get the type. Note that this can't be Class&lt;T&gt; because of Java's
	 * inability to resolve an expression such as
	 * <code>List&lt;Integer&gt;.class</code>.
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

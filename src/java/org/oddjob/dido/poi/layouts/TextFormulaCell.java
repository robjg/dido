package org.oddjob.dido.poi.layouts;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class TextFormulaCell extends FormulaCell<String> {

	@Override
	public Class<String> getType() {
		return String.class;
	}

	@Override
	protected void extractCellValue(Cell cell) {
		FormulaEvaluator evaluator = cell.getRow().getSheet(
		).getWorkbook().getCreationHelper().createFormulaEvaluator();

		CellValue cellValue = evaluator.evaluate(cell);
		
		setValue(cellValue.getStringValue());
	}
	
	public void setValue(String value) {
		this.value(value);
	}
	
	public String getValue() {
		return this.value();
	}
}

package org.oddjob.dido.poi.layouts;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class NumericFormulaCell extends FormulaCell<Double> {


	@Override
	public Class<Double> getType() {
		return Double.class;
	}

	@Override
	protected void extractCellValue(Cell cell) {
		FormulaEvaluator evaluator = cell.getRow().getSheet(
		).getWorkbook().getCreationHelper().createFormulaEvaluator();

		CellValue cellValue = evaluator.evaluate(cell);
		
		setValue(cellValue.getNumberValue());
	}
	
	public void setValue(Double value) {
		this.value(value);
	}
	
	public Double getValue() {
		return this.value();
	}
}

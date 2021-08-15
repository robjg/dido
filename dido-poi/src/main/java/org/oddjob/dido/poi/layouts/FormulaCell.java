package org.oddjob.dido.poi.layouts;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.oddjob.dido.DataException;

public abstract class FormulaCell<T> extends DataCell<T> {

	private String formula;

	@Override
	public int getCellType() {
		return Cell.CELL_TYPE_FORMULA;
	}
	
	@Override
	public void insertValueInto(Cell cell, T value) throws DataException {
		try {
			cell.setCellFormula(formula);
		} catch (FormulaParseException e) {
			throw new DataException("Failed setting formula: " + formula, e);
		}
		
		FormulaEvaluator evaluator = cell.getRow().getSheet(
			).getWorkbook().getCreationHelper().createFormulaEvaluator();
		
		evaluator.evaluateFormulaCell(cell);
	}
	
	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
}

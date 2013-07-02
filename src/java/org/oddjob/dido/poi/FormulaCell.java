package org.oddjob.dido.poi;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.oddjob.dido.DataException;

public abstract class FormulaCell<T> extends DataCell<T> {

	private String formula;
	

	@Override
	protected int getCellType() {
		return Cell.CELL_TYPE_FORMULA;
	}
	
	@Override
	protected void insertValueInto(Cell cell) throws DataException {
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

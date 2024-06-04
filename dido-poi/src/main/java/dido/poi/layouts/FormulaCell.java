package dido.poi.layouts;

import dido.data.GenericData;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public abstract class FormulaCell<T> extends AbstractDataCell<T> {

	private String formula;

	@Override
	public CellType getCellType() {
		return CellType.FORMULA;
	}

	@Override
	void insertValueInto(Cell cell, int index, GenericData<String> data) {

		try {
			cell.setCellFormula(formula);
		} catch (FormulaParseException e) {
			throw new IllegalArgumentException("Failed setting formula: " + formula, e);
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

package dido.poi.layouts;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class NumericFormulaCell extends FormulaCell<Double> {

	@Override
	public Class<Double> getType() {
		return Double.class;
	}


	@Override
	public Double extractCellValue(Cell cell) {
		FormulaEvaluator evaluator = cell.getRow().getSheet(
		).getWorkbook().getCreationHelper().createFormulaEvaluator();

		CellValue cellValue = evaluator.evaluate(cell);
		
		return cellValue.getNumberValue();
	}
}

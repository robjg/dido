package dido.poi.layouts;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class TextFormulaCell extends FormulaCell<String> {

	@Override
	public Class<String> getType() {
		return String.class;
	}

	@Override
	public String extractCellValue(Cell cell) {
		
		FormulaEvaluator evaluator = cell.getRow().getSheet(
		).getWorkbook().getCreationHelper().createFormulaEvaluator();

		CellValue cellValue = evaluator.evaluate(cell);
		
		return cellValue.getStringValue();
	}
}

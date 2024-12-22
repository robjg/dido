package dido.poi.layouts;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.how.DataException;
import dido.how.conversion.DidoConversionProvider;
import dido.how.conversion.RequiringConversion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

import java.util.function.Function;

/**
 * @oddjob.description Define a Text Formula Column.
 *
 * @author rob
 */
public class TextFormulaCell extends FormulaCell {

	@Override
	public Class<String> getType() {
		return String.class;
	}

	@Override
	protected FieldGetter getterFor(SchemaField schemaField, DidoConversionProvider conversionProvider) {

		Class<?> type = schemaField.getType();

		if (type.isAssignableFrom(String.class)) {
			return new TextCellGetter(schemaField);
		} else {
			return new TextCellGetterWithConversion<>(
					schemaField,
					RequiringConversion.with(conversionProvider).from(String.class).to(type));
		}
	}

	static class TextCellGetter extends AbstractCellGetter {

		TextCellGetter(SchemaField schemaField) {
			super(schemaField);
		}

		@Override
		public Object get(DidoData data) {
			return getString(data);
		}

		@Override
		public String getString(DidoData data) {
			Cell cell = getCell(data);

			FormulaEvaluator evaluator = cell.getRow().getSheet(
			).getWorkbook().getCreationHelper().createFormulaEvaluator();

			CellValue cellValue = evaluator.evaluate(cell);

			return cellValue.getStringValue();
		}
	}

	static class TextCellGetterWithConversion<R> extends TextCellGetter {

		private final Function<String, R> conversion;

		TextCellGetterWithConversion(SchemaField schemaField,
									 Function<String, R> conversion) {
			super(schemaField);
			this.conversion = conversion;
		}

		@Override
		public Object get(DidoData data) {
			try {
				return conversion.apply(super.getString(data));
			} catch (RuntimeException e) {
				throw DataException.of(String.format("Failed to get value for %s", this), e);
			}
		}
	}

}

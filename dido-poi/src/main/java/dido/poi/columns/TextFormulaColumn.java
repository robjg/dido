package dido.poi.columns;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.data.util.TypeUtil;
import dido.how.DataException;
import dido.how.conversion.DidoConversionProvider;
import dido.how.conversion.RequiringConversion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

import java.util.function.Function;

/**
 * Define a Text Formula Column.
 *
 * @author rob
 */
public class TextFormulaColumn extends FormulaColumn {

	protected TextFormulaColumn(Settings settings) {
		super(settings);
	}

	public static class Settings extends FormulaSettings<Settings> {

		@Override
		protected Settings self() {
			return this;
		}

		public TextFormulaColumn make() {
			return new TextFormulaColumn(this);
		}
	}

	public static Settings with() {
		return new Settings();
	}

	@Override
	public Class<String> getType() {
		return TextColumn.TYPE;
	}

	@Override
	protected FieldGetter getterFor(SchemaField schemaField, DidoConversionProvider conversionProvider) {

		Class<?> type = TypeUtil.classOf(schemaField.getType());

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

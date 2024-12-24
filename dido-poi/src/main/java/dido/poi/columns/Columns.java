package dido.poi.columns;

public class Columns {


    public static TextColumn.Settings text() {
        return TextColumn.with();
    }

    public static NumericColumn.Settings numeric() {
        return NumericColumn.with();
    }

    public static BooleanColumn.Settings bool() {
        return BooleanColumn.with();
    }

    public static DateColumn.Settings date() {
        return DateColumn.with();
    }

    public static TextFormulaColumn.Settings cellFormula() {
        return TextFormulaColumn.with();
    }

    public static NumericFormulaColumn.Settings numericFormula() {
        return NumericFormulaColumn.with();
    }

}

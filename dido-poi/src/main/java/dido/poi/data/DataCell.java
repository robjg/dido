package dido.poi.data;

import dido.poi.CellInProvider;
import dido.poi.CellOutProvider;

public interface DataCell<T> extends CellInProvider<T>, CellOutProvider<T> {

	Class<?> getType();

	String getName();
}

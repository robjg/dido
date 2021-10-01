package dido.poi;

public interface CellInProvider<T> {

    int getIndex();

    CellIn<T> provideCellIn(int columnIndex);
}

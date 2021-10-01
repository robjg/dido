package dido.poi;

public interface CellOutProvider<T> {

    int getIndex();

    CellOut<T> provideCellOut(int columnIndex);
}

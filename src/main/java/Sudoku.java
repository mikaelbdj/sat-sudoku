import java.util.ArrayList;
import java.util.List;

/**
 * Create immutable Sudoku object
 * On creation checks if the provided array fulfills requirements to be a sudoku
 */
public class Sudoku {

    private final int [][] sudoku;
    private final int size;

    public Sudoku (int[][] sudoku) throws SudokuException {
        this.sudoku = sudoku;
        this.size = sudoku.length;

        if (size != sudoku[0].length) throw new SudokuException("Height and length of sudoku does not match");
        if (Math.pow(Math.sqrt(size),2) != size) throw new SudokuException("Sudoku size is not a perfect square");
        if (flatValues().stream().anyMatch(value -> size < value || value < 0)) throw new SudokuException("Sudoku contains too large or negative values");
    }

    public int getCell (int row, int col) {
        return sudoku[row][col];
    }

    public int getSize () {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int[] row : sudoku) {
            for (int cell : row) {
                stringBuilder.append(cell).append(" ");
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    private List<Integer> flatValues() {
        List<Integer> accumulator = new ArrayList<>();
        for (int[] row : sudoku) {
            for (int cell : row) {
                accumulator.add(cell);
            }
        }
        return accumulator;
    }
}

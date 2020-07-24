import java.util.ArrayList;
import java.util.Arrays;
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

        if (Arrays.stream(sudoku).anyMatch(row -> row.length != size)) throw new SudokuException("Height and length of sudoku does not match");
        if (Math.pow(Math.sqrt(size),2) != size) throw new SudokuException("Sudoku size is not a perfect square");
        if (flatValues().stream().anyMatch(value -> size < value || value < 0)) throw new SudokuException("Sudoku contains too large or negative values");
    }

    public int getValue(int row, int col) {
        return sudoku[row][col];
    }

    public int getSize () {
        return size;
    }

    //really fancy string representation
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        int boxSize = (int) Math.sqrt(size);
        int lineLength = size * 3 + boxSize * 2 + 1 - (size <= 9 ? size : 0);
        stringBuilder.append("-".repeat(lineLength));
        stringBuilder.append("\n");

        for (int row = 0; row < size; row++) {
            stringBuilder.append("| ");
            for (int col = 0; col < size; col++) {
                int value  = getValue(row, col);
                boolean boxLine = ((col + 1) % boxSize) == 0;
                stringBuilder.append(value).append(size <= 9 || 9 < value ? " " : "  ");
                stringBuilder.append(boxLine ? "| " : "");
            }
            stringBuilder.append("\n");
            if ((row +1 ) % boxSize == 0) {
                stringBuilder.append("-".repeat(lineLength));
                stringBuilder.append("\n");
            }
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

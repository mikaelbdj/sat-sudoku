import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) wrongArgumentCount();

        Path path = Paths.get(args[0]);

        Optional<Sudoku> sudoku = Optional.empty();

        try {
            List<String> rows = Files.readAllLines(path);
            int[][] sudokuData = new int[rows.size()][rows.size()];
            for (int i = 0; i < sudokuData.length; i++) {
                sudokuData[i] = Arrays.stream(rows.get(i).split(" ")).mapToInt(Integer::parseInt).toArray();
            }
            sudoku = Optional.of(new Sudoku(sudokuData));

        } catch (IOException | SudokuException e) {
            e.printStackTrace();
        }

        if (sudoku.isEmpty()) return; //failed to retrieve sudoku from file

        SudokuSolver sudokuSolver = new SudokuSolver(sudoku.get());
        Optional<Sudoku> solution = sudokuSolver.solve();
        if (solution.isPresent()) {
            System.out.println(solution.get());
        }
        else {
            System.out.println("No solution");
        }
    }

    public static void wrongArgumentCount() {
        System.out.println("Provide one argument: A .txt file with the sudoku");
        System.exit(1);
    }
}

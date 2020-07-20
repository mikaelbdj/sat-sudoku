import org.sat4j.specs.ContradictionException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) wrongArgumentCount();

        Path path = Paths.get(args[0]);
        int[][] sudoku = new int[9][9];

        try {
           List<String> rows = Files.readAllLines(path);
           for (int i = 0; i < 9; i++) {
               sudoku[i] = Arrays.stream(rows.get(i).split(" ")).mapToInt(Integer::parseInt).toArray();
           }
           //TODO: Validate that 9 x 9 sudoku has been given - reject with error message if not
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            SudokuSolver.solve(sudoku);
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
    }

    public static void wrongArgumentCount () {
        System.out.println("Provide one argument: A .txt file with the sudoku");
        System.exit(1);
    }
}

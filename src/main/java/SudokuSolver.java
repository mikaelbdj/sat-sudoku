import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.*;

import java.util.ArrayList;
import java.util.List;

public class SudokuSolver {


    public static void solve(int[][] sudoku) throws ContradictionException {
        Vec<IVecInt> ruleClauses = generateRuleClauses();
        Vec<IVecInt> numberClauses = generateNumberClauses(sudoku);

        ISolver solver = SolverFactory.newDefault();

        solver.addAllClauses(ruleClauses);
        solver.addAllClauses(numberClauses);


        IProblem problem = solver;

        try {
            if (problem.isSatisfiable()) {
                problem.model();
                //TODO: convert to sudoku
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    //sudoku rules as clauses
    private static Vec<IVecInt> generateRuleClauses() {
        Vec<IVecInt> ruleClauses = new Vec<>();
        //row rules
        List<IVecInt> rowRules = generateRowRuleClauses();
        for (IVecInt rowRule : rowRules) {
            ruleClauses.push(rowRule);
        }

        //column rules
        List<IVecInt> columnRules = generateColumnRuleClauses();
        for (IVecInt columnRule : columnRules) {
            ruleClauses.push(columnRule);
        }

        //box rules

        //cell rules

        return ruleClauses;
    }

    private static List<IVecInt> generateColumnRuleClauses() {
        List<IVecInt> columnRules = new ArrayList<>();

        //definedness
        for (int col = 0; col < 9; col++) {
            for (int value = 1; value <= 9; value++) {
                int[] definedness = new int[9];
                for (int row = 0; row < 9; row++) {
                    definedness[row] = getLiteralFromCell(row, col, value);
                }
                columnRules.add(new VecInt(definedness));
            }
        }

        //uniqueness
        for (int col = 0; col < 9; col++) {
            for (int value = 1; value <= 9; value++) {
                for (int row1 = 0; row1 < 8; row1++) {
                    for (int row2 = row1 + 1; row2 < 9; row2++) {
                        int[] uniqueness = new int[2];
                        uniqueness[0] = -getLiteralFromCell(row1, col, value);
                        uniqueness[1] = -getLiteralFromCell(row2, col, value);
                        columnRules.add(new VecInt(uniqueness));
                    }
                }
            }
        }
        return columnRules;
    }

    private static List<IVecInt> generateRowRuleClauses() {
        List<IVecInt> rowRules = new ArrayList<>();

        //definedness
        for (int row = 0; row < 9; row++) {
            for (int value = 1; value <= 9; value++) {
                int[] definedness = new int[9];
                for (int col = 0; col < 9; col++) {
                    definedness[col] = getLiteralFromCell(row, col, value);
                }
                rowRules.add(new VecInt(definedness));
            }
        }

        //uniqueness
        for (int row = 0; row < 9; row++) {
            for (int value = 1; value <= 9; value++) {
                for (int col1 = 0; col1 < 8; col1++) {
                    for (int col2 = col1 + 1; col2 < 9; col2++) {
                        int[] uniqueness = new int[2];
                        uniqueness[0] = -getLiteralFromCell(row, col1, value);
                        uniqueness[1] = -getLiteralFromCell(row, col2, value);
                        rowRules.add(new VecInt(uniqueness));
                    }
                }
            }
        }

        return rowRules;
    }


    //clauses from the given numbers
    private static Vec<IVecInt> generateNumberClauses(int[][] sudoku) {
        Vec<IVecInt> numberClauses = new Vec<>();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = sudoku[row][col];
                int literal = getLiteralFromCell(row, col, value);
                if (value != 0) {
                    numberClauses.push(new VecInt(new int[]{literal}));
                }
            }
        }

        return numberClauses;
    }

    private static int getLiteralFromCell(int row, int col, int value) {
        return (row * 9 * 9) + (col * 9) + value;
    }
}

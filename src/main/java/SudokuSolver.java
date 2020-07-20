import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.*;

import java.util.ArrayList;
import java.util.List;

public class SudokuSolver {

    static final int SIZE = 9;

    public static void solve(int[][] sudoku)  {
        Vec<IVecInt> ruleClauses = generateRuleClauses();
        Vec<IVecInt> numberClauses = generateNumberClauses(sudoku);

        ISolver solver = SolverFactory.newDefault();




        try {
            solver.addAllClauses(ruleClauses);
            solver.addAllClauses(numberClauses);
            IProblem problem = solver;
            if (problem.isSatisfiable()) {
                problem.model();
                //TODO: convert to sudoku
            }
        } catch (TimeoutException | ContradictionException e) {
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
        List<IVecInt> boxRules = generateBoxRuleClauses();
        for (IVecInt boxRule : boxRules) {
            ruleClauses.push(boxRule);
        }

        //cell rules
        List<IVecInt> cellRules = generateCellRuleClauses();
        for (IVecInt cellRule : cellRules) {
            ruleClauses.push(cellRule);
        }

        return ruleClauses;
    }

    private static List<IVecInt> generateCellRuleClauses() {
        List<IVecInt> cellRules = new ArrayList<>();

        //definedness
        for (int cell = 0; cell < SIZE * SIZE; cell++) {
            int[] definedness = new int[SIZE];
            for (int value = 1; value <= SIZE; value++) {
                definedness[value - 1] = cell * 9 + value;
            }
            cellRules.add(new VecInt(definedness));
        }

        //uniqueness
        for (int cell = 0; cell < SIZE * SIZE; cell++) {
            for (int value1 = 1; value1 < SIZE; value1++) {
                for (int value2 = value1 + 1; value2 <= SIZE; value2++) {
                    int[] uniqueness = new int[2];
                    uniqueness[0] = -(cell * 9 + value1);
                    uniqueness[1] = -(cell * 9 + value2);
                    cellRules.add(new VecInt(uniqueness));
                }
            }
        }

        return cellRules;
    }

    private static List<IVecInt> generateBoxRuleClauses() {
        List<IVecInt> boxRules = new ArrayList<>();
        int boxSize = (int) Math.sqrt(SIZE);

        //definedness
        for (int box = 0; box < SIZE; box++) {
            for (int value = 1; value <= SIZE; value++) {
                int[] definedness = new int[SIZE];
                for (int cell = 0; cell < SIZE; cell++) {
                    int row = (box / boxSize) * boxSize + (cell / boxSize);
                    int col = (box % boxSize) * boxSize + (cell % boxSize);
                    definedness[cell] = getLiteralFromCell(row, col, value);
                }
                boxRules.add(new VecInt(definedness));
            }
        }

        //uniqueness
        for (int box = 0; box < SIZE; box++) {
            for (int value = 1; value <= SIZE; value++) {
                for (int cell1 = 0; cell1 < SIZE - 1; cell1++) {
                    for (int cell2 = cell1 + 1; cell2 < SIZE; cell2++) {
                        int[] uniqueness = new int[2];

                        int row1 = (box / boxSize) * boxSize + (cell1 / boxSize);
                        int col1 = (box % boxSize) * boxSize + (cell1 % boxSize);
                        uniqueness[0] = -getLiteralFromCell(row1, col1, value);

                        int row2 = (box / boxSize) * boxSize + (cell2 / boxSize);
                        int col2 = (box % boxSize) * boxSize + (cell2 % boxSize);
                        uniqueness[1] = -getLiteralFromCell(row2, col2, value);

                        boxRules.add(new VecInt(uniqueness));
                    }
                }
            }
        }

        return boxRules;
    }

    private static List<IVecInt> generateColumnRuleClauses() {
        List<IVecInt> columnRules = new ArrayList<>();

        //definedness
        for (int col = 0; col < SIZE; col++) {
            for (int value = 1; value <= SIZE; value++) {
                int[] definedness = new int[SIZE];
                for (int row = 0; row < SIZE; row++) {
                    definedness[row] = getLiteralFromCell(row, col, value);
                }
                columnRules.add(new VecInt(definedness));
            }
        }

        //uniqueness
        for (int col = 0; col < SIZE; col++) {
            for (int value = 1; value <= SIZE; value++) {
                for (int row1 = 0; row1 < SIZE - 1; row1++) {
                    for (int row2 = row1 + 1; row2 < SIZE; row2++) {
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
        for (int row = 0; row < SIZE; row++) {
            for (int value = 1; value <= SIZE; value++) {
                int[] definedness = new int[SIZE];
                for (int col = 0; col < SIZE; col++) {
                    definedness[col] = getLiteralFromCell(row, col, value);
                }
                rowRules.add(new VecInt(definedness));
            }
        }

        //uniqueness
        for (int row = 0; row < SIZE; row++) {
            for (int value = 1; value <= SIZE; value++) {
                for (int col1 = 0; col1 < SIZE - 1; col1++) {
                    for (int col2 = col1 + 1; col2 < SIZE; col2++) {
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

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
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

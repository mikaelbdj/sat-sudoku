import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SudokuSolver {


    private final Sudoku sudoku;
    private int clausesCount;
    private final List<ThermometerRule> thermometerRules;

    public SudokuSolver (Sudoku sudoku) {
        this.sudoku = sudoku;
        thermometerRules = new ArrayList<>();
        clausesCount = 0;
    }

    public Optional<Sudoku> solve() {
        long beforeClauses = System.currentTimeMillis();
        IProblem problem = generateSATProblem();
        long afterClauses = System.currentTimeMillis();

        try {
            if (problem.isSatisfiable()) {
                long afterSolved = System.currentTimeMillis();
                System.out.printf("Generated %d SAT clauses in %d ms \nFound model in %d ms\n",clausesCount, (afterClauses - beforeClauses), (afterSolved - afterClauses));

                return getSudokuFromModel(problem.model());
            } else {
                return Optional.empty();
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private IProblem generateSATProblem () {
        IVec<IVecInt> ruleClauses = generateRuleClauses();
        IVec<IVecInt> numberClauses = generateNumberClauses();

        ISolver solver = SolverFactory.newDefault();

        try {
            solver.addAllClauses(ruleClauses);
            solver.addAllClauses(numberClauses);
            clausesCount = ruleClauses.size() + numberClauses.size();
            for (ThermometerRule thermometerRule : thermometerRules) {
                IVec<IVecInt> thermometerClauses = thermometerRule.generateThermometerRuleClauses();
                solver.addAllClauses(thermometerClauses);
                clausesCount += thermometerClauses.size();
            }
        } catch (ContradictionException e) {
            e.printStackTrace();
        }

        return solver;
    }

    public void addThermometerRule (ThermometerRule thermometerRule) {
        thermometerRules.add(thermometerRule);
    }

    //sudoku rules as clauses
    private Vec<IVecInt> generateRuleClauses() {
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

    private List<IVecInt> generateCellRuleClauses() {
        List<IVecInt> cellRules = new ArrayList<>();
        int size = sudoku.getSize();

        //definedness
        for (int cell = 0; cell < size * size; cell++) {
            int[] definedness = new int[size];
            for (int value = 1; value <= size; value++) {
                definedness[value - 1] = cell * size + value;
            }
            cellRules.add(new VecInt(definedness));
        }

        //uniqueness
        for (int cell = 0; cell < size * size; cell++) {
            for (int value1 = 1; value1 < size; value1++) {
                for (int value2 = value1 + 1; value2 <= size; value2++) {
                    int[] uniqueness = new int[2];
                    uniqueness[0] = -(cell * size + value1);
                    uniqueness[1] = -(cell * size + value2);
                    cellRules.add(new VecInt(uniqueness));
                }
            }
        }

        return cellRules;
    }

    private List<IVecInt> generateBoxRuleClauses() {
        List<IVecInt> boxRules = new ArrayList<>();
        int size = sudoku.getSize();

        int boxSize = (int) Math.sqrt(size);

        //definedness
        for (int box = 0; box < size; box++) {
            for (int value = 1; value <= size; value++) {
                int[] definedness = new int[size];
                for (int cell = 0; cell < size; cell++) {
                    int row = (box / boxSize) * boxSize + (cell / boxSize);
                    int col = (box % boxSize) * boxSize + (cell % boxSize);
                    definedness[cell] = getLiteralFromCell(row, col, value);
                }
                boxRules.add(new VecInt(definedness));
            }
        }

        //uniqueness
        for (int box = 0; box < size; box++) {
            for (int value = 1; value <= size; value++) {
                for (int cell1 = 0; cell1 < size - 1; cell1++) {
                    for (int cell2 = cell1 + 1; cell2 < size; cell2++) {
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

    private List<IVecInt> generateColumnRuleClauses() {
        List<IVecInt> columnRules = new ArrayList<>();
        int size = sudoku.getSize();

        //definedness
        for (int col = 0; col < size; col++) {
            for (int value = 1; value <= size; value++) {
                int[] definedness = new int[size];
                for (int row = 0; row < size; row++) {
                    definedness[row] = getLiteralFromCell(row, col, value);
                }
                columnRules.add(new VecInt(definedness));
            }
        }

        //uniqueness
        for (int col = 0; col < size; col++) {
            for (int value = 1; value <= size; value++) {
                for (int row1 = 0; row1 < size - 1; row1++) {
                    for (int row2 = row1 + 1; row2 < size; row2++) {
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

    private List<IVecInt> generateRowRuleClauses() {
        List<IVecInt> rowRules = new ArrayList<>();
        int size = sudoku.getSize();

        //definedness
        for (int row = 0; row < size; row++) {
            for (int value = 1; value <= size; value++) {
                int[] definedness = new int[size];
                for (int col = 0; col < size; col++) {
                    definedness[col] = getLiteralFromCell(row, col, value);
                }
                rowRules.add(new VecInt(definedness));
            }
        }

        //uniqueness
        for (int row = 0; row < size; row++) {
            for (int value = 1; value <= size; value++) {
                for (int col1 = 0; col1 < size - 1; col1++) {
                    for (int col2 = col1 + 1; col2 < size; col2++) {
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
    private Vec<IVecInt> generateNumberClauses() {
        Vec<IVecInt> numberClauses = new Vec<>();
        int size = sudoku.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int value = sudoku.getValue(row,col);
                int literal = getLiteralFromCell(row, col, value);
                if (value != 0) {
                    numberClauses.push(new VecInt(new int[]{literal}));
                }
            }
        }

        return numberClauses;
    }

    private int getLiteralFromCell(int row, int col, int value) {
        int size = sudoku.getSize();
        return (row * size * size) + (col * size) + value;
    }

    private Optional<Sudoku> getSudokuFromModel(int[] model) {
        int size = sudoku.getSize();
        int[][] sudoku = new int[size][size];
        for (int literal : model) {
            if (0 < literal) {
                int row = (literal - 1) /  (size * size);
                int col = ((literal - 1) %  (size * size)) / size;
                int value = literal - row *  (size * size) - col * size;
                sudoku[row][col] = value;
            }
        }
        try {
            return Optional.of(new Sudoku(sudoku));
        } catch (SudokuException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}

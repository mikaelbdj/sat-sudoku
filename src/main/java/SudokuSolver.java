import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.*;

public class SudokuSolver {


    public static void solve(int[][] sudoku) throws ContradictionException {
        Vec<IVecInt> clauses = generateRuleClauses();
        Vec<IVecInt> numberClauses = generateNumberClauses(sudoku);

        ISolver solver = SolverFactory.newDefault();

        solver.addAllClauses(clauses);
        solver.addAllClauses(numberClauses);


        IProblem problem = solver;

        try {
            if (problem.isSatisfiable()) {
                problem.model();
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    //same for all sudoku
    private static Vec<IVecInt> generateRuleClauses () {
        Vec<IVecInt> ruleClauses = new Vec<>();
        //row rules

        //column rules

        //box rules

        //one number per cell constraint

        return ruleClauses;
    }


    //clauses from the given numbers
    private static Vec<IVecInt> generateNumberClauses (int[][] sudoku) {
        Vec<IVecInt> numberClauses = new Vec<>();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int value = sudoku[i][j];
                int literal = i * j + value;
                if (value != 0) {
                    numberClauses.push(new VecInt(new int[]{literal}));
                }
            }
        }

        return numberClauses;
    }
}

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sat4j.core.VecInt;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class TestSudokuSolver {

    int [][] sudoku;

    @Before
    public void setup () {
        sudoku = new int[][]{
                {0, 0, 0,  0, 0, 0,  0, 0, 0},
                {0, 0, 0,  0, 0, 0,  0, 0, 0},
                {0, 0, 0,  0, 0, 0,  0, 0, 0},

                {0, 0, 0,  0, 0, 0,  0, 0, 0},
                {0, 0, 0,  0, 0, 0,  0, 0, 0},
                {0, 0, 0,  0, 0, 0,  0, 0, 0},

                {0, 0, 0,  0, 0, 0,  0, 0, 0},
                {0, 0, 0,  0, 0, 0,  0, 0, 0},
                {0, 0, 0,  0, 0, 0,  0, 0, 0},
        };
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEmptySudokuGenerateNoNumberClauses () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SudokuSolver.class.getDeclaredMethod("generateNumberClauses", int[][].class);
        method.setAccessible(true);
        IVec<IVecInt> clauses = (IVec<IVecInt>) method.invoke(null, (Object) sudoku);

        Assert.assertTrue(clauses.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSudokuWithNumbersGenerateCorrectNumberClauses () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SudokuSolver.class.getDeclaredMethod("generateNumberClauses", int[][].class);
        method.setAccessible(true);

        sudoku = new int[][]{
                {0, 0, 3,  0, 0, 0,  0, 0, 0},
                {0, 0, 0,  7, 0, 0,  0, 0, 0},
                {0, 0, 0,  0, 0, 0,  0, 0, 0},

                {0, 0, 0,  0, 0, 0,  0, 0, 0},
                {0, 0, 0,  0, 0, 0,  0, 0, 0},
                {0, 0, 0,  0, 0, 0,  0, 0, 0},

                {0, 0, 0,  5, 0, 0,  0, 0, 0},
                {0, 0, 0,  0, 0, 0,  0, 0, 0},
                {0, 0, 0,  0, 0, 0,  0, 0, 9},
        };

        IVec<IVecInt> clauses = (IVec<IVecInt>) method.invoke(null, (Object) sudoku);

        Assert.assertThat(clauses.size(), is(4));
        Assert.assertThat(clauses.get(0), is(new VecInt(new int[]{21})));
        Assert.assertThat(clauses.get(1), is(new VecInt(new int[]{115})));
        Assert.assertThat(clauses.get(2), is(new VecInt(new int[]{518})));
        Assert.assertThat(clauses.get(3), is(new VecInt(new int[]{729})));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCorrectDefinednessRowRuleClauses () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SudokuSolver.class.getDeclaredMethod("generateRowRuleClauses");
        method.setAccessible(true);

        List<IVecInt> clauses = (List<IVecInt>) method.invoke(null);

        IVecInt definednessClause0 = new VecInt(new int[]{1,10,19,28,37,46,55,64,73}); //at least one value of 1 in row 0
        IVecInt definednessClause1 = new VecInt(new int[]{86,95,104,113,122,131,140,149,158}); //at least one value of 5 in row 1

        System.out.println(clauses.get(8));
        Assert.assertThat(clauses.size(), is(2997));
        Assert.assertThat(clauses.contains(definednessClause0), is(true));
        Assert.assertThat(clauses.contains(definednessClause1), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCorrectUniquenessRowRuleClauses () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SudokuSolver.class.getDeclaredMethod("generateRowRuleClauses");
        method.setAccessible(true);

        List<IVecInt> clauses = (List<IVecInt>) method.invoke(null);

        int[][] uniquenessClausesArray = new int[][] {
                {-1,-10},{-1,-19},{-1,-28},{-1,-37},{-1,-46},{-1,-55},{-1,-64},{-1,-73},
                {-10,-19},{-10,-28},{-10,-37},{-10,-46},{-10,-55},{-10,-64},{-10,-73},
                {-19,-28},{-19,-37},{-19,-46},{-19,-55},{-19,-64},{-19,-73},
                {-28,-37},{-28,-46},{-28,-55},{-28,-64},{-28,-73},
                {-37,-46},{-37,-55},{-37,-64},{-37,-73},
                {-46,-55},{-46,-64},{-46,-73},
                {-55,-64},{-55,-73},
                {-64,-73},
        }; //at most one value of 1 in row 0
        List<IVecInt> uniquenessClauses = new ArrayList<>();
        for (int[] uniquenessClause : uniquenessClausesArray) {
            uniquenessClauses.add(new VecInt(uniquenessClause));
        }

        Assert.assertThat(clauses.size(), is(2997));
        Assert.assertThat(clauses.containsAll(uniquenessClauses), is(true));
    }
}

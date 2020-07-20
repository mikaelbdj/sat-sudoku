import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sat4j.core.VecInt;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
}

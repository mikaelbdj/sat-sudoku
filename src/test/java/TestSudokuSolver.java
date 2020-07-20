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

    @Test
    @SuppressWarnings("unchecked")
    public void testCorrectDefinednessColumnRuleClauses () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SudokuSolver.class.getDeclaredMethod("generateColumnRuleClauses");
        method.setAccessible(true);

        List<IVecInt> clauses = (List<IVecInt>) method.invoke(null);

        IVecInt definednessClause0 = new VecInt(new int[]{1,82,163,244,325,406,487,568,649}); //at least one value of 1 in col 0
        IVecInt definednessClause1 = new VecInt(new int[]{14,95,176,257,338,419,500,581,662}); //at least one value of 5 in col 1

        Assert.assertThat(clauses.size(), is(2997));
        Assert.assertThat(clauses.contains(definednessClause0), is(true));
        Assert.assertThat(clauses.contains(definednessClause1), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCorrectUniquenessColumnRuleClauses () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SudokuSolver.class.getDeclaredMethod("generateColumnRuleClauses");
        method.setAccessible(true);

        List<IVecInt> clauses = (List<IVecInt>) method.invoke(null);

        int[][] uniquenessClausesArray = new int[][] {
                {-1,-82},{-1,-163},{-1,-244},{-1,-325},{-1,-406},{-1,-487},{-1,-568},{-1,-649},
                {-82,-163},{-82,-244},{-82,-325},{-82,-406},{-82,-487},{-82,-568},{-82,-649},
                {-163,-244},{-163,-325},{-163,-406},{-163,-487},{-163,-568},{-163,-649},
                {-244,-325},{-244,-406},{-244,-487},{-244,-568},{-244,-649},
                {-325,-406},{-325,-487},{-325,-568},{-325,-649},
                {-406,-487},{-406,-568},{-406,-649},
                {-487,-568},{-487,-649},
                {-568,-649},
        }; //at most one value of 1 in col 0
        List<IVecInt> uniquenessClauses = new ArrayList<>();
        for (int[] uniquenessClause : uniquenessClausesArray) {
            uniquenessClauses.add(new VecInt(uniquenessClause));
        }

        Assert.assertThat(clauses.size(), is(2997));
        Assert.assertThat(clauses.containsAll(uniquenessClauses), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCorrectDefinednessBoxRuleClauses () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SudokuSolver.class.getDeclaredMethod("generateBoxRuleClauses");
        method.setAccessible(true);

        List<IVecInt> clauses = (List<IVecInt>) method.invoke(null);

        IVecInt definednessClause0 = new VecInt(new int[]{1,10,19,82,91,100,163,172,181}); //at least one value of 1 in box 0
        IVecInt definednessClause1 = new VecInt(new int[]{275,284,293,356,365,374,437,446,455}); //at least one value of 5 in box 4

        Assert.assertThat(clauses.size(), is(2997));
        Assert.assertThat(clauses.contains(definednessClause0), is(true));
        Assert.assertThat(clauses.contains(definednessClause1), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCorrectUniquenessBoxRuleClauses () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SudokuSolver.class.getDeclaredMethod("generateBoxRuleClauses");
        method.setAccessible(true);

        List<IVecInt> clauses = (List<IVecInt>) method.invoke(null);

        int[][] uniquenessClausesArray = new int[][] {
                {-271,-280},{-271,-289},{-271,-352},{-271,-361},{-271,-370},{-271,-433},{-271,-442},{-271,-451},
                {-280,-289},{-280,-352},{-280,-361},{-280,-370},{-280,-433},{-280,-442},{-280,-451},
                {-289,-352},{-289,-361},{-289,-370},{-289,-433},{-289,-442},{-289,-451},
                {-352,-361},{-352,-370},{-352,-433},{-352,-442},{-352,-451},
                {-361,-370},{-361,-433},{-361,-442},{-361,-451},
                {-370,-433},{-370,-442},{-370,-451},
                {-433,-442},{-433,-451},
                {-442,-451},
        }; //at most one value of 1 in box 4
        List<IVecInt> uniquenessClauses = new ArrayList<>();
        for (int[] uniquenessClause : uniquenessClausesArray) {
            uniquenessClauses.add(new VecInt(uniquenessClause));
        }

        Assert.assertThat(clauses.size(), is(2997));
        Assert.assertThat(clauses.containsAll(uniquenessClauses), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCorrectDefinednessCellRuleClauses () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SudokuSolver.class.getDeclaredMethod("generateCellRuleClauses");
        method.setAccessible(true);

        List<IVecInt> clauses = (List<IVecInt>) method.invoke(null);

        IVecInt definednessClause0 = new VecInt(new int[]{1,2,3,4,5,6,7,8,9}); //at least one value in cell 0
        IVecInt definednessClause1 = new VecInt(new int[]{127,128,129,130,131,132,133,134,135}); //at least one value in cell 15

        Assert.assertThat(clauses.size(), is(2997));
        Assert.assertThat(clauses.contains(definednessClause0), is(true));
        Assert.assertThat(clauses.contains(definednessClause1), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCorrectUniquenessCellRuleClauses () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SudokuSolver.class.getDeclaredMethod("generateCellRuleClauses");
        method.setAccessible(true);

        List<IVecInt> clauses = (List<IVecInt>) method.invoke(null);

        int[][] uniquenessClausesArray = new int[][] {
                {-10,-11},{-10,-12},{-10,-13},{-10,-14},{-10,-15},{-10,-16},{-10,-17},{-10,-18},
                {-11,-12},{-11,-13},{-11,-14},{-11,-15},{-11,-16},{-11,-17},{-11,-18},
                {-12,-13},{-12,-14},{-12,-15},{-12,-16},{-12,-17},{-12,-18},
                {-13,-14},{-13,-15},{-13,-16},{-13,-17},{-13,-18},
                {-14,-15},{-14,-16},{-14,-17},{-14,-18},
                {-15,-16},{-15,-17},{-15,-18},
                {-16,-17},{-16,-18},
                {-17,-18},
        }; //at most one value in cell 2
        List<IVecInt> uniquenessClauses = new ArrayList<>();
        for (int[] uniquenessClause : uniquenessClausesArray) {
            uniquenessClauses.add(new VecInt(uniquenessClause));
        }

        Assert.assertThat(clauses.size(), is(2997));
        Assert.assertThat(clauses.containsAll(uniquenessClauses), is(true));
    }
}

import org.sat4j.core.Vec;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

import java.util.ArrayList;
import java.util.List;

public class ThermometerRule {

    private final TreeNode<Cell> bulb;

    public ThermometerRule (TreeNode<Cell> bulb) {
        this.bulb = bulb;
    }

    public void addThermometerCell(Cell cell) {
        bulb.addChild(new TreeNode<>(cell));
    }

    public IVec<IVecInt> generateThermometerRuleClauses() {
        IVec<IVecInt> thermometerRuleClauses = new Vec<>();

        return thermometerRuleClauses;
    }
}

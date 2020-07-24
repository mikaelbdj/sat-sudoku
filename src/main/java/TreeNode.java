import java.util.ArrayList;
import java.util.List;

/**
 * Tree data structure used for constructing thermometer sudoku rules
 * @param <T> Data type in tree
 */
public class TreeNode<T>{

    private final T data;
    private final List<TreeNode<T>> children;

    public TreeNode (T data) {
        this.data = data;
        children = new ArrayList<>();

    }

    public void addChild (TreeNode<T> node) {
        children.add(node);
    }

    public List<TreeNode<T>> getChildren () {
        return children;
    }

    public T getData() {
        return data;
    }
}

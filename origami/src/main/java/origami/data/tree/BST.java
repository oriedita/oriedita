package origami.data.tree;

/**
 * BST stands for Binary Search Tree.
 */
public interface BST<T> {

    public void insert(int key, T value);

    public void delete(int key);

    public T get(int key);
}

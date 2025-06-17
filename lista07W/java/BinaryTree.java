public class BinaryTree<T extends Comparable<T>> {
  private final int NODE_WIDTH = 8;

  private static class Node<T> {
    T value;
    Node<T> left, right;

    Node(T value) {
      this.value = value;
    }
  }

  private Node<T> root;

  public synchronized void insert(T value) {
    root = insertRec(root, value);
  }

  private Node<T> insertRec(Node<T> node, T value) {
    if (node == null)
      return new Node<T>(value);
    int cmp = value.compareTo(node.value);
    if (cmp < 0)
      node.left = insertRec(node.left, value);
    else if (cmp > 0)
      node.right = insertRec(node.right, value);
    return node;
  }

  public synchronized boolean search(T value) {
    return searchRec(root, value);
  }

  private boolean searchRec(Node<T> node, T value) {
    if (node == null)
      return false;
    int cmp = value.compareTo(node.value);
    if (cmp == 0)
      return true;
    return cmp < 0 ? searchRec(node.left, value) : searchRec(node.right, value);
  }

  public synchronized boolean delete(T value) {
    int before = size();
    root = deleteRec(root, value);
    return size() < before;
  }

  private Node<T> deleteRec(Node<T> node, T value) {
    if (node == null)
      return null;
    int cmp = value.compareTo(node.value);
    if (cmp < 0)
      node.left = deleteRec(node.left, value);
    else if (cmp > 0)
      node.right = deleteRec(node.right, value);
    else {
      if (node.left == null)
        return node.right;
      if (node.right == null)
        return node.left;
      Node<T> min = node.right;
      while (min.left != null)
        min = min.left;
      node.value = min.value;
      node.right = deleteRec(node.right, min.value);
    }
    return node;
  }

  public synchronized String draw() {
    if (root == null)
      return null;
    StringBuilder sb = new StringBuilder();
    drawRec(root, sb, 0);
    return sb.toString();
  }

  private void drawRec(Node<T> node, StringBuilder sb, int depth) {
    if (node == null)
      return;
    drawRec(node.right, sb, depth + 1);
    for (int i = 0; i < depth; i++)
      sb.append(" ".repeat(NODE_WIDTH));
    String valueStr = String.valueOf(node.value);
    if (valueStr.length() < NODE_WIDTH)
      valueStr = String.format("%-" + NODE_WIDTH + "s", valueStr);
    sb.append(valueStr).append("\n");
    drawRec(node.left, sb, depth + 1);
  }

  public int size() {
    return sizeRec(root);
  }

  private int sizeRec(Node<T> node) {
    if (node == null)
      return 0;
    return 1 + sizeRec(node.left) + sizeRec(node.right);
  }

  public boolean isEmpty() {
    return root == null;
  }
}
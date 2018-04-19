package org.robotnav.SearchAlgorithm.Tree;

import java.util.ArrayList;
import java.util.List;

// Reference: https://stackoverflow.com/questions/3522454/java-tree-data-structure
public class Tree<T> {
    private Node<T> root;

    public Tree(T rootdata) {
        root = new Node<T>(rootdata);
    }

    public Tree(Node<T> node) {
        root = node;
    }

    public Node getRootNode() { return root; }
    public void setRootNode(Node root) { this.root = root; }

    public boolean isNodeOnBranch(Node targetNode) {
        if (targetNode == root) return false;

        Node parentNode = targetNode.getParentNode();
        while (parentNode != null) {
            if (parentNode == targetNode) return true;

            parentNode = parentNode.getParentNode();
        }
        return false;
    }
}

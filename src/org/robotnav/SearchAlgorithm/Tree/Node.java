package org.robotnav.SearchAlgorithm.Tree;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {

    private T data;
    private Node parentNode;
    private List<Node> childrenNodeList;

    public Node() {
        this.data = null;
        this.parentNode = null;
        this.childrenNodeList = new ArrayList<>();
    }

    public Node(T data) {
        this();
        setData(data);
    }

    public Node(T data, Node parentNode) {
        this(data);
        this.parentNode = parentNode;

    }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public Node getParentNode() { return parentNode; }
    public void setParentNode(Node parentNode) { this.parentNode = parentNode; }

    public List<Node> getChildrenNodeList() { return childrenNodeList; }
    public void setChildrenNodeList(List<Node> childrenNodeList) { this.childrenNodeList = childrenNodeList; }
    public void clearChildrenList() { this.childrenNodeList = new ArrayList<>(); }
    public void addChildNode(Node childNode) {
        this.childrenNodeList.add(childNode);
        childNode.setParentNode(this);
    }
    public void addChildNode(T childData) {
        addChildNode(new Node<T>(childData));
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof Node) return data.equals(((Node) node).data);
        return false;
    }

    public boolean isNodeRecurring(Node targetNode) {
        Node parentNode = getParentNode();
        while (parentNode != null) {
            if (parentNode.equals(targetNode)) return true;

            parentNode = parentNode.getParentNode();
        }
        return false;
    }

    public int getLevel() {
        Node parentNode = getParentNode();
        int result = 0;
        while (parentNode != null) {
            parentNode = parentNode.getParentNode();
            result++;
        }
        return result;
    }
}

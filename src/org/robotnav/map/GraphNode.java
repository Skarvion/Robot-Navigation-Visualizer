package org.robotnav.map;

import java.util.ArrayList;

public class GraphNode<T> {
    private T data;
    private ArrayList<GraphNode> nodeList;

    public GraphNode() {
        this.data = null;
        this.nodeList = new ArrayList<>();
    }

    public GraphNode(T data) {
        this();
        this.data = data;
    }

    public void addGraphNode(GraphNode graphNode) {
        nodeList.add(graphNode);
        graphNode.nodeList.add(this);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ArrayList<GraphNode> getGraphNodeList() { return nodeList; }
}

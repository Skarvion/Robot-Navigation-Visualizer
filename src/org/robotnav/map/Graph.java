package org.robotnav.map;

import java.util.ArrayList;

public class Graph {
    private ArrayList<GraphNode> graphNodes;
    private int width;
    private int height;


    public Graph(int width, int height) {
        graphNodes = new ArrayList<>();
        int totalNodes = width * height;

        for (int i = 0; i < totalNodes; i++) {
            graphNodes.add(new GraphNode());
        }
    }


}

package org.robotnav.DataParser;

import org.robotnav.SearchAlgorithm.Data.ActionNode;
import org.robotnav.SearchAlgorithm.Data.Coordinate;
import org.robotnav.SearchAlgorithm.Tree.Node;
import org.robotnav.map.Cell;
import org.robotnav.map.CellType;
import org.robotnav.map.Grid;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class GridGenerator {

    // Randomized Prim's Algorithm
    // Reference: https://en.wikipedia.org/wiki/Maze_generation_algorithm
    // Reference: https://stackoverflow.com/questions/23843197/maze-generating-algorithm-in-grid
    // Reference: http://jonathanzong.com/blog/2012/11/06/maze-generation-with-prims-algorithm
    public static Grid generateGrid(int n, int m) {
        Random rand = new Random();
        Grid resultMap = new Grid(n, m);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                resultMap.getCell(i, j).setType(CellType.Wall);
            }
        }

        int randomX = rand.nextInt(n);
        int randomY = rand.nextInt(m);

        Node<ActionNode> startingNode = new Node(new ActionNode(null, new Coordinate(randomX, randomY)));
        LinkedList<Node<ActionNode>> frontier = new LinkedList<>();
        for (int adjX = -1; adjX <= 1; adjX++) {
            for (int adjY = -1; adjY <= 1; adjY++) {
                if (adjX == 0 && adjY == 0 || adjX != 0 && adjY != 0) continue;
                Cell tempCell = resultMap.getCell(randomX + adjX, randomY + adjY);
                if (tempCell != null) {
                    if (tempCell.getType() == CellType.Empty) continue;
                    Node<ActionNode> discoveredNode = new Node(new ActionNode(null, new Coordinate(randomX + adjX, randomY + adjY)));
                    startingNode.addChildNode(discoveredNode);
                    frontier.add(discoveredNode);
                }
            }
        }

        Node<ActionNode> lastNode = null;
        while (!frontier.isEmpty()) {
            Node<ActionNode> currentNode = frontier.remove(rand.nextInt(frontier.size()));
            Node<ActionNode> opNode = oppositeNode(currentNode);

            if (resultMap.getCell(opNode.getData().getCoordinate().getX(), opNode.getData().getCoordinate().getY()) == null) continue;

            Coordinate currCoord = currentNode.getData().getCoordinate();
            Coordinate opCoord = opNode.getData().getCoordinate();

            Cell currentCell = resultMap.getCell(currCoord.getX(), currCoord.getY());
            Cell opCell = resultMap.getCell(opCoord.getX(), opCoord.getY());
            if (currentCell.getType() == CellType.Wall && opCell.getType() == CellType.Wall) {
                resultMap.getCell(currCoord.getX(), currCoord.getY()).setType(CellType.Empty);
                resultMap.getCell(opCoord.getX(), opCoord.getY()).setType(CellType.Empty);

                lastNode = opNode;

                for (int adjX = -1; adjX <= 1; adjX++) {
                    for (int adjY = -1; adjY <= 1; adjY++) {
                        if (adjX == 0 && adjY == 0 || adjX != 0 && adjY != 0) continue;
                        Cell tempCell = resultMap.getCell(currCoord.getX() + adjX, currCoord.getY() + adjY);
                        if (tempCell != null) {
                            if (tempCell.getType() == CellType.Empty) continue;
                            Node<ActionNode> discoveredNode = new Node(new ActionNode(null, new Coordinate(opCoord.getX()+ adjX,
                                    opCoord.getY()+ adjY)));
                            opNode.addChildNode(discoveredNode);
                            frontier.add(discoveredNode);
                        }
                    }
                }
            }
        }

        if (frontier.isEmpty())
            resultMap.setRobot(randomX, randomY);
            resultMap.setGoal(lastNode.getData().getCoordinate().getX(), lastNode.getData().getCoordinate().getY());

        return resultMap;
    }

    private static Node<ActionNode> oppositeNode(Node<ActionNode> node) {
        if (node.getParentNode() == null) return null;

        Integer currX = node.getData().getCoordinate().getX();
        Integer currY = node.getData().getCoordinate().getY();
        Integer parentX = ((ActionNode) node.getParentNode().getData()).getCoordinate().getX();
        Integer parentY = ((ActionNode) node.getParentNode().getData()).getCoordinate().getY();

        if (currX.compareTo(parentX) != 0) return new Node<>(new ActionNode(null, new Coordinate(currX + currX.compareTo(parentX), currY)));
        if (currY.compareTo(parentY) != 0) return new Node<>(new ActionNode(null, new Coordinate(currX, currY + currY.compareTo(parentY))));

        return null;
    }

}

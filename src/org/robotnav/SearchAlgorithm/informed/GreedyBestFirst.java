package org.robotnav.SearchAlgorithm.informed;

import org.robotnav.Robot.Direction;
import org.robotnav.Robot.Robot;
import org.robotnav.SearchAlgorithm.Data.ActionNode;
import org.robotnav.SearchAlgorithm.Data.Coordinate;
import org.robotnav.SearchAlgorithm.SearchAlgorithm;
import org.robotnav.SearchAlgorithm.Tree.Node;
import org.robotnav.SearchAlgorithm.Tree.Tree;
import org.robotnav.Utilities.Utilities;
import org.robotnav.map.CellType;
import org.robotnav.map.Grid;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

public class GreedyBestFirst extends SearchAlgorithm {

    public GreedyBestFirst() { setSearchAlgorithmName(new String[]{"Greedy Best First", "GBF", "GreedyBestFirst", "GBFS"});}

    @Override
    public ArrayList<Direction> executeSearch(Grid gridMap, Robot robot) {
        assignHeuristicValue(gridMap);

        Tree<ActionNode> searchTree = new Tree<>(new ActionNode(Direction.None, new Coordinate(robot.getxCoordinate(), robot.getyCoordinate())));
        PriorityQueue<Node<ActionNode>> frontiers = new PriorityQueue<>(50, new HeuristicComparator());
        frontiers.add(searchTree.getRootNode());
        setNodeCount(1);

        Node<ActionNode> selectedNode = null;
        while ((selectedNode = frontiers.poll()) != null) {
            if (gridMap.getCell(selectedNode.getData().getCoordinate().getX(), selectedNode.getData().getCoordinate().getY()).getType() == CellType.Goal) {
                return deriveSolutionFromNode(selectedNode);
            }

            Direction sourceDirection = selectedNode.getData().getMovement();

            int xCoordinate = selectedNode.getData().getCoordinate().getX();
            int yCoordinate = selectedNode.getData().getCoordinate().getY();
            updateCellState(xCoordinate, yCoordinate, CellType.Visited);

            ArrayList<Direction> possibleDirections = visitableDirection(gridMap, xCoordinate, yCoordinate, sourceDirection);

            Utilities.ReverseArray(possibleDirections);

            for (Direction d : possibleDirections) {
                int newXPos = xCoordinate;
                int newYPos = yCoordinate;

                switch (d) {
                    case Up:
                        newYPos -= 1;
                        break;

                    case Left:
                        newXPos -= 1;
                        break;

                    case Down:
                        newYPos += 1;
                        break;

                    case Right:
                        newXPos += 1;
                        break;
                }

                Node<ActionNode> discoveredNode = new Node<ActionNode>(new ActionNode(d, new Coordinate(newXPos, newYPos), gridMap.getCell(newXPos, newYPos).getHeuristicValue()));
                if (!selectedNode.isNodeRecurring(discoveredNode)) {
                    selectedNode.addChildNode(discoveredNode);
                    frontiers.add(discoveredNode);
                    incrementNodeCount();
                    updateCellState(newXPos, newYPos, CellType.Frontier);
                }

                if (gridMap.getCell(newXPos, newYPos).getType() == CellType.Goal) {
                    return deriveSolutionFromNode(discoveredNode);
                }
            }
        }

        return null;
    }
}

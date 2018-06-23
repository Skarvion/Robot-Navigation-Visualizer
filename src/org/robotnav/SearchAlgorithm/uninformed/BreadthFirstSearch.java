package org.robotnav.SearchAlgorithm.uninformed;

import org.robotnav.Robot.Direction;
import org.robotnav.Robot.Robot;
import org.robotnav.SearchAlgorithm.Data.ActionNode;
import org.robotnav.SearchAlgorithm.Data.Coordinate;
import org.robotnav.SearchAlgorithm.SearchAlgorithm;
import org.robotnav.SearchAlgorithm.Tree.Node;
import org.robotnav.SearchAlgorithm.Tree.Tree;
import org.robotnav.map.CellType;
import org.robotnav.map.Grid;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Breadth first search traverse all of the frontier equally and thus the search tree has a much wider shape
 */
public class BreadthFirstSearch extends SearchAlgorithm {

    /**
     * Constructor set the ID of this algorithm
     */
    public BreadthFirstSearch() {
        setSearchAlgorithmName(new String[]{"Breadth First Search", "BFS", "BreadthFirstSearch"});
    }

    /**
     * Override execute search method for BFS
     * @param gridMap map to be traversed
     * @param robot robot in map
     * @return path direction, null if no solution found
     */
    @Override
    public ArrayList<Direction> executeSearch(Grid gridMap, Robot robot) {

        // Create search tree, frontier list and root node
        Tree<ActionNode> searchTree = new Tree<>(new ActionNode(Direction.None, new Coordinate(robot.getxCoordinate(), robot.getyCoordinate())));
        LinkedList<Node<ActionNode>> frontiers = new LinkedList<>();
        frontiers.add(searchTree.getRootNode());
        setNodeCount(1);

        // Loop while frontier is not empty, poll the first entry
        Node<ActionNode> selectedNode;
        while ((selectedNode = frontiers.pollFirst()) != null) {

            // If solution is found, return result path
            if (gridMap.getCell(selectedNode.getData().getCoordinate().getX(), selectedNode.getData().getCoordinate().getY()).getType() == CellType.Goal) {
                return deriveSolutionFromNode(selectedNode);
            }

            Direction sourceDirection = selectedNode.getData().getMovement();

            int xCoordinate = selectedNode.getData().getCoordinate().getX();
            int yCoordinate = selectedNode.getData().getCoordinate().getY();

            updateCellState(xCoordinate, yCoordinate, CellType.Visited);
            ArrayList<Direction> possibleDirections = visitableDirection(gridMap, xCoordinate, yCoordinate, sourceDirection);

            // For each possible direction, add to frontier
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

                Node<ActionNode> discoveredNode = new Node<ActionNode>(new ActionNode(d, new Coordinate(newXPos, newYPos)));
                if (!selectedNode.isNodeRecurring(discoveredNode)) {
                    selectedNode.addChildNode(discoveredNode);
                    frontiers.addLast(discoveredNode);
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
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

// Reference: https://algorithmsinsight.wordpress.com/graph-theory-2/ida-star-algorithm-in-general/
public class IterativeDeepeningAStarSearch extends SearchAlgorithm {
    private Node<ActionNode> endNode;

    public IterativeDeepeningAStarSearch() { setSearchAlgorithmName(new String[]{"Iterative Deepening A Star", "IDAS", "IDA*", "IDA", "IterativeDeepeningAStar", "CUS2"});}

    @Override
    public ArrayList<Direction> executeSearch(Grid gridMap, Robot robot) {
        assignHeuristicValue(gridMap);

        Node<ActionNode> rootNode;
        int threshold = gridMap.getCell(robot.getxCoordinate(), robot.getyCoordinate()).getHeuristicValue();

        while (true) {
            rootNode = new Node(new ActionNode(Direction.None, new Coordinate(robot.getxCoordinate(), robot.getyCoordinate())));
            setNodeCount(1);
            delaySearch();
            resetGrid();
            int temp = iterativeSearch(gridMap, rootNode, threshold);
            if (temp == -1) return deriveSolutionFromNode(endNode);
            if (temp >= Integer.MAX_VALUE) return null;
            threshold = temp;
        }
    }

    private int iterativeSearch(Grid gridMap, Node<ActionNode> selectedNode, int threshold) {
        updateCellState(selectedNode.getData().getCoordinate().getX(), selectedNode.getData().getCoordinate().getY(), CellType.Visited);

        ArrayList<Node<ActionNode>> frontiers = new ArrayList();

        int fHeuristic = selectedNode.getData().getHeuristic();
        if (fHeuristic > threshold) return fHeuristic;
        if (gridMap.getCell(selectedNode.getData().getCoordinate().getX(), selectedNode.getData().getCoordinate().getY()).getType() == CellType.Goal) {
            endNode = selectedNode;
            return -1;
        }

        // Reference: https://stackoverflow.com/questions/22888961/how-to-get-the-value-of-integer-max-value-in-java-without-using-the-integer-clas
        int min = Integer.MAX_VALUE;

        Direction sourceDirection = selectedNode.getData().getMovement();

        int xCoordinate = selectedNode.getData().getCoordinate().getX();
        int yCoordinate = selectedNode.getData().getCoordinate().getY();

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


            Node<ActionNode> discoveredNode = new Node<>(new ActionNode(d, new Coordinate(newXPos, newYPos),
                    gridMap.getCell(newXPos, newYPos).getHeuristicValue() + selectedNode.getData().getCost() + moveCost(d, selectedNode),
                    selectedNode.getData().getCost() + moveCost(d, selectedNode)));
            if (!selectedNode.isNodeRecurring(discoveredNode)) {
                selectedNode.addChildNode(discoveredNode);
                updateCellState(discoveredNode.getData().getCoordinate().getX(), discoveredNode.getData().getCoordinate().getY(), CellType.Frontier);
                frontiers.add(discoveredNode);
                incrementNodeCount();
            }
        }

        frontiers.sort(new HeuristicComparator());
        for (Node<ActionNode> tempNode : frontiers) {
            int temp = iterativeSearch(gridMap, tempNode, threshold);
            if (temp == -1) return -1;
            if (temp < min) min = temp;
        }

        return min;
    }
}

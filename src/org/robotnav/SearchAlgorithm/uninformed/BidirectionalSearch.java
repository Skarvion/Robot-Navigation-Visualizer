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
import java.util.ListIterator;

public class BidirectionalSearch extends SearchAlgorithm {

    public BidirectionalSearch() {
        setSearchAlgorithmName(new String[]{"Bidirectional Search", "BDS", "BidirectionalSearch", "CUS1"});
    }

    @Override
    public ArrayList<Direction> executeSearch(Grid gridMap, Robot robot) {
        // Create two search tree
        Tree<ActionNode> searchTreeA = new Tree<>(new ActionNode(Direction.None, new Coordinate(robot.getxCoordinate(), robot.getyCoordinate())));
        Tree<ActionNode> searchTreeB = new Tree<>(new ActionNode(Direction.None, new Coordinate(gridMap.getGoalX(), gridMap.getGoalY())));

        // Create two list of frontiers
        LinkedList<Node<ActionNode>> frontiersA = new LinkedList<>();
        LinkedList<Node<ActionNode>> frontiersB = new LinkedList<>();
        frontiersA.add(searchTreeA.getRootNode());
        frontiersB.add(searchTreeB.getRootNode());
        setNodeCount(2);

        Node selectedNodeA = null;
        Node selectedNodeB = null;

        while (((selectedNodeA = frontiersA.pollFirst()) != null) && ((selectedNodeB = frontiersB.pollFirst()) != null)) {
            Direction sourceDirectionA = ((ActionNode) selectedNodeA.getData()).getMovement();
            Direction sourceDirectionB = ((ActionNode) selectedNodeB.getData()).getMovement();

            int xCoordinateA = ((ActionNode) selectedNodeA.getData()).getCoordinate().getX();
            int yCoordinateA = ((ActionNode) selectedNodeA.getData()).getCoordinate().getY();

            updateCellState(xCoordinateA, yCoordinateA, CellType.Visited);

            int xCoordinateB = ((ActionNode) selectedNodeB.getData()).getCoordinate().getX();
            int yCoordinateB = ((ActionNode) selectedNodeB.getData()).getCoordinate().getY();

            updateCellState(xCoordinateB, yCoordinateB, CellType.Visited);

            ArrayList<Direction> possibleDirectionsA = visitableDirection(gridMap, xCoordinateA, yCoordinateA, sourceDirectionA);
            ArrayList<Direction> possibleDirectionsB = visitableDirection(gridMap, xCoordinateB, yCoordinateB, sourceDirectionB);

            Node<ActionNode> matchNodeA = addFrontier(gridMap, frontiersA, frontiersB, possibleDirectionsA, selectedNodeA, xCoordinateA, yCoordinateA);
            Node<ActionNode> matchNodeB = addFrontier(gridMap, frontiersB, frontiersA, possibleDirectionsB, selectedNodeB, xCoordinateB, yCoordinateB);

            if (matchNodeA != null)  matchNodeB = findEqualNodeFrontier(frontiersB, matchNodeA);
            else if (matchNodeB != null) matchNodeA = findEqualNodeFrontier(frontiersA, matchNodeB);

            if (matchNodeA != null && matchNodeB != null) {
                updateCellState(matchNodeA.getData().getCoordinate().getX(), matchNodeA.getData().getCoordinate().getY(), CellType.Visited);
                ArrayList<Direction> fromStart = deriveSolutionFromNode(matchNodeA);
                ArrayList<Direction> toFinish = deriveSolutionFromNode(matchNodeB);

                int size = toFinish.size();
                for (int i = 0; i <  size / 2; i++) {
                    Direction tempDirection = toFinish.get(i);
                    toFinish.set(i, toFinish.get(size - i - 1));
                    toFinish.set(size - i - 1, tempDirection);
                }
                oppositeDirectionList(toFinish);

                fromStart.addAll(toFinish);
                return fromStart;
            }

        }

        return null;
    }

    private Node<ActionNode> findEqualNodeFrontier(LinkedList<Node<ActionNode>> frontiers, Node<ActionNode> node) {
        ListIterator<Node<ActionNode>> itr = frontiers.listIterator();

        while (itr.hasNext()) {
            if (itr.next().equals(node)) {
                return itr.previous();
            }
        }

        return null;
    }

    private Node<ActionNode> addFrontier(Grid gridMap, LinkedList<Node<ActionNode>> frontiers, LinkedList<Node<ActionNode>> frontiersB, ArrayList<Direction> possibleDirections, Node<ActionNode> selectedNode, int xCoordinate, int yCoordinate) {
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
                if (frontiersB.contains(discoveredNode)) {
                    return discoveredNode;
                }
            }

        }
        return null;
    }

    private void oppositeDirectionList(ArrayList<Direction> directionList) {
        for (int i = 0; i < directionList.size(); i++) {
            switch (directionList.get(i)) {
                case Up:
                    directionList.set(i, Direction.Down);
                    break;

                case Left:
                    directionList.set(i, Direction.Right);
                    break;

                case Down:
                    directionList.set(i, Direction.Up);
                    break;

                case Right:
                    directionList.set(i, Direction.Left);
                    break;
            }
        }
    }
}

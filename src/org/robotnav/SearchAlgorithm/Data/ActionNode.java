package org.robotnav.SearchAlgorithm.Data;

import org.robotnav.Robot.Direction;
import org.robotnav.Robot.Robot;

public class ActionNode {
    private Direction movement;
    private Coordinate coordinate;
    private int heuristic;
    private int cost;

    public ActionNode(Direction movement, Coordinate coordinate) {
        this.movement = movement;
        this.coordinate = coordinate;
        heuristic = 0;
        cost = 0;
    }

    public ActionNode(Direction movement, Coordinate coordinate, int heuristic) {
        this.movement = movement;
        this.coordinate = coordinate;
        this.heuristic = heuristic;
        cost = 0;
    }

    public ActionNode(Direction movement, Coordinate coordinate, int heuristic, int cost) {
        this.movement = movement;
        this.coordinate = coordinate;
        this.heuristic = heuristic;
        this.cost = cost;
    }

    public Direction getMovement() {
        return movement;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ActionNode) {
            ActionNode test = (ActionNode) obj;
            return (coordinate.equals(test.getCoordinate()) && heuristic == ((ActionNode) obj).getHeuristic());
        } else return false;
    }

    public void setMovement(Direction movement) {
        this.movement = movement;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public int getCost() { return cost; }

    public void setCost(int cost) { this.cost = cost; }
}

package org.robotnav.SearchAlgorithm.Data;

public class Coordinate {
    private int xCoordinate;
    private int yCoordinate;

    public Coordinate(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public int getX() {
        return xCoordinate;
    }

    public void setX(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getY() {
        return yCoordinate;
    }

    public void setY(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate) return (xCoordinate == ((Coordinate) obj).getX()) && (yCoordinate == ((Coordinate) obj).getY());
        else return false;
    }
}

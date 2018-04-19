package org.robotnav.gui.component;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.robotnav.gui.GUISettings;
import org.robotnav.map.CellType;
import org.robotnav.map.Data.MapData;

public class GridTile extends Rectangle {
    private CellType cellState;

    public GridTile() {
        setWidth(GUISettings.TILE_WIDTH);
        setHeight(GUISettings.TILE_HEIGHT);
        setStroke(Color.BLACK);
        cellState = CellType.Empty;
    }

    public GridTile(CellType cellState) {
        this();
        setState(cellState);
    }

    public void setState(CellType newCellState) {
        switch (cellState) {
            case Empty:
                cellState = newCellState;
                break;
            case Frontier:
                if (newCellState == CellType.Visited) cellState = newCellState;
                break;
            case Visited:
                if (newCellState == CellType.Result) cellState = newCellState;
                break;
            case Wall:
            case Robot:
            case Goal:
                break;
        }

        setFill(GUISettings.getColorCell(cellState));
    }

    public void emptyTile() {
        cellState = CellType.Empty;
    }

    public void resetTile() {
        if (cellState != CellType.Wall && cellState != CellType.Robot && cellState != CellType.Goal) {
            cellState = CellType.Empty;
            setFill(GUISettings.getColorCell(cellState));
        }
    }
}

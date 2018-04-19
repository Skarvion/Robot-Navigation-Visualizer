package org.robotnav.gui;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.robotnav.map.CellType;

import java.util.HashMap;
import java.util.Map;

public class GUISettings {
    public static final int TILE_WIDTH = 40;
    public static final int TILE_HEIGHT = 40;

    public static final Map<CellType, Paint> gridColor;

    static {
        gridColor = new HashMap<>();
        gridColor.put(CellType.Empty, Color.WHITE);
        gridColor.put(CellType.Wall, Color.GRAY);
        gridColor.put(CellType.Robot, Color.RED);
        gridColor.put(CellType.Goal, Color.BLUE);
        gridColor.put(CellType.Visited, Color.DARKGREEN);
        gridColor.put(CellType.Frontier, Color.ORANGE);
        gridColor.put(CellType.Result, Color.YELLOW);
    }

    public static Paint getColorCell(CellType cellType) { return gridColor.get(cellType); }
}
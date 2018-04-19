package org.robotnav.gui.controller;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.robotnav.SearchAlgorithm.SearchAlgorithm;
import org.robotnav.gui.component.GridTile;
import org.robotnav.map.CellType;
import org.robotnav.map.Grid;

import java.io.IOException;
import java.util.function.UnaryOperator;

/**
 * The controller class between View GUI and the Model of the search algorithm
 */
public class MapController extends HBox {

    @FXML
    private GridPane gridPane;

    @FXML
    private ComboBox<SearchAlgorithm> searchAlgorithmComboBox;

    @FXML
    private Button executeSearchButton;

    @FXML
    private Slider delaySlider;

    @FXML
    private TextField delayText;

    private Grid gridMap;
    private GridTile[][] mapTileArray;
    private SimpleLongProperty delayProperty;

    public MapController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/map.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            setupComponents();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public MapController(Grid gridMap) {
        this();
        this.gridMap = gridMap;
    }

    public void setGridMap(Grid gridMap) {
        this.gridMap = gridMap;
        populateGrid();
    }

    private void populateGrid() {
        gridPane.getChildren().clear();

        mapTileArray = new GridTile[gridMap.getWidth()][gridMap.getHeight()];

        if (gridMap != null) {
            for (int i = 0; i < gridMap.getWidth(); i++) {
                for (int j = 0; j < gridMap.getHeight(); j++) {
                    GridTile tile = new GridTile(gridMap.getCell(i, j).getType());
                    mapTileArray[i][j] = tile;
                    gridPane.add(tile, i, j);
                }
            }
        }
    }

    public void resetGrid() {
        for (int i = 0; i < gridMap.getWidth(); i++) {
            for (int j = 0; j < gridMap.getHeight(); j++) {
                mapTileArray[i][j].resetTile();
            }
        }
    }

    // Reference: https://gist.github.com/jewelsea/1962045
    private void setupComponents() {
        delayProperty = new SimpleLongProperty(0);
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.equals("")) return change;
            if (newText.matches("([0-9]{0,3})?")) {
                return change;
            }
            return null;
        };

        delayText.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
        delayText.setText(Long.toString(Math.round(delaySlider.getValue())));
        delaySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue == null) {
                    delayText.setText("");
                    return;
                }
                delayText.setText(Math.round(newValue.intValue()) + "");
            }
        });
        delayProperty.bind(delaySlider.valueProperty());

        delayText.setOnAction(e -> textFieldHandleSlider());
        delayText.focusedProperty().addListener(e -> textFieldHandleSlider());

        searchAlgorithmComboBox.setItems(FXCollections.observableArrayList(SearchAlgorithm.getSearchAlgorithms()));
        searchAlgorithmComboBox.setCellFactory(new Callback<ListView<SearchAlgorithm>, ListCell<SearchAlgorithm>>() {
            @Override
            public ListCell<SearchAlgorithm> call(ListView<SearchAlgorithm> param) {
                return new ListCell<SearchAlgorithm>() {
                    @Override
                    protected void updateItem(SearchAlgorithm item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getFirstName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        searchAlgorithmComboBox.setConverter(new StringConverter<SearchAlgorithm>() {
            @Override
            public String toString(SearchAlgorithm object) {
                return object.getFirstName();
            }

            @Override
            public SearchAlgorithm fromString(String string) {
                return null;
            }
        });

        executeSearchButton.setOnAction(e -> {
            populateGrid();

            Task searchTask = new Task() {
                @Override
                protected Object call() throws Exception {
                    SearchAlgorithm selectedSA = searchAlgorithmComboBox.getValue();
                    selectedSA.setViewController(MapController.this);
                    selectedSA.setMsDelay(delayProperty.getValue());
                    executeSearchButton.setDisable(true);
                    selectedSA.executeSearch(gridMap, gridMap.getRobot());
                    executeSearchButton.setDisable(false);
                    return null;
                }
            };
            new Thread(searchTask).start();
        });
    }

    private void textFieldHandleSlider() {
        try {
            if (delayText.getText() == null || delayText.getText().equals("")) {
                delayText.setText(Long.toString(delayProperty.getValue()));
                return;
            }

            Long numLong = Long.parseLong(delayText.getText());

            if (numLong > delaySlider.getMax()) {
                numLong = (long) delaySlider.getMax();
                delayText.setText(Long.toString(numLong));
            }

            if (numLong >= delaySlider.getMin() && numLong <= delaySlider.getMax()) {
                delaySlider.setValue(numLong);
                delayText.setText(Long.toString(numLong));
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            delayText.setText("0");
        }
    }

    public void updateTile(int x, int y, CellType cellState) {

        mapTileArray[x][y].setState(cellState);
    }
}


package com.example.project1demo.controller;

import com.example.project1demo.MainApplication;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class GraphController {
    @FXML
    private Label coordinate;
    private TextField activeTextField;
    private static Map<String, XYChart.Series<Number, Number>> mappingIDAndDataSeries;
    private static Map<String, XYChart.Series<Number, Number>> mappingFunctionAndSeries;
    private static Map<String, Expression> mappingNameSeriesAndExpression;

    private int countFunction = 1;
    private int countValidFunction = 1;
    @FXML
    private VBox leftVBox;
    @FXML
    private Group groupInput;
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    private double maxUpperBounds;
    private double minLowerBounds;
    private double maxUpperBoundsOnPan;
    private double minLowerBoundsOnPan;

    public GraphController() {
        mappingFunctionAndSeries = new HashMap<>();
        mappingIDAndDataSeries = new HashMap<>();
        mappingNameSeriesAndExpression = new HashMap<>();
        maxUpperBounds = Double.MIN_VALUE;
        minLowerBounds = Double.MAX_VALUE;
        maxUpperBoundsOnPan = Double.MIN_VALUE;
        minLowerBoundsOnPan = Double.MAX_VALUE;
    }

    @FXML
    protected void handleOnAddFunction() {
        Group group = new Group();

        TextField textFieldInputFunction = (TextField) groupInput.getChildren().getFirst();
        TextField textField = new TextField();
        textField.setPrefHeight(textFieldInputFunction.getPrefHeight());
        textField.setPrefWidth(textFieldInputFunction.getPrefWidth());
        textField.setOnKeyTyped(this::handleOnInputFunction);
        textField.setOnMouseClicked(this::handleAppendText);

        Button bt = new Button();
        Button buttonDeleteFunction = (Button) groupInput.getChildren().getLast();
        bt.setLayoutX(buttonDeleteFunction.getLayoutX());
        bt.setLayoutY(buttonDeleteFunction.getLayoutY());
        bt.setOnAction(this::handleOnDeleteFunction);
        bt.setText("x");
        group.getChildren().addAll(textField, bt);
        group.setId("groupInput" + countFunction);
        leftVBox.getChildren().add(group);
        countFunction++;
        countValidFunction++;
    }

    @FXML
    protected void handleOnDeleteFunction(ActionEvent event) {
        Button bt = (Button) event.getSource();
        Group group = (Group) bt.getParent();
        if (countValidFunction > 1) {
            leftVBox.getChildren().remove(group);
            lineChart.getData().remove(mappingIDAndDataSeries.get(group.getId()));
            countValidFunction--;
        }
    }

    @FXML
    public void handleOnInputFunction(KeyEvent event) {
        TextField textField = (TextField) event.getSource();
        String key = textField.getParent().getId();
        activeTextField = (TextField) event.getSource();
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> Platform.runLater(() -> updateChartData(textField.getText(), key)));

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.isEmpty() || textField.getText().isEmpty()) {
                    pause.playFromStart(); // Bắt đầu lại thời gian đợi khi giá trị thay đổi
                }
            } catch (Exception exception) {
                System.out.println("Error");
            }
        });

    }

    @FXML
    public void handleAppendText(MouseEvent event) {
        activeTextField = (TextField) event.getSource();
        activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
    }

    private void updateChartData(String expressionText, String key) {
        try {
            lineChart.getData().remove(mappingIDAndDataSeries.get(key));
            Expression expression = new ExpressionBuilder(expressionText).variable("x").build();
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            // Add tick marks to the axes
            resetVariable();
            minLowerBounds = Double.MAX_VALUE;
            maxUpperBoundsOnPan = xAxis.getUpperBound();
            minLowerBoundsOnPan = xAxis.getLowerBound();
            double lowerBound = xAxis.getLowerBound();
            double upperBound = xAxis.getUpperBound();
            double step = 0.01;

            if (mappingFunctionAndSeries.get(expressionText) == null) {

                for (double x = lowerBound - 2; x <= upperBound + 2; x += step) {
                    updateSeries(expression, series, x);
                }
                series.setName(expressionText + "(" + countValidFunction + ")");
                mappingNameSeriesAndExpression.put(series.getName(), expression);
                mappingFunctionAndSeries.put(expressionText, series);
            } else {
                series = mappingFunctionAndSeries.get(expressionText);
            }
            mappingIDAndDataSeries.put(key, series);
            lineChart.getData().add(series);
            series.getNode().setOnMousePressed(this::handle);
            series.getNode().setStyle("-fx-stroke-width: 1;");
        } catch (Exception e) {
            System.out.println("Error evaluating expression: " + expressionText);

        }
    }

    public static void updateSeries(Expression expression, XYChart.Series<Number, Number> series, double x) {
        double value = expression.setVariable("x", x).evaluate();
        NormalDistributionController.createDataPoint(series, x, value);
    }

    private double start_x, start_y, xScale, yScale;

    @FXML
    public void handleOnMousePressed(MouseEvent event) {
        start_x = event.getSceneX();
        start_y = event.getSceneY();
        xScale = xAxis.getScale();
        yScale = yAxis.getScale();
    }

    @FXML
    public void handlePanGraph(MouseEvent event) {
        coordinate.setVisible(false);
        double deltaX = (event.getSceneX() - start_x) / xScale;
        double deltaY = (event.getSceneY() - start_y) / yScale;

        oldUpperBoundX = xAxis.getUpperBound();
        oldLowerBoundX = xAxis.getLowerBound();
        xAxis.setLowerBound(xAxis.getLowerBound() - deltaX);
        xAxis.setUpperBound(xAxis.getUpperBound() - deltaX);
        yAxis.setLowerBound(yAxis.getLowerBound() - deltaY);
        yAxis.setUpperBound(yAxis.getUpperBound() - deltaY);
        //sau khi xong buoc nay thi bat dau update do thi
        double lowerBound = xAxis.getLowerBound();
        double upperBound = xAxis.getUpperBound();
        double steps = (upperBound - lowerBound) / 2000;
        if (deltaX < 0) {
            if (maxUpperBoundsOnPan < upperBound) {
                updateLineChartOnPan(oldUpperBoundX, upperBound, steps);
                maxUpperBoundsOnPan = upperBound;
            }
        } else {
            if (minLowerBoundsOnPan > lowerBound) {
                updateLineChartOnPan(lowerBound, oldLowerBoundX, steps);
                minLowerBoundsOnPan = lowerBound;
            }
        }
        start_x = event.getSceneX();
        start_y = event.getSceneY();
    }

    private void updateLineChartOnPan(double old_value, double new_value, double steps) {
        for (double x = old_value; x <= new_value; x += steps) {
            for (XYChart.Series<Number, Number> series : lineChart.getData()) {
                Expression expression = mappingNameSeriesAndExpression.get(series.getName());
                double value = expression.setVariable("x", x).evaluate();
                Circle circle = new Circle(0);
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, value);
                dataPoint.setNode(circle);
                series.getData().add(dataPoint);
            }
        }
    }

    private double oldLowerBoundX;
    private double oldUpperBoundX;
    //Tao bien do luu lai max gia tri cua con lan chuot

    @FXML
    public void handleOnScroll(ScrollEvent event) {
        coordinate.setVisible(false);
        if (event.getDeltaY() == 0) {
            return;
        }
        double scaleFactor = (event.getDeltaY() < 0) ? 1.05 : 1 / 1.05;
        double xValue = xAxis.getValueForDisplay(event.getX()).doubleValue();
        double yValue = yAxis.getValueForDisplay(event.getY()).doubleValue();

        oldLowerBoundX = xAxis.getLowerBound();
        oldUpperBoundX = xAxis.getUpperBound();
        xAxis.setLowerBound(scaleFactor * (xAxis.getLowerBound() - xValue) + xValue);
        xAxis.setUpperBound(scaleFactor * (xAxis.getUpperBound() - xValue) + xValue);
        yAxis.setLowerBound(scaleFactor * (yAxis.getLowerBound() - yValue) + yValue);
        yAxis.setUpperBound(scaleFactor * (yAxis.getUpperBound() - yValue) + yValue);
        xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 20);
        yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 20);
        double lowerBound = xAxis.getLowerBound();
        double upperBound = xAxis.getUpperBound();
        if (scaleFactor > 1 && maxUpperBounds < upperBound) {
            double steps = (upperBound - lowerBound) / 2000;
            for (double x = oldUpperBoundX; x <= upperBound && upperBound > oldUpperBoundX; x += steps) {
                for (XYChart.Series<Number, Number> series : lineChart.getData()) {
                    Expression expression = mappingNameSeriesAndExpression.get(series.getName());

//                series.getData().add(new XYChart.Data<>(x, expression.setVariable("x", x).evaluate()));
                    double value = expression.setVariable("x", x).evaluate();
                    XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, value);
                    dataPoint.setNode(new Circle(1));
                    dataPoint.getNode().setVisible(false);
                    series.getData().add(dataPoint);
                }
            }
            maxUpperBounds = Math.max(upperBound, maxUpperBounds);
        }
        if(scaleFactor > 1 && minLowerBounds > lowerBound){
            double steps = (upperBound - lowerBound) / 2000;
            for (double x = lowerBound; x <= oldLowerBoundX && oldLowerBoundX > lowerBound; x += steps) {

                for (XYChart.Series<Number, Number> series : lineChart.getData()) {
                    Expression expression = mappingNameSeriesAndExpression.get(series.getName());

//                series.getData().add(new XYChart.Data<>(x, expression.setVariable("x", x).evaluate()));
                    double value = expression.setVariable("x", x).evaluate();
                    XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, value);
                    dataPoint.setNode(new Circle(1)); // Example: blue circle with radius 5
                    dataPoint.getNode().setVisible(false);
                    series.getData().add(dataPoint);
                }
            }
            minLowerBounds = Math.min(minLowerBounds, lowerBound);
        }

    }

    @FXML
    public void initialize() {
        xAxis.setLowerBound(-5);
        xAxis.setUpperBound(5);
        yAxis.setUpperBound(10);
        yAxis.setLowerBound(-10);
        xAxis.setTickUnit(1);
        yAxis.setTickUnit(1);
        maxUpperBounds = Double.MIN_VALUE;
    }

    @FXML
    public void switchToNormalDistribution(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("normal-distribution-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 770, 350);
            stage.setTitle("NormalDistribution");
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            System.out.println("ErrorIOE");
        }
    }

    @FXML
    public void handleAddExp() {
        try {
            activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
            activeTextField.appendText("e");
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    @FXML
    public void handleAddPi() {
        try {
            activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
            activeTextField.appendText("π");
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    @FXML
    public void handleAddCot() {
        try {
            activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
            activeTextField.appendText("cot");
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    @FXML
    public void handleAddSin() {
        try {
            activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
            activeTextField.appendText("sin");
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    @FXML
    public void handleAddCos() {
        try {
            activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
            activeTextField.appendText("cos");
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    @FXML
    public void handleAddTan() {
        try {
            activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
            activeTextField.appendText("tan");
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    @FXML
    public void handleAddArcsin() {
        try {
            activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
            activeTextField.appendText("asin");
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    @FXML
    public void handleAddArccos() {
        try {
            activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
            activeTextField.appendText("acos");
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    @FXML
    public void handleAddArctan() {
        try {
            activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
            activeTextField.appendText("atan");
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    @FXML
    public void handleAddSinh() {
        try {
            activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
            activeTextField.appendText("sinh");
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    @FXML
    public void handleAddCosh() {
        try {
            activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
            activeTextField.appendText("cosh");
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    @FXML
    public void handleAddAbs() {
        try {
            activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
            activeTextField.appendText("abs");
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    @FXML
    public void handleAddSqrt() {
        try {
            activeTextField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.ENTER, false, false, false, false));
            activeTextField.appendText("sqrt");
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    public void handleOnHome() {
        // Add tick marks to the axes
        resetVariable();
    }
    public void resetVariable(){
        xAxis.setTickUnit(1);
        yAxis.setTickUnit(1);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        xAxis.setLowerBound(-10);
        xAxis.setUpperBound(10);
        yAxis.setLowerBound(-5);
        yAxis.setUpperBound(5);
        maxUpperBounds = Double.MIN_VALUE;
    }
    @FXML
    public void handleOnSolve(ActionEvent event){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("table-function-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 1200, 500);
            stage.setTitle("Table Function");
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            System.out.println("ErrorIOE");
        }
    }
    @FXML
    public void handleReadDocument(ActionEvent event){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("about-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 1200, 500);
            stage.setTitle("About");
            stage.setScene(scene);
            stage.show();
        } catch (Exception exception) {
            System.out.println("ErrorIOE");
        }
    }

    private void handle(MouseEvent event) {
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
        pauseTransition.setOnFinished(event1 -> coordinate.setVisible(false));
        double xValue = xAxis.getValueForDisplay(event.getX()).doubleValue();
        double yValue = yAxis.getValueForDisplay(event.getY()).doubleValue();
        coordinate.setText(String.format("(%.6f, %.6f)", xValue, yValue));

// Đảm bảo Label không vượt quá biên của LineChart
        double chartWidth = lineChart.getWidth();
        double chartHeight = lineChart.getHeight();
        coordinate.setLayoutX(Math.min(event.getX(), chartWidth - coordinate.getWidth()));
        coordinate.setLayoutY(Math.min(event.getY(), chartHeight - coordinate.getHeight()));
        coordinate.setVisible(true);
        pauseTransition.playFromStart();
    }
}

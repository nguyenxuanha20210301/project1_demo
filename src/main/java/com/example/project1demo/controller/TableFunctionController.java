package com.example.project1demo.controller;

import com.example.project1demo.MainApplication;
import com.example.project1demo.model.DataPoint;
import com.example.project1demo.model.ResultFunctionSolve;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;

import java.io.IOException;
import java.text.DecimalFormat;

public class TableFunctionController {
    private double maxUpperBoundsOnPan;
    private double minLowerBoundsOnPan;

    XYChart.Series<Number, Number> series;
    XYChart.Series<Number, Number> series1;
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    public TextField minX;
    @FXML
    public TextField maxX;
    @FXML
    public TextField steps;
    @FXML
    private TableView<ResultFunctionSolve> tableView1;
    @FXML
    public TableColumn<ResultFunctionSolve, String> YCol1;
    @FXML
    private TextField function;
    @FXML
    private TableView<DataPoint> tableView;
    @FXML
    private TableColumn<DataPoint, Double> XCol;
    @FXML
    private TableColumn<DataPoint, Double> YCol;
    @FXML
    private Label coordinate;
    private final ObservableList<DataPoint> data = FXCollections.observableArrayList();
    private final ObservableList<ResultFunctionSolve> data1 = FXCollections.observableArrayList();
    public TableFunctionController() {
        series = new XYChart.Series<>();
        series1 = new XYChart.Series<>();
    }

    @FXML
    private NumberAxis xAxis1;
    @FXML
    private NumberAxis yAxis1;
    private double maxUpperBounds1;
    private double minLowerBounds1;
    private double maxUpperBoundsOnPan1;
    private double minLowerBoundsOnPan1;
    private double start_x1, start_y1, xScale1, yScale1;
    public void initialize() {
        XCol.setCellValueFactory(new PropertyValueFactory<>("x"));
        YCol.setCellValueFactory(new PropertyValueFactory<>("y"));
        YCol1.setCellValueFactory(new PropertyValueFactory<>("result"));
        tableView.setItems(data);
        tableView1.setItems(data1);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        xAxis.setTickUnit(1);
        yAxis.setTickUnit(1);
        xAxis.setUpperBound(10);
        xAxis.setLowerBound(-10);
        yAxis.setUpperBound(5);
        yAxis.setLowerBound(-5);
        maxUpperBounds = Double.MIN_VALUE;
        minLowerBounds = Double.MAX_VALUE;
        maxUpperBoundsOnPan = Double.MIN_VALUE;
        minLowerBoundsOnPan = Double.MAX_VALUE;
        xAxis1.setAutoRanging(false);
        yAxis1.setAutoRanging(false);
        xAxis1.setTickUnit(1);
        yAxis1.setTickUnit(1);
        xAxis1.setUpperBound(10);
        xAxis1.setLowerBound(-10);
        yAxis1.setUpperBound(5);
        yAxis1.setLowerBound(-5);
        maxUpperBounds1 = Double.MIN_VALUE;
        minLowerBounds1 = Double.MAX_VALUE;
        maxUpperBoundsOnPan1 = Double.MIN_VALUE;
        minLowerBoundsOnPan1 = Double.MAX_VALUE;
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
    public void handleOnMousePressed1(MouseEvent event) {
        start_x1 = event.getSceneX();
        start_y1 = event.getSceneY();
        xScale1 = xAxis1.getScale();
        yScale1 = yAxis1.getScale();
    }
    @FXML
    public void handlePanGraph(MouseEvent event) {
        try{
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
        }catch (Exception error){
            System.out.println("Error handlePanGraph");
        }

    }
    @FXML
    private Label coordinate1;
    private double oldUpperBoundX1;
    private double oldLowerBoundX1;
    @FXML
    public void handlePanGraph1(MouseEvent event) {
        try{
            coordinate1.setVisible(false);
            double deltaX = (event.getSceneX() - start_x1) / xScale1;
            double deltaY = (event.getSceneY() - start_y1) / yScale1;

            oldUpperBoundX1 = xAxis1.getUpperBound();
            oldLowerBoundX1 = xAxis1.getLowerBound();
            xAxis1.setLowerBound(xAxis1.getLowerBound() - deltaX);
            xAxis1.setUpperBound(xAxis1.getUpperBound() - deltaX);
            yAxis1.setLowerBound(yAxis1.getLowerBound() - deltaY);
            yAxis1.setUpperBound(yAxis1.getUpperBound() - deltaY);
            //sau khi xong buoc nay thi bat dau update do thi
            double lowerBound = xAxis1.getLowerBound();
            double upperBound = xAxis1.getUpperBound();
            double steps = (upperBound - lowerBound) / 2000;
            if (deltaX < 0) {
                if (maxUpperBoundsOnPan1 < upperBound) {
                    updateLineChartOnPan1(oldUpperBoundX1, upperBound, steps);
                    maxUpperBoundsOnPan1 = upperBound;
                }
            } else {
                if (minLowerBoundsOnPan1 > lowerBound) {
                    updateLineChartOnPan1(lowerBound, oldLowerBoundX1, steps);
                    minLowerBoundsOnPan1 = lowerBound;
                }
            }
            start_x1 = event.getSceneX();
            start_y1 = event.getSceneY();
        }catch (Exception error){
            System.out.println("Error handlePanGraph1");
        }

    }
    private void updateLineChartOnPan(double old_value, double new_value, double steps) {
        try{
            Expression expression = new ExpressionBuilder(function.getText()).variable("x").build();
            for (double x = old_value; x <= new_value; x += steps) {
                double value = expression.setVariable("x", x).evaluate();
                Circle circle = new Circle(1);
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, value);
                dataPoint.setNode(circle);
                dataPoint.getNode().setVisible(false);
                series.getData().add(dataPoint);
            }
        }catch (Exception error){
            System.out.println("Error updateLineChartOnPan");
        }

    }
    private void updateLineChartOnPan1(double old_value, double new_value, double steps) {
        try{
            double deltaX = 1e-8;
            for (double x = old_value; x <= new_value; x += steps) {
                double value = calculateDerivative(x, deltaX);
                Circle circle = new Circle(1);
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, value);
                dataPoint.setNode(circle);
                dataPoint.getNode().setVisible(false);
                series1.getData().add(dataPoint);
            }
        }catch (Exception error){
            System.out.println("updateLineChartOnPan1 error");
        }

    }
    @FXML
    public void handleOnDrawFunctionGraph(){
        String expressionText = function.getText();
        try {
            maxUpperBounds = Double.MIN_VALUE;
            minLowerBounds = Double.MAX_VALUE;
            maxUpperBoundsOnPan = xAxis.getUpperBound();
            minLowerBoundsOnPan = xAxis.getLowerBound();
            lineChart.getData().clear();
            series.getData().clear();
            Expression expression = new ExpressionBuilder(expressionText).variable("x").build();
            double lowerBound = xAxis.getLowerBound();
            double upperBound = xAxis.getUpperBound();
            double step = 0.005;
            for (double x = lowerBound - 1; x <= upperBound + 1; x += step) {
                GraphController.updateSeries(expression, series, x);
            }
            series.setName(expressionText);
            lineChart.getData().add(series);
            series.getNode().setOnMousePressed(e -> {
                PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
                pauseTransition.setOnFinished(event1 -> coordinate.setVisible(false));
                double xValue = xAxis.getValueForDisplay(e.getX()).doubleValue();
                double yValue = yAxis.getValueForDisplay(e.getY()).doubleValue();
                coordinate.setText(String.format("(%.6f, %.6f)", xValue, yValue));

                // Đảm bảo Label không vượt quá biên của LineChart
                double chartWidth = lineChart.getWidth();
                double chartHeight = lineChart.getHeight();
                coordinate.setLayoutX(Math.min(e.getX(), chartWidth - coordinate.getWidth()));
                coordinate.setLayoutY(Math.min(e.getY() + 100, chartHeight - coordinate.getHeight() + 100));
                coordinate.setVisible(true);
                pauseTransition.playFromStart();
            });
            series.getNode().setStyle("-fx-stroke-width: 1;");
        }catch (Exception error){
            System.out.println("Error evaluating expression: " + expressionText);
        }
    }
    @FXML
    private LineChart<Number, Number> lineChart1;
    @FXML
    public void handleOnDrawDerivative(){
        String expressionText = function.getText();
        try {
            maxUpperBounds1 = Double.MIN_VALUE;
            minLowerBounds1 = Double.MAX_VALUE;
            maxUpperBoundsOnPan1 = xAxis.getUpperBound();
            minLowerBoundsOnPan1 = xAxis.getLowerBound();
            lineChart1.getData().clear();
            series1.getData().clear();

            double lowerBound = xAxis1.getLowerBound();
            double upperBound = xAxis1.getUpperBound();
            double deltaX = 1e-8;
            double step = 0.005;
            for (double x = lowerBound - 1; x <= upperBound + 1; x += step) {
                double value = calculateDerivative(x, deltaX);
                NormalDistributionController.createDataPoint(series1, x, value);
            }
            series1.setName("("+expressionText+")'");
            lineChart1.getData().add(series1);
            series1.getNode().setOnMousePressed(e -> {
                PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
                pauseTransition.setOnFinished(event1 -> coordinate1.setVisible(false));
                double xValue = xAxis1.getValueForDisplay(e.getX()).doubleValue();
                double yValue = yAxis1.getValueForDisplay(e.getY()).doubleValue();
                coordinate1.setText(String.format("(%.6f, %.6f)", xValue, yValue));

                // Đảm bảo Label không vượt quá biên của LineChart
                double chartWidth = lineChart1.getWidth();
                double chartHeight = lineChart1.getHeight();
                coordinate1.setLayoutX(Math.min(e.getX(), chartWidth - coordinate.getWidth()));
                coordinate1.setLayoutY(Math.min(e.getY() + 100, chartHeight - coordinate.getHeight() + 100));
                coordinate1.setVisible(true);
                pauseTransition.playFromStart();
            });
            series1.getNode().setStyle("-fx-stroke-width: 1;");
        }catch (Exception error){
            System.out.println("Error evaluating expression: " + expressionText);
        }
    }
    private double oldLowerBoundX;
    private double oldUpperBoundX;
    private double maxUpperBounds;
    private double minLowerBounds;
    @FXML
    public void handleOnScroll(ScrollEvent event) {
        try{
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
            double steps = (upperBound - lowerBound) / 2000;
            Expression expression = new ExpressionBuilder(function.getText()).variable("x").build();
            if (scaleFactor > 1 && maxUpperBounds < upperBound) {
                for (double x = oldUpperBoundX; x <= upperBound && upperBound > oldUpperBoundX; x += steps) {

                    double value = expression.setVariable("x", x).evaluate();
                    XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, value);
                    dataPoint.setNode(new Circle(2));
                    dataPoint.getNode().setVisible(false);
                    series.getData().add(dataPoint);

                }
                maxUpperBounds = Math.max(upperBound, maxUpperBounds);
            }
            if (scaleFactor > 1 && minLowerBounds > lowerBound){
                for (double x = lowerBound; x <= oldLowerBoundX && oldLowerBoundX > lowerBound; x += steps) {
                    double value = expression.setVariable("x", x).evaluate();
                    XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, value);
                    dataPoint.setNode(new Circle(2)); // Example: blue circle with radius 5
                    dataPoint.getNode().setVisible(false);
                    series.getData().add(dataPoint);
                }
                minLowerBounds = Math.min(lowerBound, minLowerBounds);
            }
        }catch (Exception error){
            System.out.println("Error Scroll");
        }

    }

    @FXML
    public void handleOnCentre1(){
        xAxis1.setTickUnit(1);
        yAxis1.setTickUnit(1);
        // Set fixed bounds for x and y axes
        xAxis1.setAutoRanging(false);
        yAxis1.setAutoRanging(false);
        xAxis1.setLowerBound(-10);
        xAxis1.setUpperBound(10);
        yAxis1.setLowerBound(-5);
        yAxis1.setUpperBound(5);
        maxUpperBounds1 = Double.MIN_VALUE;
        minLowerBounds1 = Double.MAX_VALUE;
        maxUpperBoundsOnPan1 = Double.MIN_VALUE;
        minLowerBoundsOnPan1 = Double.MAX_VALUE;
    }
    @FXML
    public void handleOnScroll1(ScrollEvent event) {
        try{
            coordinate1.setVisible(false);
            if (event.getDeltaY() == 0) {
                return;
            }
            double scaleFactor = (event.getDeltaY() < 0) ? 1.05 : 1 / 1.05;
            double xValue = xAxis1.getValueForDisplay(event.getX()).doubleValue();
            double yValue = yAxis1.getValueForDisplay(event.getY()).doubleValue();

            oldLowerBoundX1 = xAxis1.getLowerBound();
            oldUpperBoundX1 = xAxis1.getUpperBound();
            xAxis1.setLowerBound(scaleFactor * (xAxis1.getLowerBound() - xValue) + xValue);
            xAxis1.setUpperBound(scaleFactor * (xAxis1.getUpperBound() - xValue) + xValue);
            yAxis1.setLowerBound(scaleFactor * (yAxis1.getLowerBound() - yValue) + yValue);
            yAxis1.setUpperBound(scaleFactor * (yAxis1.getUpperBound() - yValue) + yValue);
            xAxis1.setTickUnit((xAxis1.getUpperBound() - xAxis1.getLowerBound()) / 20);
            yAxis1.setTickUnit((yAxis1.getUpperBound() - yAxis1.getLowerBound()) / 20);
            double lowerBound = xAxis1.getLowerBound();
            double upperBound = xAxis1.getUpperBound();
            double deltaX = 1e-8;
            double steps = (upperBound - lowerBound) / 2000;
            if (scaleFactor > 1 && maxUpperBounds1 < upperBound) {
                for (double x = oldUpperBoundX1; x <= upperBound && upperBound > oldUpperBoundX1; x += steps) {
                    double value = calculateDerivative(x, deltaX);
                    XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, value);
                    dataPoint.setNode(new Circle(2));
                    dataPoint.getNode().setVisible(false);
                    series1.getData().add(dataPoint);

                }
                maxUpperBounds1 = Math.max(upperBound, maxUpperBounds1);
            }
            if (scaleFactor > 1 && minLowerBounds1 > lowerBound){
                for (double x = lowerBound; x <= oldLowerBoundX1 && oldLowerBoundX1 > lowerBound; x += steps) {
                    double value = calculateDerivative(x, deltaX);
                    XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, value);
                    dataPoint.setNode(new Circle(2)); // Example: blue circle with radius 5
                    dataPoint.getNode().setVisible(false);
                    series1.getData().add(dataPoint);
                }
                minLowerBounds1 = Math.min(lowerBound, minLowerBounds1);
            }
        }catch (Exception error){
            System.out.println("Error Scroll");
        }
    }
    @FXML
    private void handleOnSolve() {
        try {
            data.clear();
            Expression expression = new ExpressionBuilder(function.getText()).variable("x").build();
            double min_x = Double.parseDouble(minX.getText());
            double max_x = Double.parseDouble(maxX.getText());
            double step = Double.parseDouble(steps.getText());
            for (double x = min_x; x <= max_x; x += step) {
                double value = expression.setVariable("x", x).evaluate();
                data.add(new DataPoint(x, value));
            }
            tableView.setItems(data);
        } catch (Exception error) {
            System.out.println("Error");
        }
    }

    @FXML
    public void switchToHome(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
            stage.setTitle("Graph Application");
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            System.out.println("ErrorIOE");
        }
    }
    @FXML
    public void handleOnHome() {
        xAxis.setTickUnit(1);
        yAxis.setTickUnit(1);
        // Set fixed bounds for x and y axes
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        xAxis.setLowerBound(-10);
        xAxis.setUpperBound(10);
        yAxis.setLowerBound(-5);
        yAxis.setUpperBound(5);
        maxUpperBounds = Double.MIN_VALUE;
        minLowerBounds = Double.MAX_VALUE;
        maxUpperBoundsOnPan = Double.MIN_VALUE;
        minLowerBoundsOnPan = Double.MAX_VALUE;
    }
    @FXML
    private TextField startX;
    @FXML
    private TextField endX;

    @FXML
    public void handleOnSolveFunction(ActionEvent event) {
        Button bt = (Button) event.getSource();
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1.25));
        pauseTransition.setOnFinished(event1 -> bt.getScene().setCursor(Cursor.DEFAULT));
        try {
            bt.getScene().setCursor(Cursor.WAIT);
            data1.clear();
            Expression expression = new ExpressionBuilder(function.getText()).variable("x").build();
            // Định nghĩa hàm số
            // Define the differentiable function
            UnivariateFunction function0 = x -> expression.setVariable("x", x).evaluate();

            BisectionSolver solver = new BisectionSolver();
            // Đặt giới hạn cho số lần lặp và độ chính xác
            int maxIterations = 100;
            double tolerance = 1e-6;
            double from_x = Double.parseDouble(startX.getText());
            double to_x = Double.parseDouble(endX.getText());
            // Tìm nghiệm
            // Initialize the previous result
            double prevResult = Double.NaN;
            double deltaX = 0.00001;
            double countIterations = (to_x - from_x) / deltaX;
            for (int i = 0; i < countIterations; i += 1) {
                double result = solver.solve(maxIterations, function0, from_x + i * deltaX, from_x + i * deltaX + deltaX, tolerance);
                String formattedResult = new DecimalFormat("0.####").format(result);
                // Print the result only if the difference is greater than 1e-6
                if (!Double.isNaN(prevResult) && (expression.setVariable("x", result).evaluate() * expression.setVariable("x", prevResult).evaluate() < 0)) {
                    data1.add(new ResultFunctionSolve(formattedResult));
                }

                // Update the previous result
                prevResult = result;
            }
            tableView1.setItems(data1);
        } catch (Exception error) {
            System.out.println("Error on solve");
        } finally {
            pauseTransition.playFromStart();
        }

    }
    @FXML
    private Label resultDerivative;
    @FXML
    private TextField inputXValue;
    @FXML
    public void handleOnCalculateDerivative() {
        try{
            double xValue = Double.parseDouble(inputXValue.getText());
            double deltaX = 1e-8;
            double derivative = calculateDerivative(xValue, deltaX);
            String formattedResult = new DecimalFormat("0.#######").format(derivative);
            resultDerivative.setText(formattedResult);
        }catch (Exception error){
            System.out.println("Error on calculate derivative!");
        }

    }
    private double calculateDerivative(double x, double deltaX) {
        try{
            Expression expression = new ExpressionBuilder(function.getText()).variable("x").build();
            return (expression.setVariable("x",x + deltaX).evaluate() - expression.setVariable("x", x).evaluate()) / deltaX;
        }catch (Exception e){
            System.out.println("CalculateDerivative was not success!");
            return Double.NaN;
        }

    }
}



package com.example.project1demo.controller;

import com.example.project1demo.MainApplication;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.io.IOException;

public class NormalDistributionController {
    @FXML
    public Label coordinate;
    @FXML
    private TextField mean;
    @FXML
    private TextField deviation;
    private double m;
    private double d;
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @FXML
    private void initialize() {
        xAxis.setLowerBound(-5);
        xAxis.setUpperBound(5);
        yAxis.setUpperBound(5);
        yAxis.setLowerBound(-5);
        xAxis.setTickUnit(1);
        yAxis.setTickUnit(1);
    }

    @FXML
    public void handleOnInputMean() {
        try {
            String str = mean.getText();
            m = Double.parseDouble(str);
        } catch (Exception error) {
            System.out.println("Error");
        }
    }

    @FXML
    public void handleOnInputStandardDeviation() {
        try {
            String str = deviation.getText();
            d = Double.parseDouble(str);
        } catch (Exception error) {
            System.out.println("Error");
        }

    }

    private static void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Input error");
        alert.showAndWait();
    }

    @FXML
    public void handleOnDraw() {
        try {
            lineChart.getData().clear();
            double max_y = 1 / (d * Math.sqrt(2 * Math.PI)) + (1 / (d * Math.sqrt(2 * Math.PI))) / 10;
            double max_x = m + 3 * d + 2;
            xAxis.setUpperBound(max_x);
            xAxis.setLowerBound(-max_x);
            yAxis.setUpperBound(max_y);
            yAxis.setLowerBound(-1);
            NormalDistribution normalDistribution = new NormalDistribution(m, d);
            XYChart.Series<java.lang.Number, java.lang.Number> series = new XYChart.Series<>();
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> Platform.runLater(() -> {

                lineChart.getData().add(series);
                series.getNode().setOnMousePressed(event -> {
                    PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
                    pauseTransition.setOnFinished(event1 -> coordinate.setVisible(false));
                    double xValue = xAxis.getValueForDisplay(event.getX()).doubleValue();
                    double yValue = yAxis.getValueForDisplay(event.getY()).doubleValue();
                    coordinate.setText(String.format("(%.6f, %.6f)", xValue, yValue));

                    // Đảm bảo Label không vượt quá biên của LineChart
                    double chartWidth = lineChart.getWidth();
                    double chartHeight = lineChart.getHeight();
                    coordinate.setLayoutX(Math.min(event.getX() + 130, 130 + chartWidth - coordinate.getWidth()));
                    coordinate.setLayoutY(Math.min(event.getY() + 30, 30 + chartHeight - coordinate.getHeight()));
                    coordinate.setVisible(true);
                    pauseTransition.playFromStart();
                });
                series.getNode().setStyle("-fx-stroke-width: 1;");
            }));
            // Lấy màu từ Series
            // Set the fill color of the Circle to match the series color
            for (double x = -(m + 3 * d); x <= m + 3 * d; x += 0.001) {
//                    series.getData().add(new XYChart.Data<>(x, normalDistribution.density(x)));
                double value = normalDistribution.density(x);
                createDataPoint(series, x, value);
            }
            pause.playFromStart();
        } catch (Exception error) {
            showAlert();
        }
    }

    public static void createDataPoint(XYChart.Series<Number, Number> series, double x, double value) {
        XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, value);
        Circle circle = new Circle(1);
        dataPoint.setNode(circle);
        dataPoint.getNode().setVisible(false);
        series.getData().add(dataPoint);
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
}

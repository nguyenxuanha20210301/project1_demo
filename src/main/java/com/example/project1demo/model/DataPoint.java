package com.example.project1demo.model;

import javafx.beans.property.SimpleDoubleProperty;

public class DataPoint {
    private SimpleDoubleProperty x;
    private SimpleDoubleProperty y;
    public DataPoint(double xVal, double yVal){
        this.x = new SimpleDoubleProperty(xVal);
        this.y = new SimpleDoubleProperty(yVal);
    }

    public double getX() {
        return x.get();
    }

    public SimpleDoubleProperty xProperty() {
        return x;
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public double getY() {
        return y.get();
    }

    public SimpleDoubleProperty yProperty() {
        return y;
    }

    public void setY(double y) {
        this.y.set(y);
    }
}

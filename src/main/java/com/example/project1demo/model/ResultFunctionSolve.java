package com.example.project1demo.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class ResultFunctionSolve {
    private SimpleStringProperty result;
    public ResultFunctionSolve(String res){
        this.result = new SimpleStringProperty(res);
    }

    public String getResult() {
        return result.get();
    }

    public SimpleStringProperty resultProperty() {
        return result;
    }

    public void setResult(String result) {
        this.result.set(result);
    }
}

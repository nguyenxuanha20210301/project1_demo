package com.example.project1demo.controller;

import com.example.project1demo.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AboutController {
    @FXML
    public void handleOnHome(ActionEvent event){
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

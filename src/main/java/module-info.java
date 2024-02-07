module com.example.project1demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires exp4j;
    requires commons.math3;
    opens com.example.project1demo.model to javafx.base;

    opens com.example.project1demo.controller to javafx.fxml;
    exports com.example.project1demo;

}
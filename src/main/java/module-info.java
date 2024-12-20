module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.sql;
    requires jdk.jdi;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.domain to javafx.base;
    opens com.example.demo.Views.utils to javafx.base;
    exports com.example.demo;
    opens com.example.demo.controller to javafx.fxml;

}
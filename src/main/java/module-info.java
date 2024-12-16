module com.wrapper {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.wrapper.controllers to javafx.fxml;
    exports com.wrapper;
}

module com.wrapper {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.wrapper.controllers to javafx.fxml;
    exports com.wrapper;
}

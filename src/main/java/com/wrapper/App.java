package com.wrapper;

import com.wrapper.controllers.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
        FXMLLoader fxmlLoader = loadFXML("mainWindow");
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();

        ((MainWindowController) fxmlLoader.getController()).initialize();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml).load());
    }

    private static FXMLLoader loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/wrapper/fxml/" + fxml + ".fxml"));
        return fxmlLoader;
    }

    public static void main(String[] args) {
        launch();
    }

}
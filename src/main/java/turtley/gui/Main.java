package turtley.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import turtley.Turtley;

/**
 * Starts Turtley's JavaFX user interface.
 */
public class Main extends Application {

    private final Turtley turtley = new Turtley();

    /**
     * Loads and displays the main Turtley window.
     *
     * @param stage the primary application stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setTurtley(turtley);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Turtley interface.", exception);
        }
    }
}

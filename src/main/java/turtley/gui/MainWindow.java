package turtley.gui;

import java.io.IOException;
import java.io.InputStream;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import turtley.Turtley;

/**
 * Controls the main Turtley conversation window.
 */
public class MainWindow extends AnchorPane {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Turtley turtley;
    private final Image userImage = loadImage("/images/DaUser.png");
    private final Image turtleyImage = loadImage("/images/DaTurtley.png");

    /**
     * Binds the conversation scroll position to the height of its contents.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the Turtley instance that processes user commands.
     *
     * @param turtley the application logic used by this window.
     */
    public void setTurtley(Turtley turtley) {
        this.turtley = turtley;
    }

    /**
     * Sends the current input to Turtley and appends both dialog messages.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = turtley.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getTurtleyDialog(response, turtleyImage)
        );
        userInput.clear();
    }

    /**
     * Loads an image from the application's bundled resources.
     *
     * @param resourcePath the classpath path of the image.
     * @return the loaded image.
     */
    private static Image loadImage(String resourcePath) {
        try (InputStream inputStream = MainWindow.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing GUI image resource: " + resourcePath);
            }
            return new Image(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load GUI image resource: " + resourcePath, exception);
        }
    }
}

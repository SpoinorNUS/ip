package turtley.gui

import java.io.IOException
import java.util.Collections

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.text.Font

/**
 * Represents a dialog row containing a speaker image and message text.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box interface.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Creates a dialog row aligned as a user message.
     *
     * @param text the user message.
     * @param image the user avatar.
     * @return the user dialog row.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a dialog row aligned as a Turtley response.
     *
     * @param text the Turtley response.
     * @param image the Turtley avatar.
     * @return the Turtley dialog row.
     */
    public static DialogBox getTurtleyDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a Turtley welcome row with fixed-width text for aligned ASCII art.
     *
     * @param text the Turtley welcome message.
     * @param image the Turtley avatar.
     * @return the Turtley welcome row.
     */
    public static DialogBox getTurtleyWelcomeDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.dialog.setFont(Font.font("Monospaced"));
        dialogBox.dialog.setWrapText(false);
        return dialogBox;
    }

    /**
     * Flips the row so that the avatar appears before the response text.
     */
    private void flip() {
        ObservableList<Node> nodes = FXCollections.observableArrayList(getChildren());
        Collections.reverse(nodes);
        getChildren().setAll(nodes);
        setAlignment(Pos.TOP_LEFT);
    }
}

package turtley.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Represents a dialog row containing a speaker image and message text.
 */
public class DialogBox extends HBox {

    private static final double TYPING_INTERVAL_MILLIS = 20.0;
    private static final double BOB_DISTANCE = 6.0;
    private static final double BOB_DURATION_MILLIS = 300.0;
    private static final double ROTATION_ANGLE = 12.0;
    private static final String VOICE_RESOURCE = "/images/TurtleyVoice.mp4";

    private static Media voiceMedia;
    private static MediaPlayer activeVoicePlayer;
    private static DialogBox activeVoiceOwner;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private Timeline typingAnimation;
    private TranslateTransition bobAnimation;
    private RotateTransition rotationAnimation;
    private MediaPlayer voicePlayer;
    private boolean isPlaybackStarted;

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
     * Creates a dialog row aligned as a Turtley response and types it out.
     *
     * @param text the Turtley response.
     * @param image the Turtley avatar.
     * @return the animated Turtley dialog row.
     */
    public static DialogBox getTurtleyDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox("", image);
        dialogBox.flip();
        dialogBox.startTyping(text);
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
        DialogBox dialogBox = getTurtleyDialog(text, image);
        dialogBox.dialog.setFont(Font.font("Monospaced"));
        dialogBox.dialog.setWrapText(false);
        return dialogBox;
    }

    /**
     * Starts revealing the dialog text and animating the avatar one character at a time.
     *
     * @param text the complete dialog text.
     */
    private void startTyping(String text) {
        stopAnimations();
        stopActiveVoicePlayer();
        isPlaybackStarted = false;

        dialog.setText("");
        if (text.isEmpty()) {
            return;
        }

        bobAnimation = new TranslateTransition(Duration.millis(BOB_DURATION_MILLIS), displayPicture);
        bobAnimation.setByY(BOB_DISTANCE);
        bobAnimation.setAutoReverse(true);
        bobAnimation.setCycleCount(Animation.INDEFINITE);
        bobAnimation.setInterpolator(Interpolator.EASE_BOTH);

        rotationAnimation = new RotateTransition(Duration.millis(BOB_DURATION_MILLIS), displayPicture);
        rotationAnimation.setByAngle(ROTATION_ANGLE);
        rotationAnimation.setAutoReverse(true);
        rotationAnimation.setCycleCount(Animation.INDEFINITE);
        rotationAnimation.setInterpolator(Interpolator.EASE_BOTH);

        typingAnimation = new Timeline(new KeyFrame(
                Duration.millis(TYPING_INTERVAL_MILLIS),
                event -> {
                    int nextLength = dialog.getText().length() + 1;
                    dialog.setText(text.substring(0, nextLength));
                }));
        typingAnimation.setCycleCount(text.length());
        typingAnimation.setOnFinished(event -> stopAnimations());

        MediaPlayer currentVoicePlayer = loadVoicePlayer();
        voicePlayer = currentVoicePlayer;
        activeVoicePlayer = currentVoicePlayer;
        activeVoiceOwner = this;
        currentVoicePlayer.setCycleCount(MediaPlayer.INDEFINITE);
        currentVoicePlayer.setOnReady(() -> startSynchronizedPlayback(currentVoicePlayer));
        if (currentVoicePlayer.getStatus() == MediaPlayer.Status.READY) {
            startSynchronizedPlayback(currentVoicePlayer);
        }
    }

    /**
     * Starts the media, typing, and avatar animations at the same time.
     *
     * @param player the media player that has finished loading.
     */
    private void startSynchronizedPlayback(MediaPlayer player) {
        if (isPlaybackStarted || activeVoiceOwner != this || activeVoicePlayer != player) {
            return;
        }

        isPlaybackStarted = true;
        player.play();
        bobAnimation.play();
        rotationAnimation.play();
        typingAnimation.play();
    }

    /**
     * Stops all currently playing Turtley voice audio.
     */
    private static void stopActiveVoicePlayer() {
        if (activeVoicePlayer != null) {
            stopAndDispose(activeVoicePlayer);
            activeVoicePlayer = null;
            activeVoiceOwner = null;
        }
    }

    /**
     * Stops the avatar and voice animations and restores the avatar's original transform.
     */
    private void stopAnimations() {
        if (bobAnimation != null) {
            bobAnimation.stop();
            displayPicture.setTranslateY(0.0);
            bobAnimation = null;
        }
        if (rotationAnimation != null) {
            rotationAnimation.stop();
            displayPicture.setRotate(0.0);
            rotationAnimation = null;
        }
        if (voicePlayer != null) {
            MediaPlayer currentVoicePlayer = voicePlayer;
            voicePlayer = null;
            if (activeVoiceOwner == this && currentVoicePlayer == activeVoicePlayer) {
                stopActiveVoicePlayer();
            } else {
                stopAndDispose(currentVoicePlayer);
            }
        }
    }

    /**
     * Stops, rewinds, and releases a voice player.
     *
     * @param player the voice player to release.
     */
    private static void stopAndDispose(MediaPlayer player) {
        if (player.getStatus() == MediaPlayer.Status.DISPOSED) {
            return;
        }
        player.pause();
        player.stop();
        player.seek(Duration.ZERO);
        player.dispose();
    }

    /**
     * Creates a voice player for the cached media.
     *
     * @return a new voice player.
     */
    private static MediaPlayer loadVoicePlayer() {
        return new MediaPlayer(loadVoiceMedia());
    }

    /**
     * Returns the cached voice media, loading the resource only on first use.
     *
     * @return the voice media.
     */
    private static Media loadVoiceMedia() {
        if (voiceMedia != null) {
            return voiceMedia;
        }

        URL voiceUrl = DialogBox.class.getResource(VOICE_RESOURCE);
        if (voiceUrl == null) {
            throw new IllegalStateException("Missing Turtley voice resource: " + VOICE_RESOURCE);
        }
        voiceMedia = new Media(voiceUrl.toExternalForm());
        return voiceMedia;
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

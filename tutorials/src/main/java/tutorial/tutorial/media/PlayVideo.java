package tutorial.tutorial.media;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import tutorial.tutorial.DragUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class PlayVideo {
    ObservableList<Node> nodes;
    Scene scene;

    public void displayScreen(Runnable runnable) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: orange; -fx-background-color: orange;");
        Pane pane = new Pane();

        scrollPane.setContent(pane);

        nodes = pane.getChildren();

//        File videofile = new File(Objects.requireNonNull(PlayVideo.class.getResource("/tutorial/tutorial/video.mp4")).toExternalForm());
        // creating a Media object from the File Object
        Media videomedia = new Media(Objects.requireNonNull(PlayVideo.class.getResource("/tutorial/tutorial/video.mp4")).toExternalForm());
        // creating a MediaPlayer object from the Media Object
        MediaPlayer mdplayer = new MediaPlayer(videomedia);
        // creating a MediaView object from the MediaPlayer Object
        MediaView viewmedia = new MediaView(mdplayer);
        //setting the fit height and width of the media view
        viewmedia.setFitHeight(455);
        viewmedia.setFitWidth(500);
        // creating video controls using the buttons
        Button pause = new Button("Pause");
        Button resume = new Button("Resume");
        // creating an HBox
        HBox box = new HBox(20, pause, resume);
        box.setAlignment(Pos.CENTER);
        // function to handle play and pause buttons
        pause.setOnAction(act -> mdplayer.pause());
        resume.setOnAction(act -> mdplayer.play());
        // creating the root
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(viewmedia, box);
        scene = new Scene(root, 400, 400);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Example of Video in JavaFX");
        stage.show();

        stage.setOnCloseRequest(e -> runnable.run());
    }
}

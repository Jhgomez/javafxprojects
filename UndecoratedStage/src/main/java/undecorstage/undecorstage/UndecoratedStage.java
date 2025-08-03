package undecorstage.undecorstage;

import javafx.application.Application;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import undecorstage.undecorstage.window.NiceWindow;
import undecorstage.undecorstage.window.controls.NiceMenu;
import undecorstage.undecorstage.window.controls.NiceMenuItem;
import undecorstage.undecorstage.window.enu.Theme;

public class UndecoratedStage extends Application {
    Stage stage;

    @Override
    public void start(Stage stage) {
        NiceWindow window = new NiceWindow(Theme.BLUE);
        window.setTitle("NiceWindow 2021");

        NiceMenu fileMenu = new NiceMenu("File", window.getTheme());
        NiceMenu editMenu = new NiceMenu("Edit", window.getTheme());
        NiceMenu helpMenu = window.createNiceMenu("Help");

        NiceMenuItem newMenuItem = window.createNiceMenuItem("New", null, false);
        NiceMenuItem openRecent = window.createNiceMenuItem("Open Recent", null, window.getTheme(), false);
        NiceMenuItem closeMenuItem = window.createNiceMenuItem("New", null, true);

        fileMenu.setMenuItems(newMenuItem, openRecent, closeMenuItem);

        editMenu.setMenuItems(
                window.createNiceMenuItem("Copy", false),
                window.createNiceMenuItem("Paste", false),
                window.createNiceMenuItem("Delete", true)
        );

        helpMenu.setMenuItems(
                window.createNiceMenuItem("Find Actions", false),
                window.createNiceMenuItem("Documentation", false),
                window.createNiceMenuItem("About", true)
        );
        window.setMenus(fileMenu, editMenu, helpMenu);

        ImageView graphic = new ImageView();
        graphic.setImage(Reference.ICON);
        graphic.setFitWidth(16);
        graphic.setFitHeight(16);
        closeMenuItem.setGraphic(graphic);

        AnchorPane p =  new AnchorPane();
        p.setPrefWidth(800);
        p.setPrefHeight(480);
        p.setStyle("-fx-background-color: aqua;");

        window.setContent(p);

        window.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
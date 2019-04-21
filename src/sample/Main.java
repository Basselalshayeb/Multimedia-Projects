package sample;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root= FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Photo Editor");
        primaryStage.setScene(new Scene(root, 700, 700));
        File icon=new File(getClass().getResource("photoeditor.jpg").getPath());
        primaryStage.getIcons().add(SwingFXUtils.toFXImage(ImageIO.read(icon),null));
        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);
    }
}

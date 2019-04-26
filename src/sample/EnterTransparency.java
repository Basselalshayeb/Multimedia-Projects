package sample;

import com.sun.org.apache.xpath.internal.functions.FuncExtFunctionAvailable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;

public class EnterTransparency {
    public static double trans;
    public static String text;
    public static double getAnswer() throws IOException {
        Stage window=new Stage();
        window.setTitle("Enter transparency");
        window.setHeight(160);
        window.setWidth(200);
        Label label=new Label("Transparency");
        label.setAlignment(Pos.CENTER);
        Slider slider=new Slider();
        slider.setMin(0);slider.setMax(1);slider.setValue(1);
        Button savebtn=new Button("Save");
        savebtn.setPrefWidth(100);
        //Create the listener
        savebtn.setOnAction(e->{
            trans=slider.getValue();
            window.close();
        });

        VBox lay=new VBox(10);
        lay.getChildren().add(label);
        lay.getChildren().add(slider);
        lay.getChildren().add(savebtn);
        Scene scene=new Scene(lay);
        window.setScene(scene);
        window.showAndWait();
        return trans;
                /*
        Parent root = FXMLLoader.load(getClass().getResource("insertphotowithtransparency"));
        window.setScene(new Scene(root,200,160));
        window.show();*/
    }
    public static String getText(){
        Stage window=new Stage();
        window.setTitle("Enter Text");
        window.setHeight(140);
        window.setWidth(160);
        Label label=new Label("Enter text");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        label.setPrefHeight(35);
        TextField input=new TextField();
        input.setPrefWidth(200);
        input.setPrefHeight(40);
        Button savebtn=new Button("Done");
        savebtn.setPrefWidth(200);
        savebtn.setPrefHeight(30);
        savebtn.setAlignment(Pos.CENTER);
        savebtn.setStyle("-fx-background-color: #14919B");
        //Create the listener
        savebtn.setOnAction(e->{
            text=input.getText();
            window.close();
        });
        VBox vbox=new VBox();
        vbox.getChildren().addAll(label,input,savebtn);
        vbox.setStyle("-fx-background-color: #486581");
        Scene scene=new Scene(vbox);
        window.setScene(scene);
        window.showAndWait();
        return text;
    }
}

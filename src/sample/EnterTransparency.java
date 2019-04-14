package sample;

import com.sun.org.apache.xpath.internal.functions.FuncExtFunctionAvailable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class EnterTransparency {
    public static double trans;
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
}

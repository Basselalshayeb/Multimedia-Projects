package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class InsertPhotoController {

    public Slider transparency;

    public double onSave(ActionEvent e){
        double ret;
        ret=transparency.getValue();
        return  ret;
    }
}

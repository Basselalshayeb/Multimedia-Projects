package sample;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class Controller implements Initializable
{

   public ColorPicker colorpicker;

   public TextField brushsize;

    public RadioButton pen,eraser;

    public Canvas canvas;

        //not done Load image to canvas
        public  void onLoadImage(ActionEvent e) throws IOException {
            FileChooser filechooser=new FileChooser();
            File infile=filechooser.showOpenDialog(null);
            Image img=SwingFXUtils.toFXImage(ImageIO.read(infile), null);
            //canvas.;
        }
    public void onSaveImageAs(ActionEvent event){
        try{
            //1- get the image
            Image paintedimg=canvas.snapshot(null,null);
            //2- get the save location
            FileChooser fileChooser=new FileChooser();
            fileChooser.setTitle("Save image");
            fileChooser.setInitialFileName("edited_image.png");
            File target=fileChooser.showSaveDialog(null);
                 /*if(!Pattern.matches(".*[.]png",target.getName()))  Warning : no need for user error
                    target=new File(target+".png");*/
            //3- now write the buffered image to file
            ImageIO.write(SwingFXUtils.fromFXImage(paintedimg,null),"png",target);
        }catch (IOException e){
            System.out.println("error in save image \n"+e.getStackTrace());
        }
    }
    public void onSaveImage(ActionEvent event){
        try{
            Image paintedimg=canvas.snapshot(null,null);
            ImageIO.write(SwingFXUtils.fromFXImage(paintedimg,null),"jpg",new File("image.jpg"));
        }catch (IOException e){
            System.out.println("error in save image");
        }
    }
    public void onExit(ActionEvent e){
        Platform.exit();
    }
    //make another window
    public void onAbout(ActionEvent e){

    }

    //switch from pen to eraser only one is clicked
        public void onPenClick(ActionEvent e){
            if(pen.isSelected()){
                eraser.setSelected(false);
            }
        }
        public void onEraserClick(ActionEvent e){
            if(eraser.isSelected()){
                pen.setSelected(false);
            }
        }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //the paint happen
        GraphicsContext g=canvas.getGraphicsContext2D();

        //always this is working
        canvas.setOnMouseDragged(e -> {
        double size;
        try{
        size=Double.parseDouble(brushsize.getText());
        }catch (NumberFormatException ex){
            size=0;
        }
        //-size/2 to make the mouse on the corner of the rect
        double x=e.getX()-size/2,y=e.getY()-size/2;
        //now paint mouser pos + size to make rectangle
        if(eraser.isSelected()){
            g.clearRect(x,y,size,size);

        }else if(pen.isSelected()){
            g.setFill(colorpicker.getValue());
           //rounded rect become full round in the end
            // g.fillRoundRect(x, y, size, size, 100, 100);
            g.fillRect(x,y,size,size);
        }else{
        //nothing happen
        }

        });
    }
}

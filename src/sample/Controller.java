package sample;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.regex.Pattern;

public class Controller implements Initializable
{

   public ColorPicker colorpicker;

   public TextField brushsize;

   //tools
    public RadioButton pen,eraser;

    //max width and height
    Double maxwidth,maxheight;

    private Vector<Canvas> canvss=new Vector<Canvas>();

    //more than canvas
    public ScrollPane scrollpane;

        //not done Load image to canvas
        public  void onLoadImage(ActionEvent event) throws IOException {
            //clear
            canvss.clear();
            FileChooser filechooser=new FileChooser();
            filechooser.setTitle("Load image");
            filechooser.setInitialDirectory(new File("C:\\Users\\Bassel\\Desktop"));
            //get the file
            File infile=filechooser.showOpenDialog(null);
            Image img=SwingFXUtils.toFXImage(ImageIO.read(infile), null);
            //just initialize the first canvas
            canvss.add(new Canvas());
            canvss.elementAt(0).setHeight(img.getHeight());
            canvss.elementAt(0).setWidth(img.getWidth());
            canvss.elementAt(0).getGraphicsContext2D().drawImage(img,0,0,img.getWidth(),img.getHeight());
            maxwidth=img.getWidth();
            maxheight=img.getHeight();
            //here add another layer so i can write on
            canvss.add(new Canvas());
            canvss.elementAt(1).setWidth(canvss.elementAt(0).getWidth());
            canvss.elementAt(1).setHeight(canvss.elementAt(0).getHeight());

            Pane p =new Pane();
            p.getChildren().addAll(canvss.elementAt(0),canvss.elementAt(1));
            scrollpane.setContent(p);
            canvss.elementAt(1).toFront();

            //add the listener
            canvss.elementAt(1).setOnMouseDragged(this::mouseListinerForPaint);
        }
        public void onAddAnotherLayer(ActionEvent e) throws IOException {
            FileChooser filechooser=new FileChooser();
            filechooser.setTitle("Load the new layer image");
            //get the new layer file
            File infile=filechooser.showOpenDialog(null);
            Image img=SwingFXUtils.toFXImage(ImageIO.read(infile), null);
            //here should define the transparency for the new layer
            //open new window using fxml and get the transparency
            //append the layer
            maxheight=Math.max(maxheight,img.getHeight());
            maxwidth=Math.max(maxwidth,img.getWidth());
            Canvas layer=new Canvas();
            layer.setWidth(maxwidth);
            layer.setHeight(maxheight);
            layer.getGraphicsContext2D().drawImage(img,0,0,img.getWidth(),img.getHeight());
            canvss.add(layer);
            //add the new layer to the layers
            Pane p=(Pane)scrollpane.getContent();
            p.getChildren().add(layer);
            //add the new front layer (Painting layer)
            Canvas paintinglayer=new Canvas();
            paintinglayer.setHeight(maxheight);
            paintinglayer.setWidth(maxwidth);
            p.getChildren().add(paintinglayer);
            paintinglayer.toFront();
            paintinglayer.setOnMouseDragged(this::mouseListinerForPaint);
            //replace the scrollpane
            scrollpane.setContent(p);
        }
        public void mouseListinerForPaint(MouseEvent e){
            Canvas test=(Canvas)e.getSource();
            GraphicsContext g=test.getGraphicsContext2D();
            // GraphicsContext g=canvss.elementAt(1).getGraphicsContext2D();
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
                g.fillOval(x,y,size,size);
            }else{
                //nothing happen
            }
        }

    public void onSaveImageAs(ActionEvent event){
        try{
            //1- get the image from all the canvases
           Canvas res=new Canvas();
           res.setWidth(maxwidth);
           res.setHeight(maxheight);
           for(int i=0;i<canvss.size();i++) {
               //get the image
               SnapshotParameters params = new SnapshotParameters();
               params.setFill(Color.TRANSPARENT);
               Image img=canvss.elementAt(i).snapshot(params,null);
               res.getGraphicsContext2D().drawImage(img,0,0 );
           }
            Image paintedimg=res.snapshot(null,null);
            //2- get the save location
            FileChooser fileChooser=new FileChooser();
            fileChooser.setTitle("Save image");
            fileChooser.setInitialFileName("edited_image.png");
            File target=fileChooser.showSaveDialog(null);
            //3- now write the buffered image to file
            ImageIO.write(SwingFXUtils.fromFXImage(paintedimg,null),"png",target);
        }catch (IOException e){
            System.out.println("error in save image \n"+e.getStackTrace());
        }
    }
    public void onSaveImage(ActionEvent event){
        try{
            //1- merge all the canvas
            Canvas res=new Canvas();
            res.setWidth(maxwidth);
            res.setHeight(maxheight);
            for(int i=0;i<canvss.size();i++) {
                //get the image
                SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.TRANSPARENT);
                Image img=canvss.elementAt(i).snapshot(params,null);
                res.getGraphicsContext2D().drawImage(img,0,0 );
            }
            //2- put in paint
            Image paintedimg=res.snapshot(null,null);
            //3- write it
            ImageIO.write(SwingFXUtils.fromFXImage(paintedimg,null),"png",new File("edited_image.png"));
        }catch (IOException e){
            System.out.println("error in save image");
        }
    }
    public void onExit(ActionEvent e){
        Platform.exit();
    }
    //make another window
    public void onAbout(ActionEvent e){}

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
            //initialize the first values
            pen.setSelected(true);
            colorpicker.setValue(Color.BLACK);
            brushsize.setText("22");
        //old code
            //the paint happen
        /*GraphicsContext g=canvas.getGraphicsContext2D();

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

        });*/
    }
}

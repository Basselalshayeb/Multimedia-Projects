package sample;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class Controller implements Initializable
{

   public ColorPicker colorpicker;

   public TextField brushsize;

   //brush shape combo box
    public ComboBox brushshape;
   //tools
    public CheckBox pen,eraser,fill;


    //max width and height
   private  Double maxwidth=0.0,maxheight=0.0;

    //combo box for color system
    public ComboBox colorsystem;
    //current color system
    private String currentcolorsystem="RGB";
    //more than canvas
    private Vector<Canvas> canvss=new Vector<Canvas>();

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
            //define the transparency
            double trans=EnterTransparency.getAnswer();
            //append the layer
            maxheight=Math.max(maxheight,img.getHeight());
            maxwidth=Math.max(maxwidth,img.getWidth());
            Canvas layer=new Canvas();
            layer.setWidth(maxwidth);
            layer.setHeight(maxheight);
            layer.getGraphicsContext2D().setGlobalAlpha(trans);
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
        //collor system controller
        public void onConvertColorSystem(ActionEvent e){
            switch ((String)colorsystem.getValue()){
                //RGB section
                case "RGB": if (currentcolorsystem.equals("CMY")){
                    onConvertToCMYClick(new ActionEvent());
                    currentcolorsystem="RGB";
                }else if(currentcolorsystem.equals("CMYK")){
                    onConvertToCMYKClick(new ActionEvent());
                    currentcolorsystem="RGB";
                }break;
                //CMY section
                case "CMY": if (currentcolorsystem.equals("RGB")){

                    onConvertToCMYClick(new ActionEvent());
                    currentcolorsystem="CMY";

                }else if(currentcolorsystem.equals("CMYK")){
                    //1-convert it to RGB
                    onConvertToCMYKClick(new ActionEvent());
                    onConvertToCMYClick(new ActionEvent());
                    //2- convert it to CMY
                    currentcolorsystem="CMY";
                }  break;
                //CMYK section
                case "CMYK":if (currentcolorsystem.equals("RGB")){

                    onConvertToCMYKClick(new ActionEvent());
                    currentcolorsystem="CMYK";

                }else if(currentcolorsystem.equals("CMY")){
                    //1-convert it to RGB
                    onConvertToCMYClick(new ActionEvent());
                    //2- convert it to CMYK
                    onConvertToCMYKClick(new ActionEvent());
                    currentcolorsystem="CMYK";
                }  break;
                //zaid
                case "Black and white" : break;
                //zaid
                case "Gray scale": break;
            }
        }
        public void onConvertToCMYClick(ActionEvent e){
            //1- make the image from all the canvass
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
            //the merged image
            Image paintedimg=res.snapshot(null,null);
            //2- iterate over pixels and convert
            PixelReader preader =paintedimg.getPixelReader();
            PixelWriter pwriter=((WritableImage) paintedimg).getPixelWriter();
            for(int i = 0; i<paintedimg.getHeight();i++){
                for(int j=0;j<paintedimg.getWidth();j++){
                    Color color=preader.getColor(j,i);
                    double c=1-color.getRed();
                    double m=1-color.getGreen();
                    double y=1-color.getBlue();
                     Color t=new Color(c,m,y,1.0);
                    pwriter.setColor(j,i,t);
                }
            }
            //3- write it to the last canvas layer canvass
            canvss.elementAt(canvss.size()-1-1).getGraphicsContext2D().drawImage(paintedimg,0,0);
        }
        public void onConvertToCMYKClick(ActionEvent e){
            //1- make the image from all the canvass
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
            //2- iterate over pixels and convert
            PixelReader preader =paintedimg.getPixelReader();
            PixelWriter pwriter=((WritableImage) paintedimg).getPixelWriter();
            for(int i = 0; i<paintedimg.getHeight();i++){
                for(int j=0;j<paintedimg.getWidth();j++){
                    Color color=preader.getColor(j,i);
                    double c=1-color.getRed();
                    double m=1-color.getGreen();
                    double y=1-color.getBlue();
                    double k=Math.min(Math.min(c,m),y);
                    double cc=c-k,mm=m-k,yy=y-k;
                    Color t=new Color(cc,mm,yy,1.0);
                    pwriter.setColor(j,i,t);
                }
            }
            //3- write it to the last canvas layer canvass
            canvss.elementAt(canvss.size()-1-1).getGraphicsContext2D().drawImage(paintedimg,0,0);
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
            if(brushshape.getValue().equals("Pen"))
                size=1;
            if(fill.isSelected())
                size=0;
            //-size/2 to make the mouse on the corner of the rect
            double x=e.getX()-size/2,y=e.getY()-size/2;
            //now paint mouser pos + size to make rectangle
            if(eraser.isSelected()){
                g.clearRect(x,y,size,size);
            }else if(pen.isSelected()) {
                g.setFill(colorpicker.getValue());
                if (brushshape.getValue().equals("Pen") || brushshape.getValue().equals("Oval"))
                    g.fillOval(x, y, size, size);
                else if (brushshape.getValue().equals("Rect"))
                    g.fillRect(x, y, size, size);
            }
            else if (fill.isSelected()){
                //1- adjust the x and y
                x=Math.max(0,x);
                x=Math.min(x,canvss.elementAt(canvss.size()-1).getHeight());
                y=Math.max(0,y);
                y=Math.min(y,canvss.elementAt(canvss.size()-1).getWidth());
                //2- get working image
                Canvas finallbutlastone=mergeAllLayersButLast();
                Image finallbutlastoneimg=finallbutlastone.snapshot(null,null);
                //4- get result area
                Canvas temp=new Canvas(finallbutlastone.getWidth(),finallbutlastone.getWidth());
                temp.getGraphicsContext2D().drawImage(finallbutlastoneimg,0,0);
                Image resultedimgfromfill=temp.snapshot(null,null);

                //call the recursive function
                PixelReader preader=finallbutlastoneimg.getPixelReader();
                Color clickedcolor=preader.getColor((int)x,(int)y);
                Color choosedcolor=colorpicker.getValue();
                System.out.println(finallbutlastoneimg.getWidth()+" "+finallbutlastoneimg.getHeight());
                fillTheResultedImage((int)x,(int)y,(int)finallbutlastoneimg.getWidth(),(int)finallbutlastoneimg.getHeight(),preader,((WritableImage) resultedimgfromfill).getPixelWriter(),clickedcolor,choosedcolor);
                PixelWriter pwriter=((WritableImage) finallbutlastoneimg).getPixelWriter();
                canvss.elementAt(canvss.size()-1-1).getGraphicsContext2D().drawImage(resultedimgfromfill,0,0);
              /*  //bottom right
                for(int i=(int)x;i<finallbutlastoneimg.getWidth();i++){
                for(int j=(int)y;j<finallbutlastoneimg.getHeight();j++)
                    {
                        if(preader.getColor(i,j).equals(clickedcolor))
                            pwriter.setColor(i,j,choosedcolor);
                        else break;
                    }
                    if(!preader.getColor(i,0).equals(clickedcolor))
                        break;
                }
                //top right
                System.out.println(clickedcolor);
                for(int i=(int)x;i<finallbutlastoneimg.getWidth();i++){
                    for(int j=(int)y;j>=0;j--)
                    {
                        System.out.println("first"+preader.getColor(i,j));
                        if(preader.getColor(i,j).equals(clickedcolor))
                            pwriter.setColor(i,j,choosedcolor);
                        else break;
                    }
                    if(!preader.getColor(i,0).equals(clickedcolor))
                    break;
                }*/
                //top left
                //canvss.elementAt(canvss.size()-1-1).getGraphicsContext2D().drawImage(finallbutlastoneimg,0,0);

            }
        }
        HashMap<Pair<Integer,Integer>,Boolean> vis=new HashMap<>();
        //function to fill the resulted image after fill
    public void fillTheResultedImage(int x,int y,int maxx,int maxy,PixelReader preader,PixelWriter pwriter,Color clickedcolor,Color choosedcolor){
           System.out.println(clickedcolor+" "+preader.getColor(x,y));
           Stack<Pair<Integer,Integer>> st=new Stack<>();
           st.push(new Pair<>(x,y));
           while(!st.empty()){
               Pair temp=st.pop();
               x=(int)temp.getKey();
               y=(int)temp.getValue();
                System.out.println("poped "+temp);
               if(preader.getColor((int)temp.getKey(),(int)temp.getValue()).equals(clickedcolor)){
               System.out.println("fuck");
                   pwriter.setColor(x,y,choosedcolor);
               }
               //x+1<=maxx &&
               if(x+1<maxx-10 && !vis.containsKey(new Pair(x+1,y))){
                   System.out.println(x+1+" "+y);
                   vis.put(new Pair(x+1,y),true);
                   st.push(new Pair(x+1,y));
               }
               //x-1<=maxx &&
               if(x-1>10 && !vis.containsKey(new Pair(x-1,y))){
                   vis.put(new Pair(x-1,y),true);
                   st.push(new Pair(x-1,y));
               }
               //y+1<=maxx &&
               if(y+1<maxy-10 && !vis.containsKey(new Pair(x,y+1))){
                   vis.put(new Pair(x,y+1),true);
                   st.push(new Pair(x,y+1));
               }
               //y-1<=maxx &&
               if(y-1>10 && !vis.containsKey(new Pair(x,y-1))){
                   vis.put(new Pair(x,y-1),true);
                   st.push(new Pair(x,y-1));
               }
           }

    }
public Canvas mergeAllLayersButLast(){
    Canvas res=new Canvas();
    res.setWidth(maxwidth);
    res.setHeight(maxheight);
    for(int i=0;i<canvss.size()-1;i++) {
        //get the image
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image img=canvss.elementAt(i).snapshot(params,null);
        res.getGraphicsContext2D().drawImage(img,0,0 );
    }
    return res;
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
                fill.setSelected(false);
            }
        }
        public void onEraserClick(ActionEvent e){
            if(eraser.isSelected()){
                pen.setSelected(false);
                fill.setSelected(false);
            }
        }
        public void onFillClick(){
            if(fill.isSelected()){
                pen.setSelected(false);
                eraser.setSelected(false);
            }
        }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            //initialize the first values
            pen.setSelected(true);

            colorpicker.setValue(Color.BLACK);
            brushsize.setText("22");

            // initialize the bursh shapes
        brushshape.getItems().addAll("Pen","Oval","Rect");
        brushshape.setValue("Pen");
            //initialize the color system  combo box
        colorsystem.getItems().addAll("RGB","CMY","CMYK","Black and white","Gray scale");
        colorsystem.setValue("RGB");
    }
}

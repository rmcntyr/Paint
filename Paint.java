package paint;

import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Paint extends Application {

    static File file;       //allows the user to upload their own files to the program
    static boolean saved;  //allows the user to close the program without a pop-up warning message if no edits have been made to the image
    static int x = 1280, y = 720;   //arbitrary dimensions for the window
    static Canvas layer1 = new Canvas(x, y);  //Creates an object to allow the user to draw on
    static Canvas layer2 = new Canvas(x, y);   //Second canvas to allow for the user to see what their line/shape will look like before it is drawn
    static Pane root = new Pane(layer1, layer2);   //Both canvases must be used together
    static GraphicsContext gc1 = layer1.getGraphicsContext2D();
    GraphicsContext gc2 = layer2.getGraphicsContext2D();
    static Stage primaryStage;
    Rectangle select = new Rectangle();
    static Image croppedImage;
    Image testImage;
    boolean cropped = true;

    @Override
    public void start(Stage primaryStage) {

        //Drawing tool buttons
        ToggleButton linebtn = new ToggleButton("Line");
        ToggleButton drawbtn = new ToggleButton("Draw");
        ToggleButton rectbtn = new ToggleButton("Rectangle");
        ToggleButton circbtn = new ToggleButton("Circle");
        ToggleButton selectbtn = new ToggleButton("Select");
        ToggleButton cropbtn = new ToggleButton("Crop");
        ToggleButton ellibtn = new ToggleButton("Ellipse");
        ToggleButton textbtn = new ToggleButton("Text");
        ToggleButton eraserbtn = new ToggleButton("Eraser");
        ToggleButton dropbtn = new ToggleButton("Dropper");

        ToggleButton[] toolsArr = {linebtn, drawbtn, rectbtn, circbtn, eraserbtn, selectbtn, cropbtn, dropbtn, ellibtn, textbtn };    //Creates drawing tools as toggles 
        ToggleGroup tools = new ToggleGroup();

        for (ToggleButton tool : toolsArr) {
            tool.setMinWidth(90);
            tool.setToggleGroup(tools);
            tool.setCursor(Cursor.HAND);        //changes cursor when hovering over buttons
        }

        ColorPicker cpFill = new ColorPicker(Color.TRANSPARENT);        //default fill is transparent
        ColorPicker cpLine = new ColorPicker(Color.BLACK);  //Default color of the line is black

        Button open = new Button("Open Image");     //allows user to choose an image from ones they have downloaded already
        Button undo = new Button("Undo");

        Label line_color = new Label("Line Color");
        Label fill_color = new Label("Fill Color");
        Label line_width = new Label("Line Width");

        Slider slider = new Slider(1, 50, 3);   //minimum width is 1, max is 50, starts at 3
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);


        TextArea text = new TextArea();
        text.setPrefRowCount(1);
        
        VBox btns = new VBox(10);
        btns.setStyle("-fx-background-color: darkgrey");
        btns.getChildren().addAll(linebtn, drawbtn, line_color, cpLine, fill_color, cpFill, open, undo, line_width, slider, circbtn, rectbtn, ellibtn, selectbtn,
                cropbtn, textbtn, text, eraserbtn, dropbtn);
        btns.setPadding(new Insets(5));
        btns.setPrefWidth(100);

        gc1.setLineWidth(1);
        gc2.setLineWidth(1);

        Line line = new Line();
        Rectangle rect = new Rectangle();
        Circle circ = new Circle();
        Ellipse elps = new Ellipse();       
        
        //Sets the menu
        MenuContainer.createMenuOptions(btns, root, layer1, gc1, primaryStage);


        //Allows the different shapes and drawing tools to be used when a mouse click happens on the canvas
        root.setOnMousePressed(e -> {
            //Line
            if (linebtn.isSelected()) {
                gc1.setStroke(cpLine.getValue());   //takes the color value and applies it to the line being created
                gc2.setStroke(cpLine.getValue());
                line.setStartX(e.getX());       //gets the x and y starting position of the line
                line.setStartY(e.getY());
                Undo.push(layer1);      //shape is pushed to the top of the stack for undo to work
            //Pencil
            } else if (drawbtn.isSelected()) {     
                gc1.setStroke(cpLine.getValue());
                gc1.beginPath();
                gc1.lineTo(e.getX(), e.getY());
                Undo.push(layer1);
            //Rectangle
            } else if (rectbtn.isSelected()) {  
                gc1.setStroke(cpLine.getValue());
                gc1.setFill(cpFill.getValue());
                gc2.setStroke(cpLine.getValue());
                gc2.setFill(cpFill.getValue());
                rect.setX(e.getX());
                rect.setY(e.getY());
                Undo.push(layer1);
            //Circle
            } else if (circbtn.isSelected()) {    
                gc1.setStroke(cpLine.getValue());
                gc1.setFill(cpFill.getValue());
                gc2.setStroke(cpLine.getValue());
                gc2.setFill(cpFill.getValue());
                circ.setCenterX(e.getX());
                circ.setCenterY(e.getY());
                Undo.push(layer1);
            //Select area for crop
            } else if (selectbtn.isSelected()) {
                gc1.setStroke(cpLine.getValue());     
                gc1.setFill(cpFill.getValue());
                gc2.setStroke(cpLine.getValue());
                gc2.setFill(cpFill.getValue());
                select.setX(e.getX());
                select.setY(e.getY());
                Undo.push(layer1);
            //Place cropped area
            } else if (cropbtn.isSelected()) {
                Bounds selectionBounds = select.getBoundsInParent();
                testImage = crop(selectionBounds);
                gc1.drawImage(testImage, e.getX(), e.getY());
                Undo.push(layer1);
            //Ellipse
            } else if(ellibtn.isSelected()) {
                gc1.setStroke(cpLine.getValue());
                gc1.setFill(cpFill.getValue());
                elps.setCenterX(e.getX());
                elps.setCenterY(e.getY());
                Undo.push(layer1);
            }
            //Text
            else if(textbtn.isSelected()) {
                gc1.setLineWidth(1);
                gc1.setFont(Font.font(slider.getValue()));
                gc1.setStroke(cpLine.getValue());
                gc1.setFill(cpFill.getValue());
                gc1.fillText(text.getText(), e.getX(), e.getY());
                gc1.strokeText(text.getText(), e.getX(), e.getY());
                Undo.push(layer1);
            //Eraser
            } else if(eraserbtn.isSelected()) {
                double lineWidth = gc1.getLineWidth();
                gc1.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
                Undo.push(layer1);
            //Dropper
            } else if (dropbtn.isSelected()) {
                WritableImage snap = gc1.getCanvas().snapshot(null, null);
                cpFill.setValue(snap.getPixelReader().getColor((int)e.getX(), (int) e.getY()));
                cpLine.setValue(snap.getPixelReader().getColor((int)e.getX(), (int) e.getY()));
            }
        });

        //when the user releases the mouse after drawing
        root.setOnMouseReleased(e -> {
            saved = true;   //prevents the user from exiting the program without saving only if they have edited the image
            //Line
            if (linebtn.isSelected()) {
                gc1.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                gc2.clearRect(0, 0, x, y);  //removes the second layer of the canvas, which allows the user to see a preview of what they are drawing
            //Pencil
            } else if (drawbtn.isSelected()) {
                gc1.lineTo(e.getX(), e.getY());
                gc1.stroke();
                gc1.closePath();
            //Rectangle
            } else if (rectbtn.isSelected()) {
                gc1.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc1.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc2.clearRect(0, 0, x, y);
            //Circle
            } else if (circbtn.isSelected()) {
                gc1.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc1.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc2.clearRect(0, 0, x, y);
            //Select
            } else if (selectbtn.isSelected()) {
                gc2.fillRect(select.getX(), select.getY(), select.getWidth(), select.getHeight());
                gc2.strokeRect(select.getX(), select.getY(), select.getWidth(), select.getHeight());
                gc2.clearRect(0, 0, x, y);
                cropped = true;
            //Ellipse
            } else if(ellibtn.isSelected()) {
                elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
                elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));
                if(elps.getCenterX() > e.getX()) {
                    elps.setCenterX(e.getX());
                }
                if(elps.getCenterY() > e.getY()) {
                    elps.setCenterY(e.getY());
                }
                gc1.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                gc1.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                
            //Eraser
            } else if(eraserbtn.isSelected()) {
                double lineWidth = gc1.getLineWidth();
                gc1.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            } 
        });
        //the drawing tools as they are used while dragging
        root.setOnMouseDragged(e -> {
            if (drawbtn.isSelected()) {
                gc1.lineTo(e.getX(), e.getY());
                gc1.stroke();
                //Line
            } else if (linebtn.isSelected()) { 
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc2.clearRect(0, 0, x, y);      //clears the entire canvas of the excessive lines
                gc2.lineTo(e.getX(), e.getY());
                gc2.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                //Rectangle
            } else if (rectbtn.isSelected()) {              
                rect.setWidth(((e.getX() - rect.getX())));
                rect.setHeight(((e.getY() - rect.getY())));
                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());
                }
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }
                gc2.clearRect(rect.getX(), rect.getY(), layer2.getWidth(), layer2.getHeight());  //clears out rectangle so it doesnt draw over itself
                gc2.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());   //draws rectangle on other canvas so it doesn't fill in on the real one
                gc2.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                //Circle
            } else if (circbtn.isSelected()) {          
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);
                if (circ.getCenterX() > e.getX()) {
                    circ.setCenterX(e.getX());
                }
                if (circ.getCenterY() > e.getY()) {
                    circ.setCenterY(e.getY());
                }
                gc2.clearRect(0, 0, layer2.getWidth(), layer2.getHeight());
                gc2.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                
                gc2.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                
                //Ellipse
            }   else if (ellibtn.isSelected()){
                elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
                elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));
                if(elps.getCenterX() > e.getX()) {
                    elps.setCenterX(e.getX());
                }
                if(elps.getCenterY() > e.getY()) {
                    elps.setCenterY(e.getY());
                }
                gc2.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());  
                gc2.clearRect(0, 0, x, y);
                gc2.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                //Crop
            }   else if (selectbtn.isSelected()) {
                select.setWidth(((e.getX() - select.getX())));
                select.setHeight(((e.getY() - select.getY())));
                if (select.getX() > e.getX()) {
                    select.setX(e.getX());
                }
                if (select.getY() > e.getY()) {
                    select.setY(e.getY());
                }
                gc2.fillRect(select.getX(), select.getY(), select.getWidth(), select.getHeight());   //draws rectangle on other canvas so it doesn't fill in on the real one
                gc2.clearRect(select.getX(), select.getY(), layer2.getWidth(), layer2.getHeight());  //clears out rectangle so it doesnt draw over itself
                gc2.strokeRect(select.getX(), select.getY(), select.getWidth(), select.getHeight());
                //Eraser
            } else if(eraserbtn.isSelected()){
                double lineWidth = gc1.getLineWidth();
                gc1.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            }
        });

        // Undo - Pops the shapes saved in the stack
        undo.setOnAction(e -> {
            Undo.pop(layer2, gc1);
        });

        //Width slider
        slider.valueProperty().addListener(e -> {
            double width = slider.getValue();
            if(textbtn.isSelected()){
                gc1.setLineWidth(1);
                gc1.setFont(Font.font(slider.getValue()));
                line_width.setText(String.format("%.1f", width));
                return;
            }
            gc1.setLineWidth(width);
            gc2.setLineWidth(width);
        });
   
        // Open
        open.setOnAction((e) -> {
            Open.open();
        });
    }
    //New function
    static void newCanvas() {
        if (saved == true) {
            Boolean answer = ConfirmBox.display("Exit", "There are unsaved changes to the image, are you sure you want to create a new canvas?");
            if (answer) {
                gc1.clearRect(0, 0, x, y);
                saved = false;
            }
        }
    }
    //Close function
    static void closeProgram() {    //Does not instantly close the program when exiting
        if (saved == true) {
            Boolean answer = ConfirmBox.display("Exit", "There are unsaved changes to the image, are you sure you want to exit without saving?");
            if (answer) {
                Platform.exit();
            }
        } else {
            Platform.exit();
        }
    }
    //Crop function
    private Image crop(Bounds bounds) {
        if(cropped == true) {
        int width = (int) bounds.getWidth();
        int height = (int) bounds.getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        root.snapshot(null, writableImage);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        parameters.setViewport(new Rectangle2D(bounds.getMinX() + 100, bounds.getMinY() + 30, width, height));
        croppedImage = root.snapshot(parameters, writableImage);
        gc1.clearRect(bounds.getMinX(), bounds.getMinY(), width, height);
        }
        cropped = false;
        return croppedImage;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

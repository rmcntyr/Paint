/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.awt.image.RenderedImage;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import static paint.Paint.file;

/**
 *
 * @author rob
 */

//Contains Menu functions
public class MenuContainer extends Paint {
    
    public static void createMenuOptions(VBox btns, Pane root, Canvas layer1, GraphicsContext gc1, Stage primaryStage) {

        
    // File drop down menu
    BorderPane pane = new BorderPane();
    pane.setLeft(btns);
    pane.setCenter(root);       //both layered canvases are set centered    
    
    //Scene and stage setup
    Scene scene = new Scene(pane, x, y);
    primaryStage.setTitle("Pain(t)"); //Gives the title to the program
    primaryStage.setScene(scene);
    primaryStage.show();
    
        
    // Lay out contents
    VBox rootBox = new VBox(); //Lays out components included in it vertically
    
        MenuBar menuBar = new MenuBar();    //Creates the menu bar

        javafx.scene.control.Menu fileMenu = new javafx.scene.control.Menu("File");
        MenuItem newPaint = new MenuItem("New...");
        newPaint.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));   //new canvas shortcut
        MenuItem openPaint = new MenuItem("Open");
        openPaint.setMnemonicParsing(true);
        openPaint.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));  //open shortcut
        MenuItem save = new MenuItem("Save...");
        MenuItem saveAs = new MenuItem("Save As...");
        saveAs.setAccelerator(new KeyCodeCombination(KeyCode.F12)); //save as shortcut
        save.setMnemonicParsing(false);
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN)); //Keyboard shortcut of Ctrl + S to save an image
        MenuItem exit = new MenuItem("Exit...");
        fileMenu.getItems().add(newPaint);
        fileMenu.getItems().add(openPaint);
        fileMenu.getItems().add(save);
        fileMenu.getItems().add(saveAs);
        fileMenu.getItems().add(exit);
        menuBar.getMenus().addAll(fileMenu);
        rootBox.getChildren().add(menuBar);     //Adds the menu bar containing New, Exit, Save, and Save As
        pane.setTop(rootBox);

        //New
        newPaint.setOnAction((e) -> {
            Paint.newCanvas();
        });
        
        //Open menu option
        openPaint.setOnAction((e) -> {
            Open.open();
        });
        //Save - Goes through normal save if the image has a save destination already, if not then goes through saveAs
        save.setOnAction((ActionEvent e) -> {
            
            FileChooser savefile = new FileChooser();
            //Filters that only allowing the saving of images as certain extensions
            FileChooser.ExtensionFilter extensionFilter
                    = new FileChooser.ExtensionFilter(".png", "*.png");
            savefile.getExtensionFilters().add(extensionFilter);
            FileChooser.ExtensionFilter extensionFilter1
                    = new FileChooser.ExtensionFilter(".jpg", "*.jpg");
            savefile.getExtensionFilters().add(extensionFilter1);
            FileChooser.ExtensionFilter extensionFilter2
                    = new FileChooser.ExtensionFilter(".bmp", "*.bmp");
            savefile.getExtensionFilters().add(extensionFilter2);
            if (file == null) {         //If there is no file currently, refers user to Save As
                SaveContainer.saveAs();
            } else {           //Saves over the original file if there is a filename in place
                try {
                    WritableImage writableImage = new WritableImage(x, y);
                    root.snapshot(null, writableImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }
            saved = false;  //makes it so the user does not have to go through the confirm box if they save and exit
        });

        // SaveAs
        saveAs.setOnAction((e) -> {
            SaveContainer.saveAs();
        });

        // Exit
        exit.setOnAction((e) -> {
            Paint.closeProgram();
        });

        Button closeButton = new Button("Close Program");
        closeButton.setOnAction(e -> closeProgram());
        primaryStage.setOnCloseRequest(e -> {    //Gives the user a window to close the program
            e.consume(); //Does not close the window if the user chooses no
            Paint.closeProgram();
        });    
    }
}


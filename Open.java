/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

/**
 *
 * @author rob
 */

//Contains Open Image functions
public class Open extends Paint {
    
    static Image img;
    
    public static void open(){
        Undo.push(layer1);
        Image img;    
        FileChooser openFile = new FileChooser();
           openFile.getExtensionFilters().addAll(
                //Allows the user to open only image extensions
               new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp", "*.jpeg", "*.gif"));
            openFile.setTitle("Open File");
            file = openFile.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    InputStream io = new FileInputStream(file);
                    img = new Image(io, x, y, true, false); //maintains aspect ratio of images opened
                    gc1.drawImage(img, 0, 0);
                        } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }

        };
    
        static Image getImage() {return img;}

    } 


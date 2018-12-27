/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.awt.image.RenderedImage;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import static paint.Paint.file;

/**
 *
 * @author rob
 */

//SaveAs class function
public class SaveContainer extends Paint {
    
    public static void saveAs(){
    FileChooser savefile = new FileChooser();
    
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(".png", "*.png");
            savefile.getExtensionFilters().add(extensionFilter);
            FileChooser.ExtensionFilter extensionFilter1
                    = new FileChooser.ExtensionFilter(".jpg", "*.jpg");
            savefile.getExtensionFilters().add(extensionFilter1);
            FileChooser.ExtensionFilter extensionFilter2
                    = new FileChooser.ExtensionFilter(".bmp", "*.bmp");
            savefile.getExtensionFilters().add(extensionFilter2);
            savefile.setTitle("Save As");
            file = savefile.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                    WritableImage writableImage = new WritableImage(x, y);
                    root.snapshot(null, writableImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }
        saved = false;
            }
}

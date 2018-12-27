/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.util.Stack;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

/**
 *
 * @author rob
 */

//Contains Undo Functions
public class Undo extends Paint {
    static Stack<WritableImage> undoStack = new Stack();
    static Stack<WritableImage> redoStack = new Stack();
    
    public static void push(Canvas layer1) {        
        WritableImage createImg = layer1.snapshot(null, null);
        getStack().push(createImg);
    }
    
    public static void pushredo(Canvas layer1) {        
        WritableImage createImg = layer1.snapshot(null, null);
        getRedoStack().push(createImg);
    }
    
    public static void pop(Canvas layer1, GraphicsContext gc1) {

        if (!undoStack.isEmpty()) {
            gc1.drawImage(undoStack.pop(), 0, 0);
            pushredo(layer1);
        }
    }
    
    public static void redopop(Canvas layer1, GraphicsContext gc1) {        
        gc1.drawImage(redoStack.pop(), 0, 0);
    }
 
    public static Stack<WritableImage> getStack() {
        return undoStack;
    }

    public static Stack<WritableImage> getRedoStack() {
        return redoStack;
    }
    
}


package gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * PixelatedImageView wrapper around standard ImageView.
 * Note: Modern JavaFX (post-11) deprecates access to internal com.sun.* APIs.
 * This class provides a standard ImageView with pixel-perfect rendering via 
 * fitWidth/fitHeight management and disabling smooth interpolation.
 * 
 * @author yaw (updated for JavaFX 11+)
 */
public class PixelatedImageView extends ImageView {
    
    public PixelatedImageView() {
        super();
        // Disable smooth interpolation for pixelated look
        this.setSmooth(false);
        this.setPreserveRatio(true);
    }
    
    public PixelatedImageView(Image image) {
        super(image);
        this.setSmooth(false);
        this.setPreserveRatio(true);
    }
}
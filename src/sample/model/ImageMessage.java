package sample.model;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageMessage extends Message {

    private File image;

    public ImageMessage(File image, String timestamp, boolean sent) {
        super(timestamp, sent);
        this.image = image;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }
}

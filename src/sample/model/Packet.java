package sample.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Packet {

    private byte[] bytes;
    public enum Type{
        STRING,
        IMAGE
    }
    private Type type;

    public Packet(String text){

        byte[] arr2 = text.getBytes();
        bytes = new byte[arr2.length+1];
        bytes[0] = 0;
        for (int i=1; i<arr2.length+1;i++){
            bytes[i] = arr2[i-1];
        }

        type = Type.STRING;
    }

    public Packet(File image) throws IOException {
        BufferedImage img = ImageIO.read(image);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        baos.flush();
        byte[] arr2 = baos.toByteArray();

        bytes = new byte[arr2.length+1];
        bytes[0] = 1;
        for (int i=1; i<arr2.length+1;i++){
            bytes[i] = arr2[i-1];
        }

        type = Type.IMAGE;
    }

    public Packet(byte[] bytes){
        this.bytes = bytes;
        if (bytes[0] == 0)
            type = Type.STRING;
        else if(bytes[0] == 1)
            type = Type.IMAGE;

    }


    public Type getType(){
        return type;
    }

    public byte[] getBytes(){
        return bytes;
    }

    public Object getFormattedContent() throws IOException {
        byte[] arr = getContent();
        if(type==Type.STRING)
            return new String(arr, StandardCharsets.UTF_8);
        else if (type == Type.IMAGE){
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(arr));
            return img;

        }
        return null;
    }

    public byte[] getContent(){
        byte[] arr = new byte[bytes.length-1];
        for (int i=1; i<bytes.length;i++){
            arr[i-1] = bytes[i];
        }
        return arr;
    }

}

package util.item;

import util.GameObject;
import util.Point3f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Item extends GameObject {

    private int width;
    private int height;

    private int size = 16;

    private int sx1;
    private int sy1;
    private int numFrames;

    private int spacing = 0;

    public Item(String textureLocation,int width,int height, int sx1, int sy1, int numFrames, Point3f centre) {
        super(textureLocation,width,height,centre);

        try {
            super.image = ImageIO.read(new File(textureLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.numFrames = numFrames;
        this.sx1 = sx1;
        this.sy1 = sy1;
    }

    public Item(String textureLocation,int width,int height, int sx1, int sy1, int numFrames, Point3f centre, int size) {
        super(textureLocation,width,height,centre);

        try {
            super.image = ImageIO.read(new File(textureLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.numFrames = numFrames;
        this.sx1 = sx1;
        this.sy1 = sy1;

        this.size = size;
    }

    public Item(String textureLocation,int width,int height, int sx1, int sy1, int numFrames, int spacing, Point3f centre) {
        super(textureLocation,width,height,centre);

        try {
            super.image = ImageIO.read(new File(textureLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.numFrames = numFrames;
        this.sx1 = sx1;
        this.sy1 = sy1;

        this.spacing = spacing;
    }

    public String action(GameObject player){
        if(!player.isAtMaxHealth()) {
            player.reduceHealth(-1);
            return "Delete";
        }else{
            return "Continue";
        }
    }

    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @Override
    public int getHeight() {
        return super.getHeight();
    }

    public int getNumFrames(){return numFrames;}

    public int getSx1() {
        return sx1;
    }

    public int getSy1() {
        return sy1;
    }

    public int getSpacing() {
        return spacing;
    }

    public void setNumFrames(int n){
        numFrames = n;
    }

    public int getSize(){return size;}
}

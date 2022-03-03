package util.item;

import util.GameObject;
import util.Point3f;

import java.io.File;
import java.io.FileReader;

public class Fire extends NPC{

    private boolean hasFire=false;

    public Fire(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre);
    }

    public Fire(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre, int size) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre, size);
    }

    public Fire(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre, int size, String textInput) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre, size, textInput);
    }

    public Fire(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, int spacing, Point3f centre) {
        super(textureLocation, width, height, sx1, sy1, numFrames, spacing, centre);
    }

    @Override
    public void interact(GameObject player){
        hasFire=true;
        super.interact(player);
    }

    public boolean isHasFire(){return hasFire;}
}

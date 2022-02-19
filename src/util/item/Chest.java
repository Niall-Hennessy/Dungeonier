package util.item;

import util.Point3f;

public class Chest extends Interactable{
    public Chest(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre);
    }

    public Chest(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre, int size) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre, size);
    }

    public Chest(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, int spacing, Point3f centre) {
        super(textureLocation, width, height, sx1, sy1, numFrames, spacing, centre);
    }

    @Override
    public void interact(){
        super.interact();
        setNumFrames(3);
    }
}

package util.item;

import util.GameObject;
import util.Point3f;

public class Sword extends NPC {

    private boolean hasSword = false;

    public Sword(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre);
    }

    public Sword(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre, int size) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre, size);
    }

    public Sword(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre, int size, String textInput) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre, size, textInput);
    }

    public Sword(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, int spacing, Point3f centre) {
        super(textureLocation, width, height, sx1, sy1, numFrames, spacing, centre);
    }

    @Override
    public void interact(GameObject player) {
        hasSword = true;
        super.interact(player);
    }

    public boolean isHasSword() {
        return hasSword;
    }
}
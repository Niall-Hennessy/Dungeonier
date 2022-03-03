package util.item;

import util.GameObject;
import util.Point3f;

public class Life extends NPC {

    private boolean hasLife = false;

    public Life(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre);
    }

    public Life(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre, int size) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre, size);
    }

    public Life(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre, int size, String textInput) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre, size, textInput);
    }

    public Life(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, int spacing, Point3f centre) {
        super(textureLocation, width, height, sx1, sy1, numFrames, spacing, centre);
    }

    @Override
    public void interact(GameObject player) {
        hasLife = true;
        player.setMaxHealth(5);
        player.reduceHealth(-5);
        super.interact(player);
    }

    public boolean isHasLife() {
        return hasLife;
    }
}

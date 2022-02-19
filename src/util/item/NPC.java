package util.item;

import util.Point3f;

public class NPC extends Interactable{

    int animationTime=0;

    public NPC(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre);
    }

    public NPC(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre, int size) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre, size);
    }

    public NPC(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, int spacing, Point3f centre) {
        super(textureLocation, width, height, sx1, sy1, numFrames, spacing, centre);
    }

    @Override
    public void interact(){
        System.out.println("You Talked with an NPC");
    }

    @Override
    public int getAnimationTime(){
        animationTime++;
        return animationTime;
    }
}

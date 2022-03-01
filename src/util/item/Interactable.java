package util.item;

import util.GameObject;
import util.Point3f;

public class Interactable extends Item{

    private int animationTime = 0;
    private boolean isInteracted = false;

    public Interactable (String textureLocation,int width,int height, int sx1, int sy1, int numFrames, Point3f centre){
        super(textureLocation,width,height,  sx1,  sy1, numFrames, centre);
    }

    public Interactable (String textureLocation,int width,int height, int sx1, int sy1, int numFrames, Point3f centre, int size){
        super(textureLocation,width,height,  sx1,  sy1, numFrames, centre, size);
    }

    public Interactable (String textureLocation,int width,int height, int sx1, int sy1, int numFrames, int spacing, Point3f centre){
        super(textureLocation,width,height,  sx1,  sy1, numFrames, spacing, centre);
    }

    public void interact(){
        isInteracted = true;
    }

    public void interact(GameObject player){
        isInteracted = true;
    }

    public int getAnimationTime(){
        if(animationTime < 40 && isInteracted)
            animationTime++;
        return animationTime;
    }

    public int getSize(){
        return super.getSize();
    }

    public String getDialog(){
        return "";
    }
}

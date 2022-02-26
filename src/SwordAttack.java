import util.GameObject;
import util.Point3f;

public class SwordAttack extends GameObject {

    private int timer;

    public SwordAttack(String textureLocation, int width, int height, Point3f centre) {
        super(textureLocation, width, height, centre);
    }

    public SwordAttack(String textureLocation, int width, int height, Point3f centre, String direction) {
        super(textureLocation, width, height, centre, direction);
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer){
        this.timer = timer;
    }

    public void countDown(){timer--;}
}

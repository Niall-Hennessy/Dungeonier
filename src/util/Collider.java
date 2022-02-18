package util;

public class Collider extends GameObject{

    public Collider() {
    }

    public Collider(String textureLocation, int width, int height, Point3f centre) {
        super(textureLocation, width, height, centre);
    }

    public Collider(String textureLocation, int width, int height, Point3f centre, String direction) {
        super(textureLocation, width, height, centre, direction);
    }
}

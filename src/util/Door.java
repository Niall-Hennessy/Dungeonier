package util;

public class Door extends GameObject{

    private String destination;

    public Door(String destination,int width,int height,Point3f centre){
        super("res/Bullet.png", width, height, centre);
        this.destination = destination;
    }

    public String getDestination(){return destination;}
}

package util;

public class Door extends GameObject{

    private String destination;
    private Point3f destinationPosition;

    public Door(String destination, int width,int height,Point3f centre){
        super("res/Bullet.png", width, height, centre);
        this.destination = destination;
        this.destinationPosition = new Point3f(0,0,0);
    }

    public Door(String destination, Point3f destinationPosition, int width,int height,Point3f centre){
        super("res/Bullet.png", width, height, centre);
        this.destination = destination;
        this.destinationPosition = destinationPosition;
    }

    public String getDestination(){return destination;}

    public Point3f getDestinationPosition(){return destinationPosition;}
}

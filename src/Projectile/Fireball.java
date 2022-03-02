package Projectile;

import Projectile.Projectile;
import util.GameObject;
import util.Point3f;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

public class Fireball extends GameObject {

    private int time=200;

    public Fireball() {
    }

    public Fireball(String textureLocation, int width, int height, Point3f centre) {
        super(textureLocation, width, height, centre);
    }

    public Fireball(String textureLocation, int width, int height, Point3f centre, String direction) {
        super(textureLocation, width, height, centre, direction);
    }

    public int tick(){
        time--;
        return time;
    }
}

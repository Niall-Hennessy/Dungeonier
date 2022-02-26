package Enemy;

import util.Point3f;
import util.item.Item;

import static java.lang.Math.random;

public class Skeleton extends Enemy{

    public Skeleton(Point3f centre) {
        super("gfx/Character/Enemy/skeleton.png", 100, 100, centre);
        super.setDetectionRadius(500);
        super.health = 2;
    }

    @Override
    public Item getDrop(){
        int random = (int) (Math.random()*100);

        if(random < 33)
            return new Item("gfx/objects.png", 100, 100, 0, 48, 4, this.getCentre());
        else
            return null;
    }
}
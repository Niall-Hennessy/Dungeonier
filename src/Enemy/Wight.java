package Enemy;

import util.Point3f;
import util.item.Item;

public class Wight extends Enemy{

    public Wight(Point3f centre) {
        super("gfx/Character/Enemy/Wight.png", 100, 100, centre);
        super.setDetectionRadius(500);
        super.health = 5;
    }

    @Override
    public Item getDrop(){
        int random = (int) (Math.random()*100);

        if(random < 100)
            return new Item("gfx/objects.png", 100, 100, 0, 48, 4, this.getCentre());
        else
            return null;
    }
}

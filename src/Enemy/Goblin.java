package Enemy;

import util.Point3f;

public class Goblin extends Enemy{

    public Goblin(Point3f centre) {
        super("gfx/Character/Enemy/Goblin Warrior.png", 100, 100, centre);
        super.setDetectionRadius(500);
        super.health = 3;
    }
}

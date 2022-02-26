package Enemy;

import util.Point3f;

public class RedSlime extends Enemy{
    public RedSlime(Point3f centre) {
        super("gfx/slime_monster.png", 75, 75, centre);
        super.setDetectionRadius(500);
    }
}

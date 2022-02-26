package Enemy;

import util.GameObject;
import util.Point3f;
import util.item.Item;

public class Enemy extends GameObject {

    private int detectionRadius=1;

    private boolean detected = false;

    private boolean damaged = false;

    private int damageTimer = 0;

    public Enemy(String textureLocation, int width, int height, Point3f centre) {
        super(textureLocation, width, height, centre);
    }

    public int getDetectionRadius(){return detectionRadius;}

    public void setDetectionRadius(int detectionRadius){this.detectionRadius = detectionRadius;}

    public void setDetected(boolean b){detected = b;}

    public boolean isDetected(){return detected;}

    public boolean isDamaged() {
        if(damageTimer > 0) {
            damageTimer--;
            if(damageTimer == 0)
                damaged = false;
        }
        return damaged;
    }

    public void setDamaged(boolean damaged) {
        damageTimer = 20;
        this.damaged = damaged;
    }

    public Item getDrop(){
        return null;
    }
}

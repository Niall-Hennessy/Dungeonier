package Level;

import Tiles.TileMap;
import util.Collider;
import util.Door;
import util.GameObject;
import util.Point3f;
import util.item.Interactable;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

public class Level {

    String levelName;
    File TextureToLoad;
    File BGM;

    TileMap tileMap;

    private int enemyLimit = 0;

    public Level(String name, String spriteSheet, File BGM){
        this.tileMap = new TileMap(name, spriteSheet);
        this.levelName = name;
        this.BGM = BGM;
    }

    public Level(String name, String spriteSheet, int spriteSheetWidth, int spriteSheetHeight, File BGM){
        this.tileMap = new TileMap(name, spriteSheet, spriteSheetWidth,  spriteSheetHeight);
        this.levelName = name;
        this.BGM = BGM;
    }

    public Level(String name, String spriteSheet, int spriteSheetWidth, int spriteSheetHeight, int enemyLimit, File BGM){
        this.tileMap = new TileMap(name, spriteSheet, spriteSheetWidth,  spriteSheetHeight);
        this.levelName = name;
        this.BGM = BGM;
        this.enemyLimit = enemyLimit;
    }

    public GameObject addEnemy(){
        return new GameObject("gfx/slime_monster.png", 50, 50, new Point3f(((float) Math.random() * 1000), ((float) Math.random() * 1000), 0));
    }

    public int getEnemyLimit(){
        return enemyLimit;
    }

    public String getLevelName(){
        return levelName;
    }

    public File getTextureToLoad(){
        return TextureToLoad;
    }

    public File getBGM(){
        return BGM;
    }

    public TileMap getTileMap(){
        return tileMap;
    }

    public CopyOnWriteArrayList<Collider> getCollisions() {
        return tileMap.getCollisions();
    }

    public CopyOnWriteArrayList<Door> getDoors() {
        return tileMap.getDoors();
    }

    public CopyOnWriteArrayList<Interactable> getInteractables() {
        return tileMap.getInteractables();
    }
}

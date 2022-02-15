package Level;

import Tiles.TileMap;
import util.GameObject;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

public class Level {

    String levelName;
    File TextureToLoad;
    File BGM;

    TileMap tileMap;

    public Level(String name, String spriteSheet, File BGM){
        this.tileMap = new TileMap(name, spriteSheet);
        this.levelName = name;
        this.BGM = BGM;
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

    public CopyOnWriteArrayList<GameObject> getCollisions() {
        return tileMap.getCollisions();
    }
}

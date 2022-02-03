package Level;

import Tiles.TileMap;

import java.io.File;

public class Level {

    String levelName;
    File TextureToLoad;
    File BGM;

    TileMap tileMap = new TileMap();

    public Level(String name, File textureToLoad, File BGM){
        levelName = name;
        this.TextureToLoad = textureToLoad;
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
}

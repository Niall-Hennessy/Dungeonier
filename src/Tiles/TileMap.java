package Tiles;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.GameObject;
import util.Point3f;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class TileMap {
    private int tileSize;
    private int scale;

    private int mapWidth;
    private int mapHeight;

    File spriteSheet;

    private int spriteSheetWidth;
    private int spriteSheetHeight;

    private String[] visualLayer;
    private String[] backLayer;

    CopyOnWriteArrayList<GameObject> collisions = new CopyOnWriteArrayList<GameObject>();

    public TileMap(String mapName, String spriteSheet){

        try {
            readInTileMap(mapName);
        }catch (Exception e){

        }

        tileSize = 16;
        scale = 9;

        this.spriteSheet = new File("gfx/"+spriteSheet+".png");

        spriteSheetWidth = 40;
        spriteSheetHeight = 40;
    }

    public int getBackTile(int x, int y){
        return Integer.parseInt(backLayer[x*mapWidth + y]);
    }

    public int getTile(int x, int y){
        return Integer.parseInt(visualLayer[x*mapWidth + y]);
    }

    public File getSpriteSheet(){
        return spriteSheet;
    }

    public int getSpriteSheetWidth() {
        return spriteSheetWidth;
    }

    public int getSpriteSheetHeight() {
        return spriteSheetHeight;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getScale(){
        return scale;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public String[] getVisualGrid() {
        return visualLayer;
    }

    /*public int[][] getLogicalGrid() {
        return logicalGrid;
    }*/

    public CopyOnWriteArrayList<GameObject> getCollisions() {
        return collisions;
    }

    public void readInTileMap(String mapName){
        try{
            File file = new File("TileMaps/" + mapName + ".tmx");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            String[] backLayer2;
            String process = document.getFirstChild().getTextContent().trim();
            process = process.replace("\n", "");
            process = process.replace("    ", ",");
            backLayer2 = process.split(",");

            int n = backLayer2.length;

            backLayer = Arrays.copyOfRange(backLayer2, 0, (n + 1)/2);
            visualLayer = Arrays.copyOfRange(backLayer2, (n + 1)/2, n);

            mapWidth = (int)Math.sqrt(backLayer.length);
            mapHeight = mapWidth;

            int doorHeight_x = 0;
            int doorHeight_y = 0;
            int doorPos_x = 0;
            int doorPos_y = 0;

            if(document.getChildNodes().item(0).getChildNodes().item(7) != null && mapName.equals("Field")) {
                Node temp = document.getChildNodes().item(0).getChildNodes().item(7).getChildNodes().item(1);

                doorHeight_y = (int)Float.parseFloat(temp.getAttributes().item(0).toString().split("\"")[1]) * 9;
                doorHeight_x = (int)Float.parseFloat(temp.getAttributes().item(3).toString().split("\"")[1]) * 9;
                doorPos_x = (int)Float.parseFloat(temp.getAttributes().item(4).toString().split("\"")[1]) * 9;
                doorPos_y = (int)Float.parseFloat(temp.getAttributes().item(5).toString().split("\"")[1]) * 9;
            }

            collisions.add(new GameObject("res/Bullet.png", doorHeight_x, doorHeight_y, new Point3f(doorPos_x, doorPos_y, 0)));
        }catch(Exception e){
            System.out.println(e);
        }
    }
}

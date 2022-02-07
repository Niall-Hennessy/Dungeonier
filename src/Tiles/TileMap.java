package Tiles;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import util.GameObject;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

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

    ArrayList<GameObject> collisions = new ArrayList<GameObject>();

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

            //System.out.println(document.getChildNodes().item(0).getChildNodes().item(9).getChildNodes().item(1).getAttributes().item(4));
            //NodeList nodeList = document.getChildNodes().item(0).getChildNodes().item(9).getChildNodes();
            //for(int i=0; i<nodeList.getLength(); i++){
                //System.out.println(i);
                //System.out.println(document.getChildNodes().item(0).getChildNodes().item(9).getChildNodes().item(i).getAttributes().item(4));
            //}

        }catch(Exception e){
            System.out.println(e);
        }
    }
}

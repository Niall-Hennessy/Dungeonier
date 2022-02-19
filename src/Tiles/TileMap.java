package Tiles;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.Collider;
import util.Door;
import util.GameObject;
import util.Point3f;
import util.item.Chest;
import util.item.Interactable;
import util.item.NPC;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class TileMap {
    private int tileSize;
    private int scale;

    private int mapWidth;
    private int mapHeight;

    Image spriteSheet;

    private int spriteSheetWidth;
    private int spriteSheetHeight;

    private String[] metaLayer;
    private String[] visualLayer;
    private String[] backLayer;

    CopyOnWriteArrayList<Door> doors = new CopyOnWriteArrayList<Door>();
    CopyOnWriteArrayList<Collider> collisions = new CopyOnWriteArrayList<Collider>();
    CopyOnWriteArrayList<Interactable> interactables = new CopyOnWriteArrayList<Interactable>();

    public TileMap(String mapName, String spriteSheet){

        try {
            readInTileMap(mapName);
        }catch (Exception e){

        }

        tileSize = 16;
        scale = 5;

        try {
            this.spriteSheet = ImageIO.read(new File("gfx/" + spriteSheet + ".png"));
        }catch (Exception e){

        }

        spriteSheetWidth = 40;
        spriteSheetHeight = 40;
    }
    public TileMap(String mapName, String spriteSheet, int spriteSheetWidth, int spriteSheetHeight){

        try {
            readInTileMap(mapName);
        }catch (Exception e){

        }

        tileSize = 16;
        scale = 5;

        try {
            this.spriteSheet = ImageIO.read(new File("gfx/" + spriteSheet + ".png"));
        }catch (Exception e){

        }

        this.spriteSheetWidth = spriteSheetWidth;
        this.spriteSheetHeight = spriteSheetHeight;
    }

    public int getMetaTile(int x, int y){return Integer.parseInt(metaLayer[x*mapWidth + y]);}

    public int getBackTile(int x, int y){
        return Integer.parseInt(backLayer[x*mapWidth + y]);
    }

    public int getTile(int x, int y){
        return Integer.parseInt(visualLayer[x*mapWidth + y]);
    }

    public Image getSpriteSheet(){
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

    public CopyOnWriteArrayList<Door> getDoors() {
        return doors;
    }
    public CopyOnWriteArrayList<Collider> getCollisions() {
        return collisions;
    }
    public CopyOnWriteArrayList<Interactable> getInteractables() {
        return interactables;
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

            backLayer = Arrays.copyOfRange(backLayer2, 0, (n + 1)/3);
            visualLayer = Arrays.copyOfRange(backLayer2, (n + 1)/3, 2*(n + 1)/3);
            metaLayer = Arrays.copyOfRange(backLayer2, 2*(n + 1)/3, n);

            mapWidth = (int)Math.sqrt(backLayer.length);
            mapHeight = mapWidth;

            int doorHeight_x = 0;
            int doorHeight_y = 0;
            int doorPos_x = 0;
            int doorPos_y = 0;

            int item=9;
            if(document.getChildNodes().item(0).getChildNodes().item(item) != null) {

                //System.out.println(document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().item(1).getAttributes().item(0));
                int len = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().getLength();

                for(int i = 1; i < len; i+=2) {
                    Node temp = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().item(i);

                    doorHeight_y = (int) Float.parseFloat(temp.getAttributes().item(0).toString().split("\"")[1]) * 5;
                    doorHeight_x = (int) Float.parseFloat(temp.getAttributes().item(3).toString().split("\"")[1]) * 5;
                    doorPos_x = (int) Float.parseFloat(temp.getAttributes().item(4).toString().split("\"")[1]) * 5;
                    doorPos_y = (int) Float.parseFloat(temp.getAttributes().item(5).toString().split("\"")[1]) * 5;
                    doors.add(new Door(temp.getAttributes().item(2).toString().split("\"")[1], doorHeight_x, doorHeight_y, new Point3f(doorPos_x, doorPos_y, 0)));
                }
            }

            item=11;
            if(document.getChildNodes().item(0).getChildNodes().item(item) != null) {

                int len = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().getLength();

                for(int i = 1; i < len; i+=2) {

                    Node temp = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().item(i);

                    doorHeight_y = (int) Float.parseFloat(temp.getAttributes().item(0).toString().split("\"")[1]) * 5;
                    doorHeight_x = (int) Float.parseFloat(temp.getAttributes().item(2).toString().split("\"")[1]) * 5;
                    doorPos_x = (int) Float.parseFloat(temp.getAttributes().item(3).toString().split("\"")[1]) * 5;
                    doorPos_y = (int) Float.parseFloat(temp.getAttributes().item(4).toString().split("\"")[1]) * 5;
                    collisions.add(new Collider("res/Bullet.png", doorHeight_x, doorHeight_y, new Point3f(doorPos_x, doorPos_y, 0)));
                }
            }

            item=13;
            if(document.getChildNodes().item(0).getChildNodes().item(item) != null) {

                int len = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().getLength();

                for(int i = 1; i < len; i+=2) {
                    Node temp = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().item(i);

                    System.out.println(temp.getAttributes().item(1).toString().split("\"")[1]);

                    String[] type = temp.getAttributes().item(1).toString().split("\"")[1].split("/");
                    int pos_x = (int) Float.parseFloat(temp.getAttributes().item(2).toString().split("\"")[1]) * 5;
                    int pos_y = (int) Float.parseFloat(temp.getAttributes().item(3).toString().split("\"")[1]) * 5;

                    if (type[0].equals("NPC")) {
                        interactables.add(new NPC("gfx/Character/" + type[1] + "/" + type[2] + ".png", 100, 100, 0, 0, 3, new Point3f(pos_x, pos_y, 0), 32));
                    }
                    else if (type[0].equals("Chest")) {
                        System.out.println("Test");
                        interactables.add(new Chest("gfx/Super_Retro_World_free/animation/chest_002.png", 100, 100, 0, 16, 1, new Point3f(pos_x, pos_y, 0), 16));
                    }
                }
            }

        }catch(Exception e){
            System.out.println(e);
        }
    }
}

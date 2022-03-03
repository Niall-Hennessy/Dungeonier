package Tiles;

import Enemy.Enemy;
import Enemy.RedSlime;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.Collider;
import util.Door;
import util.GameObject;
import util.Point3f;
import util.item.*;
import Enemy.Skeleton;
import Enemy.Wight;
import Enemy.WaterGhost;
import Enemy.JackOLantern;
import Enemy.Goblin;

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
    CopyOnWriteArrayList<Enemy> enemies = new CopyOnWriteArrayList<Enemy>();

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

    public CopyOnWriteArrayList<Door> getDoors(String mapName) {
        doors.clear();
        readInDoorList(mapName);
        return doors;
    }
    public CopyOnWriteArrayList<Collider> getCollisions() {
        return collisions;
    }
    public CopyOnWriteArrayList<Interactable> getInteractables() {
        return interactables;
    }
    public CopyOnWriteArrayList<Enemy> getEnemies(String mapName) {
        enemies.clear();
        readInEnemyList(mapName);
        return enemies;
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

            Point3f point3f = new Point3f();

            //Get Doorways
            if(document.getChildNodes().item(0).getChildNodes().item(item) != null) {

                int len = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().getLength();

                for(int i = 1; i < len; i+=2) {
                    Node temp = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().item(i);

                    String pos = temp.getChildNodes().item(1).getChildNodes().item(1).getAttributes().item(1).toString().split("\"")[1];
                    point3f.setX(Float.parseFloat(pos.split(",")[0]) * 5);
                    point3f.setY(Float.parseFloat(pos.split(",")[1]) * 5);

                    doorHeight_y = (int) Float.parseFloat(temp.getAttributes().item(0).toString().split("\"")[1]) * 5;
                    doorHeight_x = (int) Float.parseFloat(temp.getAttributes().item(3).toString().split("\"")[1]) * 5;
                    doorPos_x = (int) Float.parseFloat(temp.getAttributes().item(4).toString().split("\"")[1]) * 5;
                    doorPos_y = (int) Float.parseFloat(temp.getAttributes().item(5).toString().split("\"")[1]) * 5;

                    doors.add(new Door(temp.getAttributes().item(2).toString().split("\"")[1], new Point3f(Float.parseFloat(pos.split(",")[0]) * 5, Float.parseFloat(pos.split(",")[1]) * 5, 0), doorHeight_x, doorHeight_y, new Point3f(doorPos_x, doorPos_y, 0)));
                }
            }

            //Get Colliders
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

            //Get Interactables
            item=13;
            if(document.getChildNodes().item(0).getChildNodes().item(item) != null) {

                int len = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().getLength();

                for(int i = 1; i < len; i+=2) {
                    Node temp = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().item(i);

                    String text = temp.getChildNodes().item(1).getChildNodes().item(1).getAttributes().item(1).toString().split("\"")[1];

                    String[] type = temp.getAttributes().item(1).toString().split("\"")[1].split("/");
                    int pos_x = (int) Float.parseFloat(temp.getAttributes().item(2).toString().split("\"")[1]) * 5;
                    int pos_y = (int) Float.parseFloat(temp.getAttributes().item(3).toString().split("\"")[1]) * 5;

                    if(type[0].equals("Fire")){
                        interactables.add(new Fire("gfx/objects.png", 100, 100, 64, 48, 8, new Point3f(pos_x, pos_y, 0), 32, text));
                    }else if(type[0].equals("Sword")){
                        interactables.add(new Sword("gfx/objects.png", 100, 100, 176, 48, 5, new Point3f(pos_x, pos_y, 0), 32, text));
                    }else if(type[0].equals("Life")){
                        interactables.add(new Life("gfx/objects.png", 100, 100, 176, 48, 5, new Point3f(pos_x, pos_y, 0), 32, text));
                    }
                    else if (type[0].equals("NPC")) {
                        if(text == null)
                            interactables.add(new NPC("gfx/Character/" + type[1] + "/" + type[2] + ".png", 100, 100, 0, 0, 3, new Point3f(pos_x, pos_y, 0), 32));
                        else
                            interactables.add(new NPC("gfx/Character/" + type[1] + "/" + type[2] + ".png", 100, 100, 0, 0, 3, new Point3f(pos_x, pos_y, 0), 32, text));

                    }
                }
            }

            item=15;
            if(document.getChildNodes().item(0).getChildNodes().item(item) != null) {

                int len = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().getLength();

                for(int i = 1; i < len; i+=2) {
                    Node temp = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().item(i);

                    String[] type = temp.getAttributes().item(1).toString().split("\"")[1].split("/");
                    int pos_x = (int) Float.parseFloat(temp.getAttributes().item(2).toString().split("\"")[1]) * 5;
                    int pos_y = (int) Float.parseFloat(temp.getAttributes().item(3).toString().split("\"")[1]) * 5;

                    if (type[0].equals("Skeleton")) {
                        enemies.add(new Skeleton(new Point3f(pos_x, pos_y, 0)));
                    }else if (type[0].equals("Goblin")){
                        enemies.add(new Goblin(new Point3f(pos_x, pos_y, 0)));
                    }
                }
            }

        }catch(Exception e){
            System.out.println(e);
        }
    }

    public void readInEnemyList(String mapName){
        int doorHeight_x = 0;
        int doorHeight_y = 0;
        int doorPos_x = 0;
        int doorPos_y = 0;

        int item=15;

        try {
            File file = new File("TileMaps/" + mapName + ".tmx");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);

            if(document.getChildNodes().item(0).getChildNodes().item(item) != null) {

                int len = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().getLength();

                for(int i = 1; i < len; i+=2) {
                    Node temp = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().item(i);

                    String[] type = temp.getAttributes().item(1).toString().split("\"")[1].split("/");
                    int pos_x = (int) Float.parseFloat(temp.getAttributes().item(2).toString().split("\"")[1]) * 5;
                    int pos_y = (int) Float.parseFloat(temp.getAttributes().item(3).toString().split("\"")[1]) * 5;

                    if (type[0].equals("Skeleton")) {
                        enemies.add(new Skeleton(new Point3f(pos_x, pos_y, 0)));
                    }else if (type[0].equals("Goblin")){
                        enemies.add(new Goblin(new Point3f(pos_x, pos_y, 0)));
                    }else if (type[0].equals("Wight")){
                        enemies.add(new Wight(new Point3f(pos_x, pos_y, 0)));
                    }else if (type[0].equals("JackOLantern")){
                        enemies.add(new JackOLantern(new Point3f(pos_x, pos_y, 0)));
                    }else if (type[0].equals("WaterGhost")){
                        enemies.add(new WaterGhost(new Point3f(pos_x, pos_y, 0)));
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void readInDoorList(String mapName){
        int doorHeight_x = 0;
        int doorHeight_y = 0;
        int doorPos_x = 0;
        int doorPos_y = 0;

        int item=9;
        Point3f point3f = new Point3f();

        try {
            File file = new File("TileMaps/" + mapName + ".tmx");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);

            //Get Doorways
            if(document.getChildNodes().item(0).getChildNodes().item(item) != null) {

                //System.out.println(document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().item(1).getAttributes().item(0));
                int len = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().getLength();

                for(int i = 1; i < len; i+=2) {
                    Node temp = document.getChildNodes().item(0).getChildNodes().item(item).getChildNodes().item(i);

                    String pos = temp.getChildNodes().item(1).getChildNodes().item(1).getAttributes().item(1).toString().split("\"")[1];
                    point3f.setX(Float.parseFloat(pos.split(",")[0]) * 5);
                    point3f.setY(Float.parseFloat(pos.split(",")[1]) * 5);

                    doorHeight_y = (int) Float.parseFloat(temp.getAttributes().item(0).toString().split("\"")[1]) * 5;
                    doorHeight_x = (int) Float.parseFloat(temp.getAttributes().item(3).toString().split("\"")[1]) * 5;
                    doorPos_x = (int) Float.parseFloat(temp.getAttributes().item(4).toString().split("\"")[1]) * 5;
                    doorPos_y = (int) Float.parseFloat(temp.getAttributes().item(5).toString().split("\"")[1]) * 5;

                    doors.add(new Door(temp.getAttributes().item(2).toString().split("\"")[1], new Point3f(Float.parseFloat(pos.split(",")[0]) * 5, Float.parseFloat(pos.split(",")[1]) * 5, 0), doorHeight_x, doorHeight_y, new Point3f(doorPos_x, doorPos_y, 0)));
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
}

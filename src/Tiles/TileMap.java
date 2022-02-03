package Tiles;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class TileMap {
    private int tileSize;
    private int scale;

    private int mapWidth;
    private int mapHeight;

    File spriteSheet;

    private int spriteSheetWidth;
    private int spriteSheetHeight;

    private int[][] visualGrid;
    //private int[][] backLayer;
    private String[] backLayer;
    private int[][] logicalGrid;

    public TileMap(){

        try {
            readInTileMap();
        }catch (Exception e){

        }

        tileSize = 16;
        scale = 3;

        mapHeight = 100;
        mapWidth = 100;

        spriteSheet = new File("gfx/Overworld.png");

        spriteSheetWidth = 40;
        spriteSheetHeight = 40;

        /*backLayer = new int[][]{
                {4 + (40*6),4 + (40*6),4 + (40*6),4 + (40*6),4 + (40*6),4 + (40*6),4 + (40*6),4 + (40*6),4 + (40*6),4 + (40*6)},
                {4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7)},
                {4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7)},
                {4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7)},
                {4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7),4 + (40*7)},
                {4 + (40*8),4 + (40*8),4 + (40*8),4 + (40*8),4 + (40*8),4 + (40*8),4 + (40*8),4 + (40*8),4 + (40*8),4 + (40*8)},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
        };*/

        /*
        backLayer = new int[]{  2, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,};
        */

        /*visualGrid = new int[][]{
                {40,40,40,40,40,6 + (40*34),21 + (40*28),22 + (40*28),23 + (40*28),24 + (40*28)},
                {40,40,40,40,40,20 + (40*28),21 + (40*28),22 + (40*28),23 + (40*28),24 + (40*28)},
                {40,40,40,40,40,20 + (40*29),21 + (40*29),22 + (40*29),23 + (40*29),24 + (40*29)},
                {40,40,40,40,40,20 + (40*30),21 + (40*30),22 + (40*30),23 + (40*30),24 + (40*30)},
                {40,40,40,40,40,20 + (40*31),21 + (40*31),22 + (40*31),23 + (40*31),24 + (40*31)},
                {38,40,40,40,40,20 + (40*32),21 + (40*32),22 + (40*32),23 + (40*32),24 + (40*32)},
                {12,13,9,10,11,40,40,40,40,40},
                {12 + 40,13 + 40,9 + 40,10 + 40,11 + 40,40,40,40,40,40},
                {7 + (40*2),8 + (40*2),9 + (40*2),10 + (40*2),11 + (40*2),40,40,40,40,40},
                {7 + (40*3),8 + (40*3),9 + (40*3),10 + (40*3),11 + (40*3),40,40,40,40,40},
                {7 + (40*4),8 + (40*4),9 + (40*4),10 + (40*4),11 + (40*4),40,40,40,40,40},
                {40,40,40,40,40,40,40,40,40,40},
                {40,40,40,40,40,40,40,40,40,40},
        };*/
    }

    public int getBackTile(int x, int y){
        System.out.println("X: " + x + ", Y: " + y);
        return Integer.parseInt(backLayer[x*10 + y]);
    }

    public int getTile(int x, int y){
        return visualGrid[y][x];
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

    public int[][] getVisualGrid() {
        return visualGrid;
    }

    public int[][] getLogicalGrid() {
        return logicalGrid;
    }

    public void readInTileMap() throws IOException {

        BufferedReader csvReader = new BufferedReader(new FileReader("TileMaps/Teserbester_Base.csv"));
        String row;
        String total = "";
        while ((row = csvReader.readLine()) != null){
            total += row;
        }
        backLayer = total.split(",");
        csvReader.close();

        /*try
        {
            File file = new File("TileMaps/TestMap.tmx");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            //System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("layer");

            System.out.println(nodeList.item(0).getTextContent().);

            /*
            for (int itr = 0; itr < nodeList.getLength(); itr++)
            {
                Node node = nodeList.item(itr);
                System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) node;
                    System.out.println("Student id: "+ eElement.getElementsByTagName("id").item(0).getTextContent());
                    System.out.println("First Name: "+ eElement.getElementsByTagName("firstname").item(0).getTextContent());
                    System.out.println("Last Name: "+ eElement.getElementsByTagName("lastname").item(0).getTextContent());
                    System.out.println("Subject: "+ eElement.getElementsByTagName("subject").item(0).getTextContent());
                    System.out.println("Marks: "+ eElement.getElementsByTagName("marks").item(0).getTextContent());
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        */
    }
}

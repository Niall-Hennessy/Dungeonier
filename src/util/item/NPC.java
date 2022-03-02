package util.item;

import util.GameObject;
import util.Point3f;

import java.io.File;
import java.io.FileReader;
import java.nio.CharBuffer;

public class NPC extends Interactable{

    int animationTime=0;
    String dialog=null;
    int textPos=0;

    private File text;
    private FileReader fr;

    public NPC(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre);
    }

    public NPC(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, Point3f centre, int size) {
        super(textureLocation, width, height, sx1, sy1, numFrames, centre, size);
        try {
            text = new File("Texts/text.txt.txt");
            fr = new FileReader(text);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public NPC(String textureLocation, int width, int height, int sx1, int sy1, int numFrames, int spacing, Point3f centre) {
        super(textureLocation, width, height, sx1, sy1, numFrames, spacing, centre);
    }

    @Override
    public void interact(){

        System.out.println("You Talked with an NPC");
    }

    @Override
    public void interact(GameObject player){
        player.setIsInteracting(true);

        String dir = "";

        if(player.getDirection().equals("right"))
            dir = "left";
        else if(player.getDirection().equals("left"))
            dir = "right";
        else if(player.getDirection().equals("up"))
            dir = "down";
        else if(player.getDirection().equals("down"))
            dir = "up";

        super.direction = dir;

        boolean readDialog = false;

        //https://www.techiedelight.com/read-text-file-using-filereader-java/
        char[] chars = new char[(int) text.length()];
        try {
            if (textPos < (int) text.length()) {
                int length = (int) text.length() - textPos;

                if(length > 30)
                    length = 30;

                fr.read(chars, 0, length);

                String fileContent = new String(chars);
                dialog = fileContent.trim();
                textPos = Math.min(textPos + 30, (int) text.length() + 1);
            }else{
                readDialog=true;
            }
        }catch (Exception e){}

        if(readDialog){
            dialog = "";
            try {
                fr.close();
                fr = new FileReader(text);
            }catch (Exception e){}
            textPos = 0;
            player.setIsInteracting(false);
        }
    }

    @Override
    public String getDialog(){
        return dialog;
    }

    @Override
    public int getAnimationTime(){
        animationTime++;
        return animationTime;
    }
}

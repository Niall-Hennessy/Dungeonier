package Level;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class LevelManager {
    ArrayList<Level> levels;
    private int currentLevel;
    private HashMap<String, Integer> destinations = new HashMap<String, Integer>();


    public LevelManager() {
        levels = new ArrayList<Level>();
        currentLevel = 0;

        Level house = new Level("House", "Inner", new File("sound/Enchanted Festival Loop.wav"));
        levels.add(house);
        destinations.put("House", 0);

        Level outsideHouse = new Level("Outside_House", "Overworld", new File("sound/Enchanted Festival Loop.wav"));
        levels.add(outsideHouse);
        destinations.put("Outside_House", 1);

        Level field = new Level("Field", "Overworld", new File("sound/Enchanted Festival Loop.wav"));
        levels.add(field);
        destinations.put("Field", 2);

        Level town = new Level("Town", "Overworld", new File("sound/Enchanted Festival Loop.wav"));
        levels.add(town);
        destinations.put("Town", 3);

        Level forest = new Level("Forest", "Package_Nature_Dungeon/atlas_16x", 96, 16, 5, new File("sound/Enchanted Festival Loop.wav"));
        levels.add(forest);
        destinations.put("Forest", 4);

        Level fireDungeon = new Level("Fire_Dungeon", "Package_Fire_Dungeon/atlas_16x", 64, 16, 3, new File("sound/Enchanted Festival Loop.wav"));
        levels.add(fireDungeon);
        destinations.put("Fire_Dungeon", 5);

        Level natureDungeon = new Level("Nature_Dungeon", "Package_Nature_Dungeon/atlas_16x", 96, 16, 3, new File("sound/Enchanted Festival Loop.wav"));
        levels.add(natureDungeon);
        destinations.put("Nature_Dungeon", 6);

        Level waterDungeon = new Level("Water_Dungeon", "Package_Water_Dungeon/atlas_16x", 64, 16, 3, new File("sound/Enchanted Festival Loop.wav"));
        levels.add(waterDungeon);
        destinations.put("Water_Dungeon", 7);

        //Sounds used in this game
        //https://opengameart.org/content/enchanted-festival by Matthew Pablo This sound has not been edited in anyway
        //https://opengameart.org/content/menu-music by mrpoly
    }

    public void changeLevel(int n){
        currentLevel = n;
    }

    public void changeLevel(String n){
        currentLevel = destinations.get(n);
    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public Level getCurrentLevel(){
        return levels.get(currentLevel);
    }
}

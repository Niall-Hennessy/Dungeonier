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

        Level fireDungeon = new Level("Fire_Dungeon", "Package_Fire_Dungeon/atlas_16x", 64, 16, 3, new File("sound/Enchanted Festival Loop.wav"));
        levels.add(fireDungeon);
        destinations.put("Fire_Dungeon", 4);

        //Level cave = new Level("Cave", new File("gfx/cave.png"), new File("sound/Shiny_Depths.wav"));
        //levels.add(cave);
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

package Level;

import java.io.File;
import java.util.ArrayList;

public class LevelManager {
    ArrayList<Level> levels;
    private int currentLevel;

    public LevelManager() {
        levels = new ArrayList<Level>();
        currentLevel = 0;

        Level house = new Level("House", "Inner", new File("sound/Enchanted Festival Loop.wav"));
        levels.add(house);

        Level outsideHouse = new Level("Outside_House", "Overworld", new File("sound/Enchanted Festival Loop.wav"));
        levels.add(outsideHouse);

        Level field = new Level("Field", "Overworld", new File("sound/Enchanted Festival Loop.wav"));
        levels.add(field);

        Level town = new Level("Town", "Overworld", new File("sound/Enchanted Festival Loop.wav"));
        levels.add(town);

        //Level cave = new Level("Cave", new File("gfx/cave.png"), new File("sound/Shiny_Depths.wav"));
        //levels.add(cave);
    }

    public void changeLevel(int n){
        currentLevel = n;
    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public Level getCurrentLevel(){
        return levels.get(currentLevel);
    }
}

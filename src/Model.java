import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import Enemy.Enemy;
import Level.Level;
import Level.LevelManager;
import Projectile.Fireball;
import net.java.games.input.Component;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import util.*;
import util.item.Interactable;
import util.item.Item;

import net.java.games.input.*;

import javax.sound.sampled.*;

/*
 * Created by Abraham Campbell on 15/01/2020.
 *   Copyright (c) 2020  Abraham Campbell

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
   
   (MIT LICENSE ) e.g do what you want with this :-) 
 */ 
public class Model {

	private Frame frame;

	private  GameObject PlayerOne;
	private  GameObject PlayerTwo;

	private ControllerKeyboard controllerKeyboard = ControllerKeyboard.getInstance();
	private Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
	private Controller controllerGamepad = null;

	private LevelManager levelManager = new LevelManager();

	private  CopyOnWriteArrayList<Enemy> EnemiesList  = new CopyOnWriteArrayList<Enemy>();
	private  CopyOnWriteArrayList<Item> ItemsList  = new CopyOnWriteArrayList<Item>();
	private  CopyOnWriteArrayList<Interactable> InteractableList  = new CopyOnWriteArrayList<Interactable>();
	private  CopyOnWriteArrayList<Fireball> BulletList  = new CopyOnWriteArrayList<Fireball>();
    private  CopyOnWriteArrayList<GameObject> PlayerList  = new CopyOnWriteArrayList<GameObject>();
	private CopyOnWriteArrayList<Collider> CollisionList  = new CopyOnWriteArrayList<Collider>();
	private CopyOnWriteArrayList<Door> DoorList  = new CopyOnWriteArrayList<Door>();

	public enum GameState{
		PLAY, PAUSE, DEAD, DIALOG
	}

	public enum SelectedItem{
		SWORD, FIRE
	}

	private GameState gameState = GameState.PLAY;

	private SelectedItem selectedItem = SelectedItem.SWORD;

	private int Score=0;
	private boolean paused;
	private boolean twoPlayer;

	private boolean tester = false;

	private Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

	private Clip clip;

	private Level currentLevel;

	private int iFrames;

	//Consider changing to enum
	private int menuItem=0;

	private SwordAttack swordAttack=null;

	private boolean firstCollision = false;

	private Door spawnDoor = null;

	private boolean restart=false;

	private Component.Identifier identifier;

	private float direction=0;

	//FLAGS
	private boolean hasFireball = false;



	public Model() {
		//Create Player
		PlayerOne = new GameObject("gfx/character.png", 100, 100, new Point3f(500, 500, 0));
		PlayerList.add(PlayerOne);
		paused = false;
		twoPlayer = false;
		currentLevel = levelManager.getCurrentLevel();
		CollisionList = currentLevel.getCollisions();
		iFrames = 0;

		for(int i=0; i<controllers.length; i++){
			if (controllers[i].getType() == Controller.Type.STICK) {
				controllerGamepad = controllers[i];
			}
		}

		try {
			clip = AudioSystem.getClip();
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("sound/awesomeness.wav"));
			clip.open(audioInputStream);
			clip.start();
		} catch (Exception e) {
			System.out.println("Caught Exception: " + e);
		}
	}
	
	// This is the heart of the game , where the model takes in all the inputs ,decides the outcomes and then changes the model accordingly. 
	public void gamelogic() 
	{
		// Player Logic first 
		playerLogic(); 
		// Enemy Logic next
		enemyLogic();
		// Bullets move next 
		bulletLogic();
		// interactions between objects 
		gameLogic();
	}

	private void gameLogic() {

		if(PlayerOne.isDead()) {
			gameState = GameState.DEAD;
			clip.stop();
			clip.flush();
			clip.close();
		}

		if(iFrames > 0)
			iFrames--;

		if(swordAttack != null) {
			swordAttack.countDown();
			if(swordAttack.getTimer() == 0) {
				BulletList.remove(swordAttack);
				swordAttack = null;
			}
		}
		
		if(gameState == GameState.PLAY) {
			// this is a way to increment across the array list data structure
			//see if they hit anything
			// using enhanced for-loop style as it makes it alot easier both code wise and reading wise too
			for (Fireball temp : BulletList) {
				for (Enemy enemy : EnemiesList) {
					if (checkColliding(temp.getCentre(), enemy, 50)) {
						enemy.setDamaged(true);
						enemy.reduceHealth(1);

						Vector3f vector3f = PlayerOne.getCentre().getLastVector();
						enemy.getCentre().ApplyVector(vector3f);

						if(enemy.isDead()) {
							Item itemDrop = enemy.getDrop();
							if(itemDrop != null)
								ItemsList.add(itemDrop);
							EnemiesList.remove(enemy);
						}

						BulletList.remove(temp);
					}
				}
			}

			for (Enemy temp : EnemiesList) {
			    for (GameObject players : PlayerList) {
			    	if(checkColliding(temp.getCentre(), players, temp.getDetectionRadius()))
			    		temp.setDetected(true);
					else
						temp.setDetected(false);

                    if (checkColliding(players.getCentre(), temp, 100) && iFrames == 0) {
						iFrames = 120;
                        players.reduceHealth(1);
                    }
                }
			}

			for (Item temp : ItemsList) {
				for (GameObject players : PlayerList) {
					if (checkColliding(players.getCentre(), temp, 100)) {
						String reaction = temp.action(players);
						if(reaction.equals("Delete")) {
							ItemsList.remove(temp);
							Score++;
						}
					}
				}
			}


			boolean wasCollision = false;
			for (Door door : DoorList) {
				for (GameObject players : PlayerList) {
					if (checkColliding(players.getCentre(), door, 100)) {
						wasCollision = true;

						if(firstCollision){
							System.out.println(currentLevel.getLevelName());
							spawnDoor = door;
							firstCollision = false;
						}

						if(door == spawnDoor) {
							continue;
						}

						changeLevel(door.getDestination(), door.getDestinationPosition());
					}
				}
			}
			if(!wasCollision){
				firstCollision = false;
				spawnDoor = null;
			}

			for (Collider temp : CollisionList) {
				for (GameObject players : PlayerList) {
					if(checkColliding(players.getCentre(), temp, 100)) {
						Vector3f vector = players.getCentre().getLastVector();
						vector.setX(-vector.getX());
						vector.setY(-vector.getY());

						players.getCentre().ApplyVector(vector);
					}
				}
			}

			for (Interactable temp : InteractableList) {
				for (GameObject players : PlayerList) {
					if (checkColliding(players.getCentre(), temp, 100)) {
						Vector3f vector = players.getCentre().getLastVector();
						vector.setX(-vector.getX());
						vector.setY(-vector.getY());
					}
				}
			}
		}
	}

	private boolean checkColliding(Point3f player, GameObject collider, int radius){
		int Cx = (int)player.getX();
		int Cy = (int)player.getY();
		int Cr = radius;

		int d=0;
		float s=0;

		if(Cx > collider.getCentre().getX() + collider.getWidth())
			s = Cx - collider.getCentre().getX() + collider.getWidth();
		else if(Cx < collider.getCentre().getX())
			s = Cx - collider.getCentre().getX();
		d+=s*s;

		if(Cy > collider.getCentre().getY() + collider.getHeight())
			s = Cy - collider.getCentre().getY() + collider.getHeight();
		else if(Cy < collider.getCentre().getY())
			s = Cy - collider.getCentre().getY();
		d+=s*s;

		return (d < Cr*Cr);
	}

	private void enemyLogic() {
		// TODO Auto-generated method stub
		if(gameState == GameState.PLAY && !PlayerOne.isInteracting()) {
			float speed = 2f;
			for (Enemy temp : EnemiesList) {

				if(!temp.isDetected())
					continue;

				//Basic enemy tracking feature
				int x = (int) (PlayerOne.getCentre().getX() - temp.getCentre().getX());
				int y = (int) (PlayerOne.getCentre().getY() - temp.getCentre().getY());

				String directionVert=temp.getDirection();
				String directionHor=temp.getDirection();

				boolean horizontal=false;

				if(Math.abs(x) >= Math.abs(y))
					horizontal=true;

				if (x < 0) {
					x = -1;
					directionHor = "left";
				} else if (x > 0) {
					x = 1;
					directionHor = "right";
				} else
					x = 0;

				if (y > 0.5) {
					y = -1;
					directionVert = "down";
				} else if (y < -0.5) {
					y = 1;
					directionVert = "up";
				} else
					y = 0;

				if(horizontal)
					temp.setDirection(directionHor);
				else
					temp.setDirection(directionVert);

				if(temp.isDamaged())
					temp.getCentre().ApplyVector(temp.getCentre().getLastVector());
				else
					temp.getCentre().ApplyVector(new Vector3f(x*speed, y*speed, 0));
			}
		}
	}

	private void bulletLogic() {
		// TODO Auto-generated method stub
		// move bullets 
		if(gameState == GameState.PLAY && !PlayerOne.isInteracting()) {
			for (Fireball temp : BulletList) {

				if(temp.tick() == 0)
					BulletList.remove(temp);

				if (temp.getDirection() == "up")
					temp.getCentre().ApplyVector(new Vector3f(0, 6, 0));
				else if (temp.getDirection() == "right")
					temp.getCentre().ApplyVector(new Vector3f(6, 0, 0));
				else if (temp.getDirection() == "left")
					temp.getCentre().ApplyVector(new Vector3f(-6, 0, 0));
				else if (temp.getDirection() == "down")
					temp.getCentre().ApplyVector(new Vector3f(0, -6, 0));
			}
		}
	}

	private void playerLogic() {
		// smoother animation is possible if we make a target position  // done but may try to change things for students  
		 
		//check for movement and if you fired a bullet 
		if(gameState == GameState.PLAY && !PlayerOne.isInteracting()) {
			float speed = 2f;

			if(controllerGamepad != null){
				controllerGamepad.poll();

				Event event = new Event();
				EventQueue eventQueue = controllerGamepad.getEventQueue();

				eventQueue.getNextEvent(event);
				Component component = event.getComponent();

				if(component != null) {
					identifier = component.getIdentifier();
					if(identifier == Component.Identifier.Axis.POV){
						direction = component.getPollData() * 360;
					}else if(component.getPollData() == 0){
						identifier = null;
					}
				}
				if(identifier != null){
					System.out.println(identifier);

					if(identifier == Component.Identifier.Button._0){
						if(!PlayerOne.isActing())
							useItem();
					}

					if(identifier == Component.Identifier.Button._4){
						if(selectedItem == SelectedItem.SWORD && hasFireball)
							selectedItem = SelectedItem.FIRE;
						else if(selectedItem == SelectedItem.FIRE)
							selectedItem = SelectedItem.SWORD;
					}

					if(identifier == Component.Identifier.Button._5){
						if(selectedItem == SelectedItem.SWORD && hasFireball)
							selectedItem = SelectedItem.FIRE;
						else if(selectedItem == SelectedItem.FIRE)
							selectedItem = SelectedItem.SWORD;
					}

					if(identifier == Component.Identifier.Button._1){
						for (Interactable temp : InteractableList) {
							for (GameObject players : PlayerList) {
								Point3f point3f = new Point3f(0,0,0);
								point3f.setX(players.getCentre().getX() - 50);
								point3f.setY(players.getCentre().getY() - 50);
								if (checkColliding(point3f, temp, 200)) {
									temp.interact(players);
								}
							}
						}
					}

					if (identifier == Component.Identifier.Axis.POV) {
						if(direction == 45){
							PlayerOne.getCentre().ApplyVector(new Vector3f(-2 * speed, 2 * speed, 0));
							PlayerOne.setDirection("up");
						}else if(direction == 90){
							PlayerOne.getCentre().ApplyVector(new Vector3f(0, 2 * speed, 0));
							PlayerOne.setDirection("up");
						}else if(direction == 135){
							PlayerOne.getCentre().ApplyVector(new Vector3f(2 * speed, 2 * speed, 0));
							PlayerOne.setDirection("up");
						}else if(direction == 180){
							PlayerOne.getCentre().ApplyVector(new Vector3f(2 * speed, 0, 0));
							PlayerOne.setDirection("right");
						}else if(direction == 225){
							PlayerOne.getCentre().ApplyVector(new Vector3f(2 * speed, -2 * speed, 0));
							PlayerOne.setDirection("down");
						}else if(direction == 270){
							PlayerOne.getCentre().ApplyVector(new Vector3f(0, -2 * speed, 0));
							PlayerOne.setDirection("down");
						}else if(direction == 315){
							PlayerOne.getCentre().ApplyVector(new Vector3f(-2 * speed, -2 * speed, 0));
							PlayerOne.setDirection("down");
						}else if(direction == 360){
							PlayerOne.getCentre().ApplyVector(new Vector3f(-2 * speed, 0, 0));
							PlayerOne.setDirection("left");
						}else{
							PlayerOne.getCentre().ApplyVector(new Vector3f(0, 0, 0));
						}
					}else{
						System.out.println("nullify");
						identifier = null;
					}
				}
			}

			if (ControllerKeyboard.getInstance().isKeyAPressed()) {
                PlayerOne.getCentre().ApplyVector(new Vector3f(-2 * speed, 0, 0));
                PlayerOne.setDirection("left");
			}

			if (ControllerKeyboard.getInstance().isKeyDPressed()) {
                PlayerOne.getCentre().ApplyVector(new Vector3f(2 * speed, 0, 0));
                PlayerOne.setDirection("right");
			}

			if (ControllerKeyboard.getInstance().isKeyWPressed()) {
                PlayerOne.getCentre().ApplyVector(new Vector3f(0, 2 * speed, 0));
                PlayerOne.setDirection("up");
			}

			if (ControllerKeyboard.getInstance().isKeySPressed()) {
                PlayerOne.getCentre().ApplyVector(new Vector3f(0, -2 * speed, 0));
                PlayerOne.setDirection("down");
			}

			if (ControllerKeyboard.getInstance().isKeyEPressed()) {
				ControllerKeyboard.getInstance().setKeyEPressed(false);
				for (Interactable temp : InteractableList) {
					for (GameObject players : PlayerList) {
						Point3f point3f = new Point3f(0,0,0);
						point3f.setX(players.getCentre().getX() - 50);
						point3f.setY(players.getCentre().getY() - 50);
						if (checkColliding(point3f, temp, 200)) {
							temp.interact(players);
						}
					}
				}
			}

			if(ControllerKeyboard.getInstance().isKey1Pressed()){
				selectedItem = SelectedItem.SWORD;
				ControllerKeyboard.getInstance().setKey1Pressed(false);
			}

			if(ControllerKeyboard.getInstance().isKey2Pressed() && hasFireball){
				selectedItem = SelectedItem.FIRE;
				ControllerKeyboard.getInstance().setKey2Pressed(false);
			}

			if (ControllerKeyboard.getInstance().isKeySpacePressed()) {
				if(!PlayerOne.isActing())
					useItem();
				ControllerKeyboard.getInstance().setKeySpacePressed(false);
			}

			if(ControllerKeyboard.getInstance().isKeyEnterPressed() && !twoPlayer){
				twoPlayer = true;
				PlayerTwo = new GameObject("gfx/npc_test.png",50,50,new Point3f(500,500,0));
				PlayerList.add(PlayerTwo);
			}

			if (ControllerKeyboard.getInstance().isKeyJPressed() && twoPlayer) {
				PlayerTwo.getCentre().ApplyVector(new Vector3f(-2 * speed, 0, 0));
				PlayerTwo.setDirection("left");
			}

			if (ControllerKeyboard.getInstance().isKeyLPressed() && twoPlayer) {
				PlayerTwo.getCentre().ApplyVector(new Vector3f(2 * speed, 0, 0));
				PlayerTwo.setDirection("right");
			}

			if (ControllerKeyboard.getInstance().isKeyIPressed() && twoPlayer) {
				PlayerTwo.getCentre().ApplyVector(new Vector3f(0, 2 * speed, 0));
				PlayerTwo.setDirection("up");
			}

			if (ControllerKeyboard.getInstance().isKeyKPressed() && twoPlayer) {
				PlayerTwo.getCentre().ApplyVector(new Vector3f(0, -2 * speed, 0));
				PlayerTwo.setDirection("down");
			}

			if (ControllerKeyboard.getInstance().isKeyEnterPressed() && twoPlayer) {
				CreateBullet(1);
				ControllerKeyboard.getInstance().setKeyEnterPressed(false);
			}
		}
		else if(gameState == GameState.PAUSE){
			if (ControllerKeyboard.getInstance().isKeyAPressed()) {
				System.out.println("Left");
			}

			if (ControllerKeyboard.getInstance().isKeyDPressed()) {
				System.out.println("Right");
			}

			if (ControllerKeyboard.getInstance().isKeyWPressed()) {
				System.out.println("UP");
			}

			if (ControllerKeyboard.getInstance().isKeySPressed()) {
				System.out.println("Down");
			}

			if(ControllerKeyboard.getInstance().isKeyEnterPressed()){
				System.out.println("Selecting Button");
				if(menuItem == 0){
					try {
						File myObj = new File("SaveFiles/save.txt");
						if (myObj.createNewFile()) {
							System.out.println("File created: " + myObj.getName());
						} else {
							System.out.println("File already exists.");
						}
						FileWriter myWriter = new FileWriter("SaveFiles/save.txt");
						myWriter.write("Files in Java might be tricky, but it is fun enough!");
						myWriter.close();
					} catch (IOException e) {
						System.out.println("An error occurred.");
						e.printStackTrace();
					}
				}

			}
		}else if(gameState == GameState.DEAD){
			if(ControllerKeyboard.getInstance().isKeySpacePressed()){
				System.out.println("Game State is DEAD");
				restart = true;
			}
		}else if(PlayerOne.isInteracting()){

			if(controllerGamepad != null){
				controllerGamepad.poll();

				Event event = new Event();
				EventQueue eventQueue = controllerGamepad.getEventQueue();

				eventQueue.getNextEvent(event);
				Component component = event.getComponent();

				if(component != null) {
					identifier = component.getIdentifier();
					if(component.getPollData() == 0){
						identifier = null;
					}
				}
				if(identifier != null){
					if(identifier == Component.Identifier.Button._1){
						for (Interactable temp : InteractableList) {
							for (GameObject players : PlayerList) {
								Point3f point3f = new Point3f(0,0,0);
								point3f.setX(players.getCentre().getX() - 50);
								point3f.setY(players.getCentre().getY() - 50);
								if (checkColliding(point3f, temp, 200)) {
									System.out.println("Interact");
									temp.interact(players);
								}
							}
						}
					}
					identifier=null;
				}
			}

			if (ControllerKeyboard.getInstance().isKeySpacePressed()) {
				ControllerKeyboard.getInstance().setKeySpacePressed(false);
				for (Interactable temp : InteractableList) {
					for (GameObject players : PlayerList) {
						Point3f point3f = new Point3f(0,0,0);
						point3f.setX(players.getCentre().getX() - 50);
						point3f.setY(players.getCentre().getY() - 50);
						if (checkColliding(point3f, temp, 200)) {
							temp.interact(players);
						}
					}
				}
			}

			if (ControllerKeyboard.getInstance().isKeyEPressed()) {
				ControllerKeyboard.getInstance().setKeyEPressed(false);
				for (Interactable temp : InteractableList) {
					for (GameObject players : PlayerList) {
						Point3f point3f = new Point3f(0,0,0);
						point3f.setX(players.getCentre().getX() - 50);
						point3f.setY(players.getCentre().getY() - 50);
						if (checkColliding(point3f, temp, 200)) {
							temp.interact(players);
						}
					}
				}
			}
		}
		if(ControllerKeyboard.getInstance().isKeyEscPressed())
		{
			if(gameState == GameState.PLAY)
				gameState = GameState.PAUSE;
			else if(gameState == GameState.PAUSE)
				gameState = GameState.PLAY;

			ControllerKeyboard.getInstance().setKeyEscPressed(false);
		}
	}

	private void useItem(){
		//Add code so that the player is capable of changing Item and then the player item menu whatever
		if(selectedItem == SelectedItem.SWORD) {
			swordAttack();
		}
		else  if(selectedItem == SelectedItem.FIRE) {
			fireballAttack();
		}
	}

	//Item Use Methods
	private void swordAttack() {
		if (swordAttack == null){
			PlayerOne.setActing(true);
			int x = (int) PlayerOne.getCentre().getX();
			int y = (int) PlayerOne.getCentre().getY();

			String direction = PlayerOne.getDirection();

			if (direction.equals("up"))
				y += -100;
			else if (direction.equals("down"))
				y += 100;
			else if (direction.equals("left"))
				x += -100;
			else if (direction.equals("right"))
				x += 100;

			swordAttack = new SwordAttack("res/bullet.png", 50, 50, new Point3f(x, y, 0));
			swordAttack.setTimer(30);
			//BulletList.add(swordAttack);
		}
	}

	private void fireballAttack(){
		CreateBullet(1);
		//PlayerOne.setActing(true);
	}

	private void togglePauseMenu(boolean visible){

	}

	public void changeLevel(String n, Point3f pos){

		firstCollision = true;

		String previousLevel = currentLevel.getLevelName();

		levelManager.changeLevel(n);

		currentLevel = levelManager.getCurrentLevel();

		CollisionList = currentLevel.getCollisions();

		DoorList = currentLevel.getDoors();

		InteractableList = currentLevel.getInteractables();

		EnemiesList = currentLevel.getEnemies();

		PlayerOne.setCentre(new Point3f(1000,1000,0));

		if(currentLevel.getLevelName().equals("Fire_Dungeon"))
			hasFireball=true;

		try {
			clip.stop();
			clip.flush();
			clip.close();
			clip = AudioSystem.getClip();
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(levelManager.getCurrentLevel().getBGM());
			clip.open(audioInputStream);
			clip.start();
		} catch (Exception e) {
			System.out.println("Caught Exception: " + e);
		}
	}

	private void CreateBullet(int n) {
		if(BulletList.size() == 0)
			   BulletList.add(new Fireball("gfx/objects.png",50,100,new Point3f(PlayerOne.getCentre().getX()+PlayerOne.getWidth()/2 - 25,PlayerOne.getCentre().getY() + PlayerOne.getHeight()/2 - 50,0.0f),PlayerList.get(0).getDirection()));
	}

    public CopyOnWriteArrayList<GameObject> getPlayers() {
        return PlayerList;
    }

	public CopyOnWriteArrayList<Enemy> getEnemies() {
		return EnemiesList;
	}

	public CopyOnWriteArrayList<Item> getItems() {
		return ItemsList;
	}

	public CopyOnWriteArrayList<Interactable> getInteractableList() {
		return InteractableList;
	}
	
	public CopyOnWriteArrayList<Fireball> getBullets() {
		return BulletList;
	}

	public CopyOnWriteArrayList<Collider> getCollisionList() {
		return CollisionList;
	}

	public CopyOnWriteArrayList<Door> getDoorListList() {
		return DoorList;
	}

	public int getScore() { 
		return Score;
	}

	public Dimension getSize() {
		return size;
	}

	public Level getCurrentLevel(){
		return levelManager.getCurrentLevel();
	}

	public LevelManager getLevelManager() {
		return levelManager;
	}

	public boolean isPaused() {return paused;}

	public boolean isGameOver(){
		if(gameState == GameState.DEAD)
			return true;
		else
			return false;
	}

	public int getiFrames(){return iFrames;}

	public SelectedItem getSelectedItem(){return selectedItem;}

	public GameState getGameState(){return gameState;}

	public boolean restart(){return restart;}
}


/* MODEL OF your GAME world 
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWNNNXXXKKK000000000000KKKXXXNNNWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWNXXK0OOkkxddddooooooolllllllloooooooddddxkkOO0KXXNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWNXK0OkxddooolllllllllllllllllllllllllllllllllllllllloooddxkO0KXNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXK0OkdooollllllllooddddxxxkkkOOOOOOOOOOOOOOOkkxxdddooolllllllllllooddxO0KXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNK0kxdoollllllloddxkO0KKXNNNNWWWWWWMMMMMMMMMMMMMWWWWNNNXXK00Okkxdoollllllllloodxk0KNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXKOxdooolllllodxkO0KXNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWNXK0OkxdolllllolloodxOKXWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOxdoolllllodxO0KNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXKOkdolllllllloodxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0kdolllllooxk0KNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNK0kdolllllllllodk0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xdolllllodk0XNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWMMMMMMMMMMMWN0kdolllllllllodx0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xoollllodxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWMMMMMMMMMMWNXKOkkkk0WMMMMMMMMMMMMWNKkdolllloololodx0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWN0kdolllllox0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXK0kxk0KNWWWWNX0OkdoolllooONMMMMMMMMMMMMMMMWXOxolllllllollodk0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdollllllllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWN0xooollloodkOOkdoollllllllloxXWMMMMMMMMMMMMMMMWXkolllllllllllllodOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWN0koolllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxolllllllllllllllllllllllllllox0XWWMMMMMMMMMWNKOdoloooollllllllllllok0NWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xoolllllllllllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxllolllllllllllllllllloollllllolodxO0KXNNNXK0kdoooxO0K0Odolllollllllllox0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdolllllllllllllllllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkolllllllllloolllllllllllllllllllolllloddddoolloxOKNWMMMWNKOxdolollllllllodOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMWXOdolllolllllllllllllloxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxlllolllllloxkxolllllllllllllllllolllllllllllllxKWMWWWNNXXXKKOxoollllllllllodOXWMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMWXOdollllllllllllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOollllllllllxKNKOxooollolllllllllllllllllllolod0XX0OkxdddoooodoollollllllllllodOXWMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMN0xollllllllllllllllllllllld0NMMMMMMMMMMMMMMMMMMMMMMMWWNKKNMMMMMMMMMMMW0dlllllllllokXWMWNKkoloolllllllllllllllllllolokkxoolllllllllllllollllllllllllllox0NMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMWKxolllllllllllllllllllllllllloONMMMMMMMMMMMMMMMMMMMWNKOxdookNMMMMMMMMMWXkollllllodx0NWMMWWXkolooollllllllllllllllllllooollllllllllllllolllllllllllloooolloxKWMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMWXOdllllllllllllllooollllllllollld0WMMMMMMMMMMMMMMMMWXOxollllloOWMMMMMMMWNkollloodxk0KKXXK0OkdoollllllllllllllllllllllllllllllollllllllloollllllollllllllllllldOXWMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMN0xolllllllllllolllllllllllloodddddONMMMMMMMMMMMMMMMNOdolllllllokNMMMMMMWNkolllloddddddoooolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllox0NMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMWXkolllllllllllllllllllodxxkkO0KXNNXXXWMMMMMMMMMMMMMMNkolllllllllod0NMMMMMNOollllloollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllolllllllllllllokXWMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMWKxollllllllllllllllllox0NWWWWWMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllllookKNWWNOolollloolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloxKWMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMN0dlllllllllllllllllllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkoloolllollllolloxO0Odllllllllllllllllllllllllllllllllllllllllllllollllllllllllllllllllllllllllllllllllllllllllllld0NWMMMMMMMMMMMMM
MMMMMMMMMMMMMXkolllllllllllllllllolllxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXOO0KKOdollllllllllooolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloONWMMMMMMMMMMMM
MMMMMMMMMMMWXkollllllllllllllllllllllxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWMMMMWNKOxoollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllokXWMMMMMMMMMMM
MMMMMMMMMMWKxollllllllllllllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWKxollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloxKWMMMMMMMMMM
MMMMMMMMMWKxollllllllllllodxkkkkkkkO0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMNKOkO0KK0OkdolllllloolllllllllllloooollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloxKWMMMMMMMMM
MMMMMMMMWKxllllllllllolodOXWWWWWWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxolloooollllllllllllllllloollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxKWMMMMMMMM
MMMMMMMWKxlllllllllollokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxololllllllooolloollllloolloooolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxKWMMMMMMM
MMMMMMWXxllllllllooodkKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKdloollllllllllololodxxddddk0KK0kxxxdollolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXWMMMMMM
MMMMMMXkolllllodk0KXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKdllollllllllllllodOXWWNXXNWMMMMWWWNX0xolollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllokNMMMMMM
MMMMMNOollllodONWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dooollllllllllllodOXNWWWWWWMMMMMMMMMWXOddxxddolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloONMMMMM
MMMMW0dllllodKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKKK0kdlllllllllllloodxxxxkkOOKNWMMMMMMWNNNNNXKOkdooooollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllld0WMMMM
MMMWKxllllloOWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkolllllollllllllllllllllodOKXWMMMMMMMMMMMMWNXKK0OOkkkxdooolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxKWMMM
MMMNkollllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWXOdlllllolllllllllllloloolllooxKWMMMMMMMMMMMMMMMMMMMMWWWNXKOxoollllllllllllllllllllllllllllllllllllllllllllllllllllllllllolokNMMM
MMW0ollllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOOkxdollllllllllllllllllllllllllllox0NWMMMMWWNNXXKKXNWMMMMMMMMMWNKOxolllolllllllllllllllllllllllllllllllllllllllllllllllllllllllo0WMM
MMXxllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXkolllllllllllllllllllllllllllllllllllooxO000OkxdddoooodkKWMMMMMMMMMMWXxllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXWM
MWOollllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXkollllllllllllllllllllllllllllllllllllllllllllllllllllllld0WMMMMMMMMMWKdlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloOWM
MXxllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXkollllllllllllllllllllllllllllllllllooollllllllllllllllllold0WMMMMMMWN0dolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXM
W0ollllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNKkdolllllllllllllllllllllllllllllllllllllllllllllllllolllllllllokKXNWWNKkollllllllloxdollllllllolllllllllllllllllllllllllolllllllllllllolo0W
NkllllloxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllodxkkdoolollllllllxKOolllllllllllllllllllllllollooollllllloolllllloolllllkN
KxllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0doolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllkX0dlllllllllllllllllllloollloOKKOkxdddoollllllllllllllxK
Oolllllo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXXkollllooolllllllllllllllloONMMMWNNNXX0xolllllllllolloO
kolllllo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXWXkollollllllllllllllllllodKMMMMMMMMMMWKxollllolollolok
kllllllo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloolllllllllxXWWXkolllllllllllllllolllloONMMMMMMMMMMMW0dllllllllllllk
xollolld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxllllolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloollllllolloONMMN0xoolllllllolllllllloxXWMMMMMMMMMMMMXxollllllloollx
dollllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloollld0WMMWWXOdollollollllllloxXWMMMMMMMMMMMMMNOollllllokkold
olllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNxlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllldONMMMMWXxollllolllllox0NWMMMMMMMMMMMMMMNOollllllxXOolo
llllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXxllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloONMMMMMXxddxxxxkkO0XWMMMMMMMMMMMMMMMMMNOolllllxKW0olo
llllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllldONWMMMWNXNNWWWMMMMMMMMMMMMMMMMMMMMMMMW0dllollOWW0oll
llllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloxO0KXXXXKKKXNWMMMMMMMMMMMMMMMMMMMMMMMNOdolllkNWOolo
ollllllo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllooooddooloodkKWMMMMMMMMMMMMMMMMMMMMMMWXOolldKNOooo
dollllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloollllo0WMMMMMMMMMMMMMMMMMMMMMMMMXkold0Nkold
xollllloxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllollokNMMMMMMMMMMMMMMMMMMMMMMMMMWOookXXxolx
xolllllloONWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMN00XW0dlox
kollllllloxOKXXNNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOxollllllllllllllllllllllllllllllolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllolllllolo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWOollk
OolllllllllloodddkKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOkkxddooooollllllllllooodxxdollolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkoloO
KdllllllllllllllllxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWNXXXK0OOkkkkkkkkOKXXXNNX0xolllllllllllllllllllllllllllllllllllllllllllllllllllllllloollllllllloox0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKdlldK
NkllllollloolllllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWWWMMMMMMMMMWNOdlllllllllllllllllllllllllllllllllllllllllllllllllllllllollllllllodOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWOolokN
WOolllllllllllolllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllod0NWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXxolo0W
WXxllllllllllllllllox0NWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxollllllllllllllllllllllllllllllllllllllllllllllllllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOollxXM
MWOollllllllllllllooloxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdllllllllllllllllllllllllllllllllllllllllllllllllllllloolld0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdlloOWM
MWXxllolllllllllllllllldOXWWNNK00KXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllllllllllllllllllllllllllllllllllllllllllllllllllllllod0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxollxXWM
MMWOollllllllloollllllolodxkxdollodk0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOollllllllllllllllllllllllllllllllllllllllllllllllllodxOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWN0dlllo0WMM
MMMXxllolllllllllllllllllllllllllllloox0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN0dooollllllllllllllllllllllllllllllllllllllllllllodOXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKOkxxolllokNMMM
MMMW0dlllllllllllllllllllolllllllollollokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdoolllllllllllllllllllllllllllllllllllllllllllxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNOoollllllldKWMMM
MMMMNOollllllllllllllllllllllllllllllllloOWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXKOdolllllllllllllllllllllllllllllllllllllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOolllllllloOWMMMM
MMMMMXkollllllllllllllllllllllllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllllllllllllllllllllllllllllllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllolllllokNMMMMM
MMMMMWXxlllllllllllllllllllllllllllllllloxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0ollllllllllllllllllllllllllllllllllllllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWOollllllllxXWMMMMM
MMMMMMWKdlllllllllllllllllllllllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxolllllllllllllllllllllllllllllllllllllloONWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOolllllllxKWMMMMMM
MMMMMMMW0dlllllllllllllllllllllllllllllloOWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllloollllllllllllllllllllllllllllllllloxkOKKXXKKXNMMMMMMMMMMMMMMMMMMMMMMMMNOolllllldKWMMMMMMM
MMMMMMMMW0dllllllllllllllllllllllllllllldKMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdlllllllllllllllllllllllllllllllllllllllllllloooood0WMMMMMMMMMMMMMMMMMMMMMMMNOollolldKWMMMMMMMM
MMMMMMMMMW0dlllllllllllllllllllllllllllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllllllllllllllllllllllllllllllolllllllllllllllllld0WMMMMMMMMMMMMMMMMMMMMMMWKxllllldKWMMMMMMMMM
MMMMMMMMMMW0dlllllllllllllllllllllllllloxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkolllllllllllllllllllllllllllllllllllllllllllllllllxXMMMMMMMMMMMMMMMMMMMMMWXOdolllldKWMMMMMMMMMM
MMMMMMMMMMMWKxollllllllllllllllllllllllloOWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllllllllllllllllllllllllloolllllllolllollllkNMMMMMMMMMMMMMMMMMMMWXOdolllloxKWMMMMMMMMMMM
MMMMMMMMMMMMWKxollllllllllllllllllllllllod0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkoloollllllllllllllllllllllllllllloddollllllllllllld0WMMMMMMMMMMMMMMMWWNKOdolllllokXWMMMMMMMMMMMM
MMMMMMMMMMMMMWXkollllllllllllllllllllllllldKMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllollllllllllllllllllllllllllllld0XOollllllllllllkNMMMMMMMMMMMMWNK0OkxollllllloONWMMMMMMMMMMMMM
MMMMMMMMMMMMMMMNOdlllllllllllllllllllllllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN0dlllllllllllllllllllllllllolllld0NWN0dlllllloodxkKWMMMMMMMMMMMMNOollllllllllld0NMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMWKxolollllllllllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOolllllllllllllllllllllllllllldONMMMWKkdoooxOXNNWMMMMMMMMMMMMMNOollllllllllokXWMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMWXOdlllllllllllllllllloONWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdllllllllllllllllllllllllllld0NMMMMMWWXXXXNWMMMMMMMMMMMMMMMMW0dlllllllllod0NMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMWKxollolllllllllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxllllllllllllllllllllllllloxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllokXWMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMWNOdollllllloolllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkolllloollllooolllllllllodONWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkllllllolox0NMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMWXkollllllolllllox0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdlllllollllllllllllllodkKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllllllodOXWMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMWKxoolllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN0dollllllllllooddxxk0KNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdollllldOXWMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMN0xolllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxolllllodk0KXNNWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKkdollolodkXWMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMNKxoolllllllllodOKNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdolldOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xoollllodkXWMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNKkolllollllllloxOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOx0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOdolllllldOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKOdollllllllllodx0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOdoollllloxOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xollollollollodxOXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0kdooollllodk0NWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKkdooolllllllllooxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOxdollllllloxOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWN0kdllllllllollllodkOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWXKOkdoolllllloodOKNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0kdolllllllllllllodxO0XNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNX0OxdollloolllloxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0kdoolllllllllllllooxkO0XNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWNX0OkxoololllllllooxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNK0kdoolllllllllllloooodxkO0KXNWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWNXK0Okxdoolllllollllloxk0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOkdoollllllllloolllllloodxkkO00KXXNNWWWWWWMMMMMMMMMWWWWWWWNNXXK00Okxxdoolllllllllllloooxk0KNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNK0kxdoollllllllllllllllllllloodddxxxkkOOOOOOOOOOOkkkxxxdddoollllllllllllllllloodxO0XNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXK0OxdooollllllllllllooolllllllllllllllllllllllllllllllllllllllllllooodkO0KNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXK0OkxdooollllllllllllllllllllllllllllllllllllllllllloooddxkO0KXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNNXK0OOkkxdddoooooollllllllllllllllooooooddxxkOO0KKXNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWNNXXXKK00OOOOOOOOOOOOOOOO00KKXXXNNWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 */


import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Level.Level;
import Projectile.Fireball;
import Tiles.TileMap;
import util.GameObject;
import util.Item;
import util.Point3f;


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
 
 * Credits: Kelly Charles (2020)
 */ 
public class Viewer extends JPanel {
	private long CurrentAnimationTime= 0;

	private boolean lock = false;
	
	Model gameworld;// = new Model();
	Dimension size;
	 
	public Viewer(Model World) {
		this.gameworld=World;
		// TODO Auto-generated constructor stub
	}

	public Viewer(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	public Viewer(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public Viewer(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public void updateview() {
		
		this.repaint();
		// TODO Auto-generated method stub
		
	}
	
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		CurrentAnimationTime++; // runs animation time step 

		size = gameworld.getSize();
		
		//Draw background 
		drawBackground(g);

		//drawGui(g);

		//Draw player

		  
		//Draw Bullets 
		// change back 
		gameworld.getBullets().forEach((temp) -> 
		{ 
			drawBullet((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);
			Fireball fireball = new Fireball();
			fireball.drawProjectile();
		}); 
		
		//Draw Enemies

		gameworld.getCollisionList().forEach((temp) ->
		{
			//drawCollisions((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g, temp.getDirection());
		});
	}
	
	private void drawEnemies(int x, int y, int width, int height, String texture, Graphics g, String direction) {
		File TextureToLoad = new File(texture);  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE 
		try {
			Image myImage = ImageIO.read(TextureToLoad);
			//The spirte is 32x32 pixel wide and 4 of them are placed together so we need to grab a different one each time 
			//remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31
			//int currentPositionInAnimation= ((int) ((CurrentAnimationTime%40)/10))*32
			int currentPositionInAnimation = ((int) ((CurrentAnimationTime%20)/10))*24; //slows down animation so every 10 frames we get another frame so every 100ms
			//System.out.println(CurrentAnimationTime%3);

			int offset;
			if(direction.equals("up"))
				offset = 0;
			else if(direction.equals("down"))
				offset = 48;
			else
				offset = 24;

			g.drawImage(myImage, x,y, x+width, y+height, currentPositionInAnimation  , offset, currentPositionInAnimation+23, 24+offset, null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

	private void drawItems(Item item, Graphics g) {
		int currentPositionInAnimation = ((int) ((CurrentAnimationTime%item.getNumFrames())/1))*16; //slows down animation so every 10 frames we get another frame so every 100ms

		int x = (int)item.getCentre().getX();
		int y = (int)item.getCentre().getY();

		int sx1 = item.getSx1();
		int sy1 = item.getSy1();

		g.drawImage(item.getImage(), x, y, x + 100, y + 100, sx1 + currentPositionInAnimation, sy1, sx1 + currentPositionInAnimation + 16,  sy1 + 16, null);
	}

	private void drawCollisions(int x, int y, int width, int height, String texture, Graphics g, String direction) {
		File TextureToLoad = new File(texture);  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
		try {
			Image myImage = ImageIO.read(TextureToLoad);
			//The spirte is 32x32 pixel wide and 4 of them are placed together so we need to grab a different one each time
			//remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31
			//int currentPositionInAnimation= ((int) ((CurrentAnimationTime%40)/10))*32
			int currentPositionInAnimation = ((int) ((CurrentAnimationTime%20)/10))*24; //slows down animation so every 10 frames we get another frame so every 100ms
			//System.out.println(CurrentAnimationTime%3);

			int offset;
			if(direction == "up")
				offset = 0;
			else if(direction == "down")
				offset = 48;
			else
				offset = 24;

			g.drawImage(myImage, x,y, x+width, y+height, currentPositionInAnimation  , offset, currentPositionInAnimation+23, 24+offset, null);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void drawBackground(Graphics g)
	{
		//JavaScript Guide on the Basic Principles for this idea
		//Using this as a Basis. Obviously Can't copy the code though ;c
		//https://developer.mozilla.org/en-US/docs/Games/Techniques/Tilemaps/Square_tilemaps_implementation:_Scrolling_maps

		Level currentLevel = gameworld.getCurrentLevel();
		Point3f playerPosition = gameworld.getPlayers().get(0).getCentre();
		TileMap tileMap = currentLevel.getTileMap();

		int camera_x = (int) (-playerPosition.getX() + size.getWidth()/2);
		int camera_y = (int) (-playerPosition.getY() + size.getHeight()/2);

		if(camera_x>0 && tileMap.getMapWidth() == 50)
			camera_x = 0;
		else if(camera_x<-4656 && tileMap.getMapWidth() == 50)
			camera_x = -4656;

		if(camera_y>0 && tileMap.getMapWidth() == 50)
			camera_y = 0;
		else if(camera_y<-5840 && tileMap.getMapHeight() == 50)
			camera_y = -5840;

		g.translate(camera_x, camera_y);

		Image myImage = tileMap.getSpriteSheet();
		int tileSize = tileMap.getTileSize();
		int scale = tileMap.getScale();

		int mapWidth = tileMap.getMapWidth();
		int mapHeight = tileMap.getMapHeight();

		int spriteSheetWidth = tileMap.getSpriteSheetWidth();
		int spriteSheetHeight = tileMap.getSpriteSheetHeight();

		int c;
		int r;

		for (c = 0; c < mapWidth; c++) {
			for (r = 0; r < mapHeight; r++) {
				int tile = tileMap.getBackTile(r, c);
				if(tile != -1) { // 0 => empty tile
					g.drawImage(myImage, // image
							0, // target x
							0, // target y
							tileSize * scale, // target width
							tileSize * scale, // target height
							(tile - 1) % spriteSheetWidth * tileSize, // source x
							(tile) / spriteSheetHeight * tileSize, // source y
							(tile - 1) % spriteSheetWidth * tileSize + tileSize, // source width
							(tile) / spriteSheetHeight * tileSize + tileSize, // source height
							null
					);
				}
				g.translate(0,tileSize*scale);
			}
			g.translate(tileSize*scale, -(r*tileSize * scale));
		}
		g.translate(-(c*tileSize * scale), 0);

		c=0;
		r=0;
		for (c = 0; c < tileMap.getMapWidth(); c++) {
		for (r = 0; r < tileMap.getMapWidth(); r++) {
			int tile = tileMap.getTile(r, c);
				if(tile != 0) { // 0 => empty tile
					g.drawImage(myImage, // image
							0, // target x
							0, // target y
							tileSize * scale, // target width
							tileSize * scale, // target height
							(tile - 1) % spriteSheetWidth * tileSize, // source x
							(tile) / spriteSheetHeight * tileSize, // source y
							(tile - 1) % spriteSheetWidth * tileSize + tileSize, // source width
							(tile) / spriteSheetHeight * tileSize + tileSize, // source height
							null
					);
				}
				g.translate(0,tileSize*scale);
			}
			g.translate(tileSize*scale, -(r*tileSize * scale));
		}
		g.translate(-(c*tileSize * scale), 0);

		int finalCamera_x = camera_x;
		int finalCamera_y = camera_y;

		gameworld.getItems().forEach((item) ->
		{
			drawItems(item, g);
		});

		gameworld.getEnemies().forEach((temp) ->
		{
			drawEnemies((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g, temp.getDirection());
		});

		gameworld.getPlayers().forEach((temp) ->
		{
			if(temp.isActing()){
				//.out.println("Performing Action");
				if(drawAttack((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(), g, temp.getDirection()))
				{
					//System.out.println("---------------------------------");
					temp.setActing(false);
				}
			}else {
				drawPlayer((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(), g, temp.getDirection());
			}
			drawGui(finalCamera_x, finalCamera_y, temp,g);
		});

		c=0;
		r=0;
		for (c = 0; c < tileMap.getMapWidth(); c++) {
			for (r = 0; r < tileMap.getMapWidth(); r++) {
				int tile = tileMap.getMetaTile(r, c);
				if(tile != 0) { // 0 => empty tile
					g.drawImage(myImage, // image
							0, // target x
							0, // target y
							tileSize * scale, // target width
							tileSize * scale, // target height
							(tile - 1) % spriteSheetWidth * tileSize, // source x
							(tile) / spriteSheetHeight * tileSize, // source y
							(tile - 1) % spriteSheetWidth * tileSize + tileSize, // source width
							(tile) / spriteSheetHeight * tileSize + tileSize, // source height
							null
					);
				}
				g.translate(0,tileSize*scale);
			}
			g.translate(tileSize*scale, -(r*tileSize * scale));
		}
		g.translate(-(c*tileSize * scale), 0);
	}

	private void drawGui(int a, int b, GameObject Player, Graphics g){
		File TextureToLoad = new File("gfx/objects.png");
		try {
			Image myImage = ImageIO.read(TextureToLoad);
			int x = 64;
			int y = 0;
			int i;

			for(i=0;i<Player.getHealth();i++)
				g.drawImage(myImage,-a+110*i,-b,-a+110*i+100,-b+100,x,y,x + 16,y + 16,null);
			for(int j=i;j<Player.getMaxHealth();j++)
				g.drawImage(myImage,-a+110*j,-b,-a+110*j+100,-b+100,x*2,y,x*2 + 16,y + 16,null);


		}catch (Exception e){

		}
	}

	private void drawBullet(int x, int y, int width, int height, String texture,Graphics g)
	{
		File TextureToLoad = new File(texture);  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE 
		try {
			Image myImage = ImageIO.read(TextureToLoad); 
			//64 by 128 
			 g.drawImage(myImage, x,y, x+width, y+height, 0 , 0, 63, 127, null); 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private void drawPlayer(int x, int y, int width, int height, String texture,Graphics g, String direction) {
		File TextureToLoad = new File(texture);  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
		try {
			Image myImage = ImageIO.read(TextureToLoad);
			//The spirte is 32x32 pixel wide and 4 of them are placed together so we need to grab a different one each time 
			//remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31  
			int currentPositionInAnimation = (((int) ((CurrentAnimationTime%40)/10))*16); //slows down animation so every 10 frames we get another frame so every 100ms
			//int currentPositionInAnimation;
			//currentPositionInAnimation = 48;

			int offset = 0;
			if(direction == "down")
				offset = 0;
			else if(direction == "right")
				offset = 32;
			else if(direction == "up")
				offset = 64;
			else
				offset = 96;

			g.drawImage(myImage, x,y, x+width, y+height, currentPositionInAnimation  , offset, currentPositionInAnimation + 16, offset + 32, null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		//g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer));
		//Lighnting Png from https://opengameart.org/content/animated-spaceships  its 32x32 thats why I know to increament by 32 each time 
		// Bullets from https://opengameart.org/forumtopic/tatermands-art 
		// background image from https://www.needpix.com/photo/download/677346/space-stars-nebula-background-galaxy-universe-free-pictures-free-photos-free-images
		
	}



	private boolean drawAttack(int x, int y, int width, int height, String texture,Graphics g, String direction) {
		File TextureToLoad = new File(texture);  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
		try {
			Image myImage = ImageIO.read(TextureToLoad);
			//The spirte is 32x32 pixel wide and 4 of them are placed together so we need to grab a different one each time
			//remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31
			int currentPositionInAnimation = (((int) ((CurrentAnimationTime%4)/1))*32); //slows down animation so every 10 frames we get another frame so every 100ms

			int offset = 0;
			if(direction == "down")
				offset = 0;
			else if(direction == "up")
				offset = 32;
			else if(direction == "right")
				offset = 64;
			else
				offset = 96;

			int down = 128;
			int side = 8;
			g.drawImage(myImage, x,y, x+width, y+height, currentPositionInAnimation + 8 , down + offset, currentPositionInAnimation + 24, down + offset + 32, null);

			//System.out.println(currentPositionInAnimation);
			if(currentPositionInAnimation == 3*32)
				return true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			return false;
		//g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer));
		//Lighnting Png from https://opengameart.org/content/animated-spaceships  its 32x32 thats why I know to increament by 32 each time
		// Bullets from https://opengameart.org/forumtopic/tatermands-art
		// background image from https://www.needpix.com/photo/download/677346/space-stars-nebula-background-galaxy-universe-free-pictures-free-photos-free-images

	}

}


/*
 * 
 * 
 *              VIEWER HMD into the world                                                             
                                                                                
                                      .                                         
                                         .                                      
                                             .  ..                              
                               .........~++++.. .  .                            
                 .   . ....,++??+++?+??+++?++?7ZZ7..   .                        
         .   . . .+?+???++++???D7I????Z8Z8N8MD7I?=+O$..                         
      .. ........ZOZZ$7ZZNZZDNODDOMMMMND8$$77I??I?+?+=O .     .                 
      .. ...7$OZZ?788DDNDDDDD8ZZ7$$$7I7III7??I?????+++=+~.                      
       ...8OZII?III7II77777I$I7II???7I??+?I?I?+?+IDNN8??++=...                  
     ....OOIIIII????II?I??II?I????I?????=?+Z88O77ZZO8888OO?++,......            
      ..OZI7III??II??I??I?7ODM8NN8O8OZO8DDDDDDDDD8DDDDDDDDNNNOZ= ......   ..    
     ..OZI?II7I?????+????+IIO8O8DDDDD8DNMMNNNNNDDNNDDDNDDNNNNNNDD$,.........    
      ,ZII77II?III??????DO8DDD8DNNNNNDDMDDDDDNNDDDNNNDNNNNDNNNNDDNDD+.......   .
      7Z??II7??II??I??IOMDDNMNNNNNDDDDDMDDDDNDDNNNNNDNNNNDNNDMNNNNNDDD,......   
 .  ..IZ??IIIII777?I?8NNNNNNNNNDDDDDDDDNDDDDDNNMMMDNDMMNNDNNDMNNNNNNDDDD.....   
      .$???I7IIIIIIINNNNNNNNNNNDDNDDDDDD8DDDDNM888888888DNNNNNNDNNNNNNDDO.....  
       $+??IIII?II?NNNNNMMMMMDN8DNNNDDDDZDDNN?D88I==INNDDDNNDNMNNMNNNNND8:..... 
   ....$+??III??I+NNNNNMMM88D88D88888DDDZDDMND88==+=NNNNMDDNNNNNNMMNNNNND8......
.......8=+????III8NNNNMMMDD8I=~+ONN8D8NDODNMN8DNDNNNNNNNM8DNNNNNNMNNNNDDD8..... 
. ......O=??IIIIIMNNNMMMDDD?+=?ONNNN888NMDDM88MNNNNNNNNNMDDNNNMNNNMMNDNND8......
........,+++???IINNNNNMMDDMDNMNDNMNNM8ONMDDM88NNNNNN+==ND8NNNDMNMNNNNNDDD8......
......,,,:++??I?ONNNNNMDDDMNNNNNNNNMM88NMDDNN88MNDN==~MD8DNNNNNMNMNNNDND8O......
....,,,,:::+??IIONNNNNNNDDMNNNNNO+?MN88DN8DDD888DNMMM888DNDNNNNMMMNNDDDD8,.... .
...,,,,::::~+?+?NNNNNNNMD8DNNN++++MNO8D88NNMODD8O88888DDDDDDNNMMMNNNDDD8........
..,,,,:::~~~=+??MNNNNNNNND88MNMMMD888NNNNNNNMODDDDDDDDND8DDDNNNNNNDDD8,.........
..,,,,:::~~~=++?NMNNNNNNND8888888O8DNNNNNNMMMNDDDDDDNMMNDDDOO+~~::,,,.......... 
..,,,:::~~~~==+?NNNDDNDNDDNDDDDDDDDNNND88OOZZ$8DDMNDZNZDZ7I?++~::,,,............
..,,,::::~~~~==7DDNNDDD8DDDDDDDD8DD888OOOZZ$$$7777OOZZZ$7I?++=~~:,,,.........   
..,,,,::::~~~~=+8NNNNNDDDMMMNNNNNDOOOOZZZ$$$77777777777II?++==~::,,,......  . ..
...,,,,::::~~~~=I8DNNN8DDNZOM$ZDOOZZZZ$$$7777IIIIIIIII???++==~~::,,........  .  
....,,,,:::::~~~~+=++?I$$ZZOZZZZZ$$$$$777IIII?????????+++==~~:::,,,...... ..    
.....,,,,:::::~~~~~==+?II777$$$$77777IIII????+++++++=====~~~:::,,,........      
......,,,,,:::::~~~~==++??IIIIIIIII?????++++=======~~~~~~:::,,,,,,.......       
.......,,,,,,,::::~~~~==+++???????+++++=====~~~~~~::::::::,,,,,..........       
.........,,,,,,,,::::~~~======+======~~~~~~:::::::::,,,,,,,,............        
  .........,.,,,,,,,,::::~~~~~~~~~~:::::::::,,,,,,,,,,,...............          
   ..........,..,,,,,,,,,,::::::::::,,,,,,,,,.,....................             
     .................,,,,,,,,,,,,,,,,.......................                   
       .................................................                        
           ....................................                                 
               ....................   .                                         
                                                                                
                                                                                
                                                                 GlassGiant.com
                                                                 */

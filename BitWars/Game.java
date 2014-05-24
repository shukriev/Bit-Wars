import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

	public class Game extends Canvas {
		
		private BufferStrategy strategy;
		private boolean gameRunning = true;
		
		private ArrayList entities = new ArrayList();
		private ArrayList removeList = new ArrayList();
		private Entity king;
		
		private double moveSpeed = 300;
		
		private long lastFire = 0;
		private long firingInterval = 500;
		
		private int OpponentCounter;
		private int Score;
		
		private String message = "";
		
		private boolean WaitingForKey = true;
		private boolean leftPressed = false;
		private boolean rightPressed = false;
		private boolean firePressed = false;
		
		private boolean logicRequiredThisLoop = false;

		public Game() {
			JFrame container = new JFrame("Bit Wars");
			JPanel panel = (JPanel) container.getContentPane();
			
			panel.setPreferredSize(new Dimension(790, 591));
			
			setBounds(0, 0, 791, 591);
			
			panel.add(this);
			setIgnoreRepaint(true);
			container.pack();
			container.setResizable(false);
			container.setVisible(true);
			
			container.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			
			addKeyListener(new KeyInputHandler());
			requestFocus();

			createBufferStrategy(2);
			strategy = getBufferStrategy();
			InitializationEntities();
		}
		
		private void startGame() {
			
			entities.clear();
			InitializationEntities();
			
			leftPressed = false;
			rightPressed = false;
			firePressed = false;
		}
		
		private void InitializationEntities() {
			king = new KingEntity(this,"Images/King.png", 370, 550);
			entities.add(king);
			
			OpponentCounter = 0;
			for (int row = 0;row < 2;row++) {
				for (int x = 0;x < 4;x++) {
					Entity opponent1 = new OpponentEntity(this,"Images/opp.png", 140 + (x * 20), (50) + row * 30);
					entities.add(opponent1);
					Entity opponent2 = new OpponentEntity(this,"Images/opp.png", 240 + (x * 20), (50) + row * 30);
					entities.add(opponent2);
					Entity opponent3 = new OpponentEntity(this,"Images/opp.png", 350 + (x * 20), (50) + row * 30);
					entities.add(opponent3);
					Entity opponent4 = new OpponentEntity(this,"Images/opp.png", 460 + (x * 20), (50) + row * 30);
					entities.add(opponent4);
					Entity opponent5 = new OpponentEntity(this,"Images/opp.png", 560 + (x * 20), (50) + row * 30);
					entities.add(opponent5);
					OpponentCounter += 5;
				}
			}
			
			for (int row = 0;row < 2;row++) {
				for (int x = 0;x < 4;x++) {
					Entity opponent1 = new OpponentEntity(this,"Images/opp.png", 180 + (x * 20), (110) + row * 30);
					entities.add(opponent1);
					Entity opponent2 = new OpponentEntity(this,"Images/opp.png", 290 + (x * 20), (110) + row * 30);
					entities.add(opponent2);
					Entity opponent3 = new OpponentEntity(this,"Images/opp.png", 410 + (x * 20), (110) + row * 30);
					entities.add(opponent3);
					Entity opponent4 = new OpponentEntity(this,"Images/opp.png", 520 + (x * 20), (110) + row * 30);
					entities.add(opponent4);
					OpponentCounter += 4;
				}
			}
			
			for (int row = 0;row < 2;row++) {
				for (int x = 0;x < 4;x++) {
					Entity opponent1 = new OpponentEntity(this,"Images/opp.png", 245 + (x * 20), (180) + row * 30);
					entities.add(opponent1);
					Entity opponent2 = new OpponentEntity(this,"Images/opp.png", 350 + (x * 20), (180) + row * 30);
					entities.add(opponent2);
					Entity opponent3 = new OpponentEntity(this,"Images/opp.png", 455 + (x * 20), (180) + row * 30);
					entities.add(opponent3);
					OpponentCounter += 3;
				}
			}
			
			for (int row = 0;row < 2;row++) {
				for (int x = 0;x < 4;x++) {
					Entity opponent1 = new OpponentEntity(this,"Images/opp.png", 310 + (x * 20), (240) + row * 30);
					entities.add(opponent1);
					Entity opponent2 = new OpponentEntity(this,"Images/opp.png", 390 + (x * 20), (240) + row * 30);
					entities.add(opponent2);
					Entity opponent3 = new OpponentEntity(this,"Images/opp.png", 310 + (x * 20), (240) + row * 30);
					entities.add(opponent3);
					Entity opponent4 = new OpponentEntity(this,"Images/opp.png", 390 + (x * 20), (240) + row * 30);
					entities.add(opponent4);
					OpponentCounter += 4;
				}
			}
			
			
		}

		public void updateLogic() {
			
			logicRequiredThisLoop = true;
		}
		
		public void removeEntity(Entity entity) {
			
			removeList.add(entity);
		}
		
		public void notifyDeath() {
			
			Score = 0;
			message = "Fuck! You lose, try again?";
			WaitingForKey = true;
		}
		
		public void Win() {
			
			message = "Well done! You Win!";
			WaitingForKey = true;
		}
		
		public void OpponentKilled() {
			
			OpponentCounter--;
			Score++;
			
			if (Score == 50) {
				firingInterval -= 200;
			}
			
			if (OpponentCounter == 0) {
				Win();
			}
			
			for (int i = 0;i < entities.size();i++) {
				Entity entity = (Entity) entities.get(i);
				
				if (entity instanceof OpponentEntity) {
					entity.setHorizontMove(entity.getHorizontMove() * 1.02);
				}
			}
		}
		
		public void tryToFire() {
			
			if (System.currentTimeMillis() - lastFire < firingInterval) {
				return;
			}
			
			lastFire = System.currentTimeMillis();
			ShotEntity shot = new ShotEntity(this,"Images/shot.png",king.getX() + 5,king.getY() - 30);
			entities.add(shot);
		}

		public void gameLoop() throws IOException {
			
			long lastLoopTime = System.currentTimeMillis();
			
			while (gameRunning) {
				
				long delta = System.currentTimeMillis() - lastLoopTime;
				lastLoopTime = System.currentTimeMillis();
				
				Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
				
				Font myFont = new Font("TimesRoman", Font.ITALIC, 15);
				
				URL url = this.getClass().getClassLoader().getResource("Images/background.png");
				Image img = ImageIO.read(url);

				g.drawImage(img, 0, 0, null);
								
				String counterString = Integer.toString(Score);
				g.setColor(Color.white);
			    
				g.setFont(myFont);
				g.drawString(counterString,(218 - g.getFontMetrics().stringWidth(counterString)) / 2, 40);

				if (!WaitingForKey) {
					for (int i = 0;i < entities.size();i++) {
						Entity entity = (Entity) entities.get(i);
						entity.move(delta);
					}
				}
				
				for (int i = 0;i < entities.size();i++) {
					Entity entity = (Entity) entities.get(i);
					entity.draw(g);
				}
				
				for (int p = 0;p < entities.size();p++) {
					for (int  s = p + 1;s < entities.size();s++) {
						Entity me = (Entity) entities.get(p);
						Entity him = (Entity) entities.get(s);
						
						if (me.collidesWith(him)) {
							me.collidedWith(him);
							him.collidedWith(me);
						}
					}
				}
				
				entities.removeAll(removeList);
				removeList.clear();

				if (logicRequiredThisLoop) {
					for (int i = 0;i < entities.size();i++) {
						Entity entity = (Entity) entities.get(i);
						entity.doLogic();
					}
					
					logicRequiredThisLoop = false;
				}
				
				if (WaitingForKey) {
					g.setColor(Color.white);
					g.drawString(message,(800 - g.getFontMetrics().stringWidth(message)) / 2, 250);
					g.drawString("Press any key",(800 - g.getFontMetrics().stringWidth("Press any key")) / 2, 320);
				}
				
				g.dispose();
				strategy.show();
				
				king.setHorizontMove(0);
				
				if ((leftPressed) && (!rightPressed)) {
					king.setHorizontMove(-moveSpeed);
				} else if ((rightPressed) && (!leftPressed)) {
					king.setHorizontMove(moveSpeed);
				}
				
				if (firePressed) {
					tryToFire();
				}
				
				try {
						Thread.sleep(10); 
					} catch (Exception e) {
						
					}
			}
		}
		
		private class KeyInputHandler extends KeyAdapter {
			
			private int pressCount = 1;
			
			public void keyPressed(KeyEvent e) {
				if (WaitingForKey) {
					return;
				}
				
				
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					leftPressed = true;
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					rightPressed = true;
				}
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					firePressed = true;
				}
			} 
			
			public void keyReleased(KeyEvent e) {
				
				if (WaitingForKey) {
					return;
				}
				
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					leftPressed = false;
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					rightPressed = false;
				}
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					firePressed = false;
				}
			}

			public void keyTyped(KeyEvent e) {
				
				if (WaitingForKey) {
					if (pressCount == 1) {
						WaitingForKey = false;
						startGame();
						pressCount = 0;
					} else {
						pressCount++;
					}
				}
				if (e.getKeyChar() == 27) {
					System.exit(0);
				}
			}
		}
		
		public static void main(String argv[]) {
			
			Game g =new Game();
			try {
				g.gameLoop();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
}

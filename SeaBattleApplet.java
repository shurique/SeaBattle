import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.net.*;

public class SeaBattleApplet extends Applet implements ActionListener, MouseListener, MouseMotionListener {
	private Button start, quit;
	
	private cDraw draw;
	private Point leftPlayer;
	private Point leftEnemy;
	
	private SeaBattle game;
	private String playerName;
	private int[] gameScore;
	private cShip ship;
	private Point startIndex;
	
	private boolean dragdrop = false;	
	private boolean setup = false;
	private boolean move = false;
	
	public void init() {		
		leftPlayer = new Point(50, 100);
		leftEnemy = new Point(550, 100);
		
		start = new Button("START BATTLE");
		start.setEnabled(false);
		quit = new Button("QUIT");	
		
		this.add(start);
		this.add(quit);		
		
		this.setBackground(Color.gray);
		this.setSize(800, 600);	
		
		start.addActionListener(this);
		quit.addActionListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		gameScore = new int[2];		
		try {
			playerName = this.getParameter("name");			
			gameScore[0] = Integer.parseInt(this.getParameter("plrScore"));
			gameScore[1] = Integer.parseInt(this.getParameter("cmpScore"));
		} catch (NumberFormatException exp) {
			gameScore[0] = 0;
			gameScore[1] = 0;
			playerName = " XXX";
		}
		
		game = new SeaBattle();		
	}
	
	public void actionPerformed(ActionEvent ae) {
		String action = ae.getActionCommand();
		if (action.equals("START BATTLE")) {
			this.showStatus("Setup enemy ships... Please wait.");
			game.resetMap();
			game.setupEnemyShips();
			game.start();	
			this.showStatus("Start game");
			start.setEnabled(false);
		} else {
			/*URL url = this.getCodeBase();
			String strUrl = url.toString();
			strUrl += "db_save.php?name=" + playerName;
			strUrl += "&ps=" + Integer.toString(gameScore[0]);
			strUrl += "&cs=" + Integer.toString(gameScore[1]);
			try {
				url = new URL(strUrl);
			} catch (MalformedURLException exp) {
				url = this.getCodeBase();
			}
			this.getAppletContext().showDocument(url);*/
			System.exit(0);
		}
		this.repaint();
	}
	
	private void startDragDrop() {
		//starting drag & drop
		this.setCursor(new Cursor(Cursor.MOVE_CURSOR));		
		dragdrop = true;
	}
	
	private void finishDragDrop() {
		//finishing drag & drop
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		dragdrop = false;
	}
	
	private void getShipInArea(MouseEvent me) {
		//Get ship in area
		startIndex = draw.getFieldIndex(leftPlayer, me.getX(), me.getY());
		ship = game.getPlayerMap().getShip(startIndex);
		if (ship == null)  {
			this.showStatus("Ship is not avaliable!");				
		}
	}
	
	public void mouseDragged(MouseEvent me) {
		if (game.isInitialMode()) {			
			if (me.getX() > 325 && me.getX() < 525 
					&& me.getY() > 100 && me.getY() < 300) {	//Legend area
				//Setup new ship
				setup = true;
				this.startDragDrop();				
				this.showStatus("Setup a new ship...");
			}
			if (me.getX() > 50 && me.getX() < 250 
					&& me.getY() > 100 && me.getY() < 300 && setup == false && move == false) {	//Player field area				
				//Moving ship
				move = true;
				this.getShipInArea(me);
				this.startDragDrop();
				this.showStatus("Moving a ship...");
			}			
		}
	}
	
	public void mouseClicked(MouseEvent me) {	
		if (game.isInitialMode()) {
			if (me.getX() > 50 && me.getX() < 250 
					&& me.getY() > 100 && me.getY() < 300) {	//Player field
				this.getShipInArea(me);
				if (ship.getSize() != 1) {
					if (game.getPlayerMap().isTurnShipAvaliable(ship.getSize(), startIndex, !ship.isHorizontal())) {
						game.getPlayerMap().turnShip(ship, startIndex);
						this.showStatus("Ship turned");
						setup = false;
					}
				}
			}
		} else {
			if (!game.isWinMode()) { 
				//����� ����
				if (me.getX() > 550 && me.getX() < 750 
						&& me.getY() > 100 && me.getY() < 300) {	//Enemy field
					startIndex = draw.getFieldIndex(leftEnemy, me.getX(), me.getY());
					boolean success = game.shoot(startIndex);
					if (game.getEnemyMap().isShipsSunk()) {
						this.showStatus("Player wins!");
						game.stop();
						gameScore[0] ++;
					} else {
						if (!success) {
							game.computerShoot();
							if (game.getPlayerMap().isShipsSunk()) {
								this.showStatus("Computer wins!");
								game.stop();
								gameScore[1] ++;
							}
							
						}
					}
				}		
			} else {
				game = new SeaBattle();
				this.showStatus("");
			}
		}
		repaint();
	}
	
	public void mouseEntered(MouseEvent me) {
		
	}
	
	public void mouseExited(MouseEvent me) {
		
	}
	
	public void mousePressed(MouseEvent me) {
		
	}	
	
	public void mouseReleased(MouseEvent me) {
		if (game.isInitialMode() && dragdrop == true) {
			this.finishDragDrop();
			Point finishIndex = draw.getFieldIndex(leftPlayer, me.getX(), me.getY());
			
			if (setup) {
				setup = false;
				ship = game.getPlayerMap().getNewShip();
				if (game.getPlayerMap().isSpaceAvailable(ship.getSize(), finishIndex, true)) {
					game.getPlayerMap().setShip(ship, finishIndex, true);
					this.showStatus("Ship is set");
				} else {
					game.getPlayerMap().addNewShip();
				}
			} else {
				
				if (move && game.getPlayerMap().isSpaceAvailable(ship.getSize(), finishIndex, ship.isHorizontal()) && ship != null) {
					move = false;
					game.getPlayerMap().moveShip(ship, startIndex, finishIndex);
					this.showStatus("Ship moved");
				} else {
					this.showStatus("");
				}
			}			
			ship = null;
		}		
		repaint();
	}
	
	public void mouseMoved(MouseEvent me) {
		
	}	
	
	
	public void paint (Graphics g) {
		start.setLocation(100, 400);
		quit.setLocation(600, 400);
		
		start.setSize(150, 30);
		quit.setSize(150, 30);
		
		draw = new cDraw(g, 200);
		
		game.getPlayerMap().showMap();
		draw.drawMap(50, 100, game.getPlayerMap());	
		
		
		game.getEnemyMap().hideMap();
		draw.drawMap(550, 100, game.getEnemyMap());
		
		//Print legend
		int[] shipCount = null; 
		if (game.isInitialMode()) {
			shipCount = game.getPlayerMap().getShipCount();
		} else {
			shipCount = game.getEnemyMap().getShipCount();
		}
		draw.drawLegend(325, 100, shipCount);
		
		//Print user information
		g.setFont(new Font("Arial", Font.PLAIN, 20));
		g.drawString("Player " + playerName + " - Computer", 290, 325);		
		g.drawString(Integer.toString(gameScore[0]) + " : " + Integer.toString(gameScore[1]), 375, 350);
		
		//Check to see whether all the ships
		if (game.isInitialMode() && game.getPlayerMap().isShipsSunk()) {
			start.setEnabled(true);
		}	
		
	}

}

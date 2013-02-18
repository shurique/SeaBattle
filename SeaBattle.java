import java.awt.Point;
import java.util.Random;

enum GameMode {INITIAL, GAME, WIN};

public class SeaBattle {
	private GameMode mode;		//Initial - false, game - true;
	private boolean person;		//Computer = false, player = true;	
	
	private Random rnd;
	
	private cMap playerMap;
	private cMap enemyMap;	
	
	private cShip hypShip;
	private boolean shotResult;
	
	public SeaBattle() {
		mode = GameMode.INITIAL;
		playerMap = new cMap();
		enemyMap = new cMap();
		rnd = new Random();		
	}
	
	public boolean isInitialMode() {
		return mode == GameMode.INITIAL ? true : false;
	}
	
	public boolean isWinMode() {
		return mode == GameMode.WIN ? true : false;
	}
	
	private void switchTurn() {person = !person;}
	
	public void start() {
		mode = GameMode.GAME;		
		person = true;
		
		playerMap.resetShipCount();
		enemyMap.resetShipCount();
		
		shotResult = false;
	}	
	
	public void stop() {
		mode = GameMode.WIN;
	}
	
	
	public cMap getPlayerMap() {return playerMap;}
	public cMap getEnemyMap() {return enemyMap;}
		
	public void resetMap() {
		enemyMap = new cMap();
	}
	
	public void setupEnemyShips() {		
		int rndCounter = 0;
		boolean horizontal = false;
		Point index = new Point();		

		cShip newShip = null;
		do {
			newShip = enemyMap.getNewShip();
			if (newShip != null) {
				rndCounter = 0;
				do {
					//Get random values
					index.x = rnd.nextInt(10);
					index.y = rnd.nextInt(10);
					horizontal = rnd.nextBoolean();	
					rndCounter++;	
					
				} while (!enemyMap.isSpaceAvailable(newShip.getSize(), index, horizontal)
						|| rndCounter > 100);
				
				if (rndCounter < 100) {
					//Setup new ship					
					enemyMap.setShip(newShip, index, horizontal);
				} else {
					enemyMap = new cMap();
					rnd = new Random();
					enemyMap.resetShipCount();
				}			
			}
		} while (newShip != null);	
	}
	
	public boolean shoot(Point shtIndex) {		
		boolean r = false;
		cMap tmpMap = person ? enemyMap : playerMap;
		int value = tmpMap.getFieldValue(shtIndex.x, shtIndex.y);		
		switch (value) {
		case 0:			
			tmpMap.setFieldValue(shtIndex, 1);
			break;
		case 1:			
		case 2:			
		case 3:
			r = true;			
			break;
		default:			
			cShip tempShip = tmpMap.getShip(shtIndex);
			tmpMap.setFieldValue(shtIndex, 2);
			if (!person) {
				if (shotResult && hypShip != null) {
					Point stIndex = this.calcLeftPoint(hypShip.getIndexes(), shtIndex, hypShip.isHorizontal());
					boolean orientation = this.calcOrientation(stIndex, shtIndex);					
					hypShip = new cShip(hypShip.getSize() + 1, stIndex, orientation);
				} else {
					shotResult = true;
					hypShip = new cShip(1, shtIndex, true);
				}
			}
			if (tempShip.isSunk(shtIndex)) {
				//The ship is sunk
				if (person) {
					enemyMap.sinkShip(tempShip.getSize());
				} else {
					playerMap.sinkShip(tempShip.getSize());
					
					shotResult = false;
					hypShip = null;
				}
				tmpMap.setAroundValue(tempShip.getAroundPoints());
			}
			r = true;
			break;
		}
		return r;		
	}
	
	private Point getRndPoint() {
		return new Point(rnd.nextInt(10), rnd.nextInt(10));
	}
	
	public void computerShoot() {
		this.switchTurn();		
		Point shtIndex = null;		
		
		boolean result = false;		
		do {			
			if (shotResult && hypShip != null) {
				int field[][] = playerMap.getInvisibleMap();
				Point ind[] = null;
				if (hypShip.getSize() == 1) {
					ind = hypShip.getAroundPoints();
					for (int i = 0; i < ind.length; i++) {
						if (this.tryValue(field, ind[i]) == 0) {
							Point stIndex = hypShip.getIndexes()[0];
							if (hypShip.isHorizontal() && ind[i].x == stIndex.x) {
								shtIndex = new Point(ind[i]);
								break;
							} else {
								if (ind[i].y == stIndex.y) {
									shtIndex = new Point(ind[i]);
									break;
								}									
							}
						}
					}				
				} else {
					//decks > 1
					ind = hypShip.getIndexes();
					shtIndex = new Point();
					for (int i = 0; i < ind.length; i++) {
						if (hypShip.isHorizontal()) {
							shtIndex.x = ind[i].x;
							shtIndex.y = ind[i].y - 1;							
						} else {
							shtIndex.x = ind[i].x - 1;
							shtIndex.y = ind[i].y;							
						}
						if (this.tryValue(field, shtIndex) == 0) break;
						
						if (hypShip.isHorizontal()) {
							shtIndex.x = ind[i].x;
							shtIndex.y = ind[i].y + 1;							
						} else {
							shtIndex.x = ind[i].x + 1;
							shtIndex.y = ind[i].y;							
						}
						if (this.tryValue(field, shtIndex) == 0) break;
					}
				}
			} else {
				shtIndex = this.getRndPoint();				 
			}			
			result = this.shoot(shtIndex);	
		} while (result == true);
		
		this.switchTurn();
	}
	
	private int tryValue(int field[][], Point index) {
		try {
			return field[index.x][index.y];
		} catch (ArrayIndexOutOfBoundsException exp) {
			return -1;
		}
	}
	
	private boolean calcOrientation(Point first, Point second) {		
		if (first.x == second.x) {
			return true;
		} else {
			return false;
		}
	}
	
	private Point calcLeftPoint(Point arr[], Point ind, boolean horizontal) {
		Point result = new Point(ind);
		for (int i = 0; i < arr.length; i++) {
			if (horizontal) {
				if (arr[i].y < result.y) {
					result.setLocation(arr[i]);
				}
			} else {
				if (arr[i].x < result.x) {
					result.setLocation(arr[i]);
				}
			}
		}
		return result;
	}
}

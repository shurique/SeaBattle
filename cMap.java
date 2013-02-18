import java.util.Vector;
import java.awt.Point;

public class cMap {
	private boolean visible;	//Visibility on board ships
	private int[][] field;
	private Vector<cShip> ship;
	
	private int[] shipCount;
	private int lastShipIndex;
	
	public cMap() {
		visible = true;
		field = new int[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				field[i][j] = 0;
			}
		}
		
		ship = new Vector<cShip> (9);
		
		shipCount = new int[4];
		this.resetShipCount();
		lastShipIndex = 0;
	}
	
	public void setShip(cShip newShip, Point index, boolean horizontal) {
		newShip.setData(index, horizontal);	
		
		Point indexes[] = newShip.getIndexes();
		for (int i = 0; i < newShip.getSize(); i++) {
			field[indexes[i].x][indexes[i].y] = ship.size() + 4; 
		}		
		ship.add(newShip);
	}
	
	private void deleteShip(cShip delShip, Point index) {
		Point indexes[] = delShip.getIndexes();
		ship.remove(field[index.x][index.y] - 4);
		
		for (int i = 0; i < delShip.getSize(); i++) {
			field[indexes[i].x][indexes[i].y] = 0; 
		}		
	}
	
	public void moveShip(cShip newShip, Point startIndex, Point finishIndex) {
		this.deleteShip(newShip, startIndex);
		this.setShip(newShip, finishIndex, newShip.isHorizontal());
	}
	
	public void turnShip(cShip newShip, Point index) {
		this.deleteShip(newShip, index);
		this.setShip(newShip, index, !newShip.isHorizontal());
	}
	
	public boolean isSpaceAvailable(int shipSize, Point index, boolean horizontal) {
		boolean result = true;		
		cShip testShip = new cShip(shipSize);
		testShip.setData(index, horizontal);				
		
		//Test inner indexes
		Point indexes[] = testShip.getIndexes();
		for (int i = 0; i < shipSize; i++) {
			try {
				if (field[indexes[i].x][indexes[i].y] > 3) {
					result = false;
				}
			} catch (ArrayIndexOutOfBoundsException exp) {
				return false;
			}	
		}
		if (result == true) {
			//Test around indexes
			Point outIndex[] = testShip.getAroundPoints();		
			
			for (int i = 0; i < outIndex.length; i++) {
				try {
					if (field[outIndex[i].x][outIndex[i].y] > 3) {
						result = false;
						break;
					}
				} catch (ArrayIndexOutOfBoundsException exp) {
					continue;
				}				
			}
		}
		return result;
	}
	
	public boolean isTurnShipAvaliable(int shipSize, Point index, boolean horizontal) {
		boolean result = true;		
		cShip testShip = new cShip(shipSize);
		testShip.setData(index, horizontal);
		
		int testField[][] = field.clone();
		for (int i = 0; i < 10; i++) {
			testField[i] = field[i].clone();
		}
		cShip setShip = this.getShip(index);
		if (setShip != null) {
			Point indexes[] = setShip.getIndexes();
			for (int i = 0; i < setShip.getSize(); i++) {
				testField[indexes[i].x][indexes[i].y] = 0;
			}
		}
				
		//Test inner indexes
		Point indexes[] = testShip.getIndexes();
		for (int i = 1; i < shipSize; i++) {
			if (testField[indexes[i].x][indexes[i].y] > 3) {
				result = false;
			}
		}
		if (result == true) {
			//Test around indexes
			Point outIndex[] = testShip.getAroundPoints();		
			
			for (int i = 0; i < outIndex.length; i++) {
				try {
					if (testField[outIndex[i].x][outIndex[i].y] > 3) {
						result = false;
						break;
					}
				} catch (ArrayIndexOutOfBoundsException exp) {
					continue;
				}				
			}
		}
		return result;
	}
	
	public void printMap() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (field[i][j] == 0) System.out.print("- ");
				else System.out.print(field[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	boolean isVisible() {return visible;}
	
	public void hideMap() {visible = false;}
	public void showMap() {visible = true;}
	
	public int getFieldValue(int i, int j) {return field[i][j];}	
	public void setFieldValue(Point index, int value) {
		if (value >= 0 && value <= 3) {
			field[index.x][index.y] = value;
		}
	}
	public cShip getShip(Point index) {
		cShip retShip = null;
		if (field[index.x][index.y] > 3) {
			retShip = ship.get(field[index.x][index.y] - 4);
		}
		return retShip;
	}	
	
	public void setAroundValue(Point ind[]) {		
		for (int i = 0; i < ind.length; i++) {
			try {
				field[ind[i].x][ind[i].y] = 3;
			} catch (ArrayIndexOutOfBoundsException exp) {
				continue;
			}
		}
	}
	
	public int[][] getInvisibleMap() {
		int newField[][] = field.clone();
		for (int i = 0; i < 10; i++) {
			newField[i] = field[i].clone();
			for (int j = 0; j < 10; j++) {
				if (newField[i][j] > 3) {
					newField[i][j] = 0;
				}
			}
		}
		return newField;
	}
	
	public cShip getNewShip() {
		cShip newShip = null;
		for (int i = 0; i < 4; i++) {
			if (shipCount[i] != 0) {
				newShip = new cShip(i + 1);
				shipCount[i] --;
				lastShipIndex = i;
				break;
			}
		}
		return newShip;
	}
	
	public void addNewShip() {
		shipCount[lastShipIndex]++;
	}
	
	public void sinkShip(int decks) {
		shipCount[decks - 1] --;
	}
	
	public void resetShipCount() {
		for (int i = 0; i < 4; i++) {
			shipCount[i] = 4 - i;
		}
	}
	
	public int[] getShipCount() {return shipCount;}
	
	public boolean isShipsSunk() {
		boolean res = true;
		for (int i = 0; i < 4; i++) {
			if (shipCount[i] != 0) {
				res = false;
			}
		}
		return res;
	}
}

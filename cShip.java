import java.awt.Point;

public class cShip {
	private int decks;					//Number of decks	
	private boolean value[];		//Ship status
	private Point index[];				//Ship position
	private boolean horizontal;
	
	public cShip(int countDecks) {
		decks = countDecks;
		horizontal = true;
		
		value = new boolean[decks];
		for (int i = 0; i < decks; i++) {
			value[i] = true;
		}
		
		index = new Point[decks];
	}
	
	public cShip(int countDecks, Point stIndex, boolean newOrientation) {
		this(countDecks);
		this.setData(stIndex, newOrientation);
	}
	
	public void setData(Point stIndex, boolean newOrientation) {
		horizontal = newOrientation;
		
		for (int i = 0; i < decks; i++) {			
			index[i] = new Point();
			if (horizontal) {
				index[i].x = stIndex.x;
				index[i].y = stIndex.y + i;
			} else {
				index[i].x = stIndex.x + i;
				index[i].y = stIndex.y;
			}
		}
	}
	
	public boolean isSunk() {
		int i = 0; 
		while (value[i] == false) { i++;}
		return i == decks ? true: false;
	}
	
	public boolean isSunk(Point shtIndex) {
		int damage = 0;
		for (int i = 0; i < decks; i++) {			
			if (shtIndex.x == index[i].x
					&& shtIndex.y == index[i].y) {
				value[i] = false;
				damage++;
			} else {
				if (!value[i]) {
					damage++;
				}
			}
		}
		return damage == decks ? true: false;
	}
	
	public Point[] getIndexes() {return index;}	
	public int getSize() {return decks;}
	
	public Point[] getAroundPoints() {
		int count = 6 + decks * 2;
		Point aroundIndex[] = new Point[count];
		
		if (horizontal) {			
			aroundIndex[0] = new Point(index[0].x - 1, index[0].y - 1);
			aroundIndex[1] = new Point(index[0].x, index[0].y - 1);
			aroundIndex[2] = new Point(index[0].x + 1, index[0].y - 1);
			
			aroundIndex[3] = new Point(index[decks - 1].x - 1, index[decks - 1].y + 1);
			aroundIndex[4] = new Point(index[decks - 1].x, index[decks - 1].y + 1);
			aroundIndex[5] = new Point(index[decks - 1].x + 1, index[decks - 1].y + 1);
		
		} else {
			aroundIndex[0] = new Point(index[0].x - 1, index[0].y - 1);
			aroundIndex[1] = new Point(index[0].x - 1, index[0].y);
			aroundIndex[2] = new Point(index[0].x - 1, index[0].y + 1);
			
			aroundIndex[3] = new Point(index[decks - 1].x + 1, index[decks - 1].y - 1);
			aroundIndex[4] = new Point(index[decks - 1].x + 1, index[decks - 1].y);
			aroundIndex[5] = new Point(index[decks - 1].x + 1, index[decks - 1].y + 1);
		}
		int i = 6;
		for (int j = 0; j < decks; j++) {
			if (horizontal) {
				aroundIndex[i++] = new Point(index[j].x - 1, index[j].y);
				aroundIndex[i++] = new Point(index[j].x + 1, index[j].y);
			} else {
				aroundIndex[i++] = new Point(index[j].x, index[j].y - 1);
				aroundIndex[i++] = new Point(index[j].x, index[j].y + 1);
			}
		}
		return aroundIndex;
	}
	
	public boolean isHorizontal() {return horizontal;}	
	
}

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;


public class cDraw {
	private Graphics2D gr;
	private int width;	
	private int step;
	
	public cDraw(Graphics g, int newWidth) {
		gr = (Graphics2D)g;
		width = newWidth;
		step = width / 10;
	}
	
	public Point getFieldIndex(Point leftCorner, int x, int y) {
		Point newIndex = new Point(-1, -1);				
		for (int i = 0; i < 10; i++) {			 
			if ((x >= leftCorner.x + i * step) && (x < leftCorner.x + (i + 1) * step)) {
				newIndex.y = i;
			}
			if ((y >= leftCorner.y + i * step) && (y < leftCorner.y + (y + 1) * step)) {
				newIndex.x = i;
			}
		}		
		return newIndex;
	}
	
	public void drawMap(int x, int y, cMap map) {
		BasicStroke penLine = new BasicStroke(2, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 30);		
		BasicStroke penShipLine = new BasicStroke(3, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 30);
		
		String[] widthChar = {"a","b","c","d","e","f","g","h","i","k"};
		String[] heightChar = {"1","2","3","4","5","6","7","8","9","10"};	
		
		int tempX = x + step;
		int tempY = y + step;
		
		gr.setStroke(penLine);
		gr.setColor(Color.blue);
		gr.drawRect(x, y, width, width);
		
		gr.setFont(new Font("Arial", Font.PLAIN, 12));		
		for (int i = 0; i < 10; i++) {		
			//Print lines
			gr.setColor(Color.blue);
			gr.drawLine(tempX, y, tempX, y + width);
			gr.drawLine(x, tempY, x + width, tempY);
			
			//Print signature
			gr.setColor(Color.black);
			gr.drawString(widthChar[i], tempX - step / 2, y - step / 2);
			gr.drawString(heightChar[i], x - step, tempY - step / 4);
			
			tempX += step;
			tempY += step;
		}
		
		gr.setColor(Color.black);
		gr.setFont(new Font("Arial", Font.PLAIN, 20));
		gr.setStroke(penShipLine);
		tempY = y;				
		for (int i = 0; i < 10; i++) {
			tempX = x;			
			for (int j = 0; j < 10; j++) {
				//if (map.getFieldValue(i, j) != 0) gr.drawString(Integer.toString(map.getFieldValue(i, j)), tempX + step / 4, tempY + step - 3);
				switch(map.getFieldValue(i, j)) {
				case 0:
					//empty				
					
					break;
				case 1:		//shoot
					gr.drawString("o", tempX + step / 4, tempY + step - 3);
					break;
				case 2:		//hit
					this.drawDeck(tempX, tempY);
					gr.drawString("x", tempX + step / 4 + 1, tempY + step - 3);
					break;
				case 3:		//around					
					gr.drawString("+", tempX + step / 4, tempY + step - 2);
					break;
				default: 
					if (map.isVisible()) {						
						this.drawDeck(tempX, tempY);
					}
				}
				
				tempX += step;
			}
			tempY += step;
		}
		
	}
	private void drawDeck(int x, int y) {
		gr.drawRect(x, y, step, step);
	}
	
	public void drawLegend(int x, int y, int[] statistic) {
		int tx;
		int ty = width / 4;
		
		for (int i = 0; i < 4; i++) {
			tx = x;			
			for (int j = 0; j < i + 1; j++) {
				this.drawDeck(tx, y);
				tx += step; 
			}			
			gr.drawString("- " + Integer.toString(statistic[i]), x + step * 5, y + 15);
			y += ty;
		}
	}
}

package andy.tetris;

import java.awt.Color;

public class PlayArea {
	int width;
	int height;
	Color[][] area;
	
	public PlayArea(int width, int height){
		this.width = width;
		this.height = height;
		
		area = new Color[height][width];
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Color[][] getArea() {
		return area;
	}
}

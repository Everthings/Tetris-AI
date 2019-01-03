package andy.tetris.pieces;

import java.awt.Color;
import java.awt.Graphics;

public abstract class AbstractPiece {
	Orientation o = Orientation.UP;
	public Pair centerPos;
	Pair[] positions = new Pair[4];
	public Color c;
	
	AbstractPiece(Pair startPos) {
		centerPos = startPos;
		calculateRotation();
	}
	
	public void rotate(Direction dir) {
		if(dir == Direction.COUNTER_CLOCKWISE) {
			switch(o) {
			case UP:
				o = Orientation.LEFT;
				break;
			case DOWN:
				o = Orientation.RIGHT;
				break;
			case LEFT:
				o = Orientation.DOWN;
				break;
			case RIGHT:
				o = Orientation.UP;
				break;
			}
		}else {
			switch(o) {
			case UP:
				o = Orientation.RIGHT;
				break;
			case DOWN:
				o = Orientation.LEFT;
				break;
			case LEFT:
				o = Orientation.UP;
				break;
			case RIGHT:
				o = Orientation.DOWN;
				break;
			}
		}
		
		calculateRotation();
	}
	
	abstract public void calculateRotation();
	
	public Pair[] getPositions() {
		return positions;
	}
}

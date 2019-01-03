package andy.tetris.pieces;

import java.awt.Color;

public class J extends AbstractPiece{

	public J(Pair startPos) {
		super(startPos);
		
		c = Color.BLUE;
	}
	
	@Override
	public void calculateRotation() {
		if(o == Orientation.UP) {
			positions[0] = centerPos.add(new Pair(1, -1));
			positions[1] = centerPos.add(new Pair(-1, 0));
			positions[2] = centerPos.add(new Pair(0, 0));
			positions[3] = centerPos.add(new Pair(1, 0));
		}else if(o == Orientation.DOWN) {
			positions[0] = centerPos.add(new Pair(-1, 0));
			positions[1] = centerPos.add(new Pair(0, 0));
			positions[2] = centerPos.add(new Pair(1, 0));
			positions[3] = centerPos.add(new Pair(-1, 1));
		}else if(o == Orientation.LEFT) {
			positions[0] = centerPos.add(new Pair(0, -1));
			positions[1] = centerPos.add(new Pair(0, 0));
			positions[2] = centerPos.add(new Pair(0, 1));
			positions[3] = centerPos.add(new Pair(-1, -1));
		}else if(o == Orientation.RIGHT) {
			positions[0] = centerPos.add(new Pair(1, 1));
			positions[1] = centerPos.add(new Pair(0, -1));
			positions[2] = centerPos.add(new Pair(0, 0));
			positions[3] = centerPos.add(new Pair(0, 1));
		}
	}
}

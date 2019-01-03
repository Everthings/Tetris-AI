package andy.tetris.pieces;

import java.awt.Color;

public class I extends AbstractPiece{

	public I(Pair startPos) {
		super(startPos);
		
		c = Color.CYAN;
	}

	@Override
	public void calculateRotation() {
		if(o == Orientation.UP || o == Orientation.DOWN) {
			positions[0] = centerPos.add(new Pair(-2, 0));
			positions[1] = centerPos.add(new Pair(-1, 0));
			positions[2] = centerPos.add(new Pair(0, 0));
			positions[3] = centerPos.add(new Pair(1, 0));
		}else if(o == Orientation.LEFT || o == Orientation.RIGHT) {
			positions[0] = centerPos.add(new Pair(0, -1));
			positions[1] = centerPos.add(new Pair(0, 0));
			positions[2] = centerPos.add(new Pair(0, 1));
			positions[3] = centerPos.add(new Pair(0, 2));
		}
	}
}

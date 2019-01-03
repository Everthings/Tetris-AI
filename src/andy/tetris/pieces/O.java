package andy.tetris.pieces;

import java.awt.Color;
import java.awt.Graphics;

public class O extends AbstractPiece{

	public O(Pair startPos) {
		super(startPos);
		
		c = Color.YELLOW;
	}

	@Override
	public void calculateRotation() {
		positions[0] = centerPos.add(new Pair(0, -1));
		positions[1] = centerPos.add(new Pair(1, -1));
		positions[2] = centerPos.add(new Pair(0, 0));
		positions[3] = centerPos.add(new Pair(1, 0));
	}
}

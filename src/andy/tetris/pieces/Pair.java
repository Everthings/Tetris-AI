package andy.tetris.pieces;

public class Pair {
	double x;
	double y;
	
	public Pair(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public Pair add(Pair p2) {
		return new Pair(x + p2.getX(), y + p2.getY());
	}
}

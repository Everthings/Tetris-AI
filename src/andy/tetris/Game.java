package andy.tetris;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import andy.tetris.pieces.AbstractPiece;
import andy.tetris.pieces.Direction;
import andy.tetris.pieces.I;
import andy.tetris.pieces.J;
import andy.tetris.pieces.L;
import andy.tetris.pieces.O;
import andy.tetris.pieces.Pair;
import andy.tetris.pieces.S;
import andy.tetris.pieces.T;
import andy.tetris.pieces.Z;

@SuppressWarnings("serial")
public class Game extends JPanel{
	
	int width;
	int height;
	
	AbstractPiece currentPiece;
	AbstractPiece[] nextPieces = new AbstractPiece[3];
	AbstractPiece savedPiece = null;
	PlayArea area;
	
	int score = 0;
	
	double genes[] = {0.33228235923436766, 0.7008006248419443, -0.6251913365805418, -0.08719352399931382};
	//double genes[] = {-0.17177110278469856, -0.14387619853455655, -0.919371595726622, -0.3233422284738853};
	
	BufferedImage T_Image;
	BufferedImage Z_Image;
	BufferedImage S_Image;
	BufferedImage O_Image;
	BufferedImage L_Image;
	BufferedImage J_Image;
	BufferedImage I_Image;
	
	boolean ended = false;
	
	int targetX;
	int targetRotation;
	
	Game(int width, int height){
		this.width = width;
		this.height = height;
		
		setSize(width, height);
		setVisible(true);
		setBackground(Color.BLACK); 
		
		initImages();
		
		area = new PlayArea(10, 20);
		
		currentPiece = generateNewPiece();
		nextPieces[0] = generateNewPiece();
		nextPieces[1] = generateNewPiece();
		nextPieces[2] = generateNewPiece();
		
		decideOrientation();
		
		startUpdateThread();
		
		repaint();
	}
	
	void initImages() {
		try {
			T_Image = ImageIO.read(new File("TetrisImages/T.png"));
			S_Image = ImageIO.read(new File("TetrisImages/S.png"));
			J_Image = ImageIO.read(new File("TetrisImages/J.png"));
			Z_Image = ImageIO.read(new File("TetrisImages/Z.png"));
			O_Image = ImageIO.read(new File("TetrisImages/O.png"));
			I_Image = ImageIO.read(new File("TetrisImages/I.png"));
			L_Image = ImageIO.read(new File("TetrisImages/L.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startUpdateThread() {
		
		Timer timer = new Timer(true);
		
		timer.scheduleAtFixedRate( new TimerTask() {

			@Override
			public void run() {
				iterateGameLoop(currentPiece, area.getArea());
				repaint();
			}
			
		}, 0, 30); 
	}
	
	public void runIteration() {
		iterateGameLoop(currentPiece, area.getArea());
		repaint();
	}
	
	boolean iterateGameLoop(AbstractPiece p, Color[][] areaArray){
		
		if(targetRotation != 0) {
			targetRotation--;
			p.rotate(Direction.CLOCKWISE);
		}
		
		if(p.centerPos.getX() - targetX > 0)
			p.centerPos = new Pair(p.centerPos.getX() - 1, p.centerPos.getY());
		else if(p.centerPos.getX() - targetX < 0)
			p.centerPos = new Pair(p.centerPos.getX() + 1, p.centerPos.getY());
		
		if(!handlePieceMove(p, areaArray)) {
			
			handleClear(areaArray);
			
			checkGameEnd();

			currentPiece = nextPieces[0];
			for(int i = 0; i < 2; i++)
				nextPieces[i] = nextPieces[i + 1];
			nextPieces[2] = generateNewPiece();
			
			decideOrientation();
			
			return false;
		}
		
		return true;
	}
	
	boolean handlePieceMove(AbstractPiece p, Color[][] areaArray) {
		movePiece(p);
		if(!canTravelFurther(p, areaArray)) {
			placePiece(areaArray);
			return false;
		}
		
		return true;
	}
	
	void placePiece(Color[][] areaArray) {
		Pair[] positions = currentPiece.getPositions();
		
		for(Pair p: positions) {
			if(p.getY() + 1 < area.getHeight())
				areaArray[(int)p.getY() + 1][(int)p.getX()] = currentPiece.c;
		}
	}
	
	void handleClear(Color[][] areaArray) {
		//clears lines
		ArrayList<Integer> lines = checkLinesCleared(area.getArea());
		
		if(lines.size() == 1)
			score += 1;
		else if(lines.size() == 2)
			score += 3;
		else if(lines.size() == 3)
			score += 5;
		else if(lines.size() == 4)
			score += 8;
		
		for(Integer i: lines) {
			for(int a = 0; a < area.getWidth(); a++) {
				for(int j = i; j < area.getHeight() - 1; j++) {       
					areaArray[j][a] = areaArray[j + 1][a];
				}
				
			}
		}
	}
	
	void decideOrientation() {
		ArrayList<Color[][]> goodOrientations = new ArrayList<Color[][]>();
		ArrayList<Integer> rotations = new ArrayList<Integer>();
		ArrayList<Integer> startX = new ArrayList<Integer>();
		int holdStartIndex = -1;

		for(int i = 0; i < area.getWidth(); i++) {
			currentPiece.centerPos = new Pair(i, area.getHeight() + 1);
			currentPiece.rotate(Direction.CLOCKWISE);
			if(!outOfBounds(currentPiece) && canTravelFurther(currentPiece, area.getArea())) {
				
				Color[][] areaClone = cloneAreaArray(area.getArea());
				skip(currentPiece, areaClone);
				goodOrientations.add(areaClone);
				rotations.add(1);
				startX.add(i);
			}
			
			currentPiece.centerPos = new Pair(i, area.getHeight() + 1);
			currentPiece.rotate(Direction.CLOCKWISE);
			if(!outOfBounds(currentPiece) && canTravelFurther(currentPiece, area.getArea())) {
				
				Color[][] areaClone = cloneAreaArray(area.getArea());
				skip(currentPiece, areaClone);
				goodOrientations.add(areaClone);
				rotations.add(2);
				startX.add(i);
			}
			
			currentPiece.centerPos = new Pair(i, area.getHeight() + 1);
			currentPiece.rotate(Direction.CLOCKWISE);
			if(!outOfBounds(currentPiece) && canTravelFurther(currentPiece, area.getArea())) {
				
				Color[][] areaClone = cloneAreaArray(area.getArea());
				skip(currentPiece, areaClone);
				goodOrientations.add(areaClone);
				rotations.add(3);
				startX.add(i);
			}
			
			currentPiece.centerPos = new Pair(i, area.getHeight() + 1);
			currentPiece.rotate(Direction.CLOCKWISE);
			if(!outOfBounds(currentPiece) && canTravelFurther(currentPiece, area.getArea())) {
				Color[][] areaClone = cloneAreaArray(area.getArea());
				skip(currentPiece, areaClone);
				goodOrientations.add(areaClone);
				rotations.add(0);
				startX.add(i);
			}
			
			currentPiece.centerPos = new Pair(i, area.getHeight() + 1);
		}
		
		holdStartIndex = goodOrientations.size() - 1;
		
		boolean nextMoved = false;
		
		AbstractPiece tempPiece = currentPiece;
		if(savedPiece != null)
			currentPiece = savedPiece;
		else {
			currentPiece = nextPieces[0];
			nextMoved = true;
		}
		savedPiece = tempPiece;
		savedPiece.centerPos = new Pair(area.getWidth()/2, area.getHeight() + 1);
		
		for(int i = 0; i < area.getWidth(); i++) {
			currentPiece.centerPos = new Pair(i, area.getHeight() + 1);
			currentPiece.rotate(Direction.CLOCKWISE);
			if(!outOfBounds(currentPiece) && canTravelFurther(currentPiece, area.getArea())) {
				Color[][] areaClone = cloneAreaArray(area.getArea());
				skip(currentPiece, areaClone);
				goodOrientations.add(areaClone);
				rotations.add(1);
				startX.add(i);
			}
			
			currentPiece.centerPos = new Pair(i, area.getHeight() + 1);
			currentPiece.rotate(Direction.CLOCKWISE);
			if(!outOfBounds(currentPiece) && canTravelFurther(currentPiece, area.getArea())) {
				
				Color[][] areaClone = cloneAreaArray(area.getArea());
				skip(currentPiece, areaClone);
				goodOrientations.add(areaClone);
				rotations.add(2);
				startX.add(i);
			}
			
			currentPiece.centerPos = new Pair(i, area.getHeight() + 1);
			currentPiece.rotate(Direction.CLOCKWISE);
			if(!outOfBounds(currentPiece) && canTravelFurther(currentPiece, area.getArea())) {
				Color[][] areaClone = cloneAreaArray(area.getArea());
				skip(currentPiece, areaClone);
				goodOrientations.add(areaClone);
				rotations.add(3);
				startX.add(i);
			}
			
			currentPiece.centerPos = new Pair(i, area.getHeight() + 1);
			currentPiece.rotate(Direction.CLOCKWISE);
			if(!outOfBounds(currentPiece) && canTravelFurther(currentPiece, area.getArea())) {
				Color[][] areaClone = cloneAreaArray(area.getArea());
				skip(currentPiece, areaClone);
				goodOrientations.add(areaClone);
				rotations.add(0);
				startX.add(i);
			}
			
			currentPiece.centerPos = new Pair(i, area.getHeight() + 1);
		}

		int index = 0;
		double maxScore = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < goodOrientations.size(); i++) {
			double score = determineAreaScore(goodOrientations.get(i));
			if(score > maxScore) {
				index = i;
				maxScore = score;
			}
		}
		
		if(index <= holdStartIndex) {		
			if(!nextMoved) {
				tempPiece = currentPiece;
				currentPiece = savedPiece;
				savedPiece = tempPiece;
			}else {
				nextPieces[0] = currentPiece;
				currentPiece = savedPiece;
				savedPiece = null;
			}
		}else {
			if(nextMoved) {
				for(int i = 0; i < 2; i++)
					nextPieces[i] = nextPieces[i + 1];
				nextPieces[2] = generateNewPiece();
			}
		}
		
		targetX = startX.get(index);
		targetRotation = rotations.get(index);
		
		currentPiece.centerPos = new Pair(area.getWidth()/2, area.getHeight() + 1);
	}
	
	Color[][] cloneAreaArray(Color[][] areaArray) {
		Color[][] ret = new Color[areaArray.length][];
		for(int i = 0; i < areaArray.length; i++)
			ret[i] = areaArray[i].clone();
		
		return ret;
	}
	
	void movePiece(AbstractPiece p) {
		Pair tempPos = p.centerPos;
		p.centerPos = new Pair(tempPos.getX(), tempPos.getY() - 1);
		p.calculateRotation();
	}
	
	void skip(AbstractPiece p, Color[][] areaArray) {
		while(handlePieceMove(p, areaArray));
	}
	
	boolean canTravelFurther(AbstractPiece piece, Color[][] areaArray) {
		Pair[] positions = piece.getPositions();
		
		for(Pair p: positions) {
			if(p.getY() <= -1 || (p.getY() < area.getHeight() && areaArray[(int)p.getY()][(int)p.getX()] != null))
				return false;
		}
		
		return true;
	}
	
	void checkGameEnd() {
		Color[][] areaArray = area.getArea();
		for(int i = 0; i < area.getWidth(); i++) {
			if(areaArray[area.getHeight() - 1][i] != null) {
				System.exit(0);
			}
		}
	}
	
	boolean outOfBounds(AbstractPiece piece) {
		Pair[] positions = piece.getPositions();
		
		for(Pair p: positions) {
			if(p.getY() < 0 || p.getX() < 0 || p.getX() >= area.getWidth())
				return true;
		}
		
		return false;
	}
	
	AbstractPiece generateNewPiece() {
		double rand = Math.random();
		
		AbstractPiece ret;
		
		if(rand < 0.142) {
			ret = new O(new Pair(area.getWidth()/2, area.getHeight() + 1));
		}else if(rand < 0.285) {
			ret = new I(new Pair(area.getWidth()/2, area.getHeight() + 1));
		}else if(rand < 0.428) {
			ret = new J(new Pair(area.getWidth()/2, area.getHeight() + 1));
		}else if(rand < 0.571) {
			ret = new L(new Pair(area.getWidth()/2, area.getHeight() + 1));
		}else if(rand < 0.714) {
			ret = new S(new Pair(area.getWidth()/2, area.getHeight() + 1));
		}else if(rand < 0.857) {
			ret = new T(new Pair(area.getWidth()/2, area.getHeight() + 1));
		}else{
			ret = new Z(new Pair(area.getWidth()/2, area.getHeight() + 1));
		}
		
		return ret;
	}
	
	double determineAreaScore(Color[][] areaArray) {
		
		int[] heights = getHeightArray(areaArray);
		//aggregate height
		int aggHeight = 0;
		for(int i = 0; i < heights.length; i++)
			aggHeight += heights[i];
		
		
		
		//complete lines
		int linesCleared = checkLinesCleared(areaArray).size();
		
		//holes
		int holeCount = 0;
		for(int i = 0; i < heights.length; i++) {
			for(int j = 0; j < heights[i] - 1; j++) {
				if(areaArray[j][i] == null)
					holeCount++;
			}
		}
		
		//bumpiness
		int bump = 0;
		for(int i = 0; i < heights.length - 1; i++) 
			bump += Math.abs(heights[i] - heights[i + 1]);
		
		return genes[0] * aggHeight + genes[1] * linesCleared + genes[2] * holeCount + genes[3] * bump;
	}
	
	int[] getHeightArray(Color[][] areaArray) {
		int[] heights = new int[areaArray[0].length];
		for(int i = 0; i < areaArray[0].length; i++) {
			for(int j = 0; j < areaArray.length; j++) {
				if(areaArray[j][i] != null)
					heights[i] = j + 1;
			}
		}
		
		return heights;
	}
	
	public ArrayList<Integer> checkLinesCleared(Color[][] areaArray) {
		ArrayList<Integer> lines = new ArrayList<Integer>();
		
		for(int i = area.getHeight() - 1; i >= 0; i--) {
			boolean noSpaces = true;
			for(int j = 0; j < area.getWidth(); j++) {
				if(areaArray[i][j] == null) {
					noSpaces = false;
					break;
				}
			}
			
			if(noSpaces)
				lines.add(i);
		}
		
		return lines;
	}
	
	public boolean hasEnded() {
		return ended;
	}
	
	public double getScore() {
		return score;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);    
		
		Graphics2D g2 = (Graphics2D) g;
		
		double block_width = (double)width/area.getWidth();
		double block_height = (double)height/area.getHeight();

		Color[][] areaArray = area.getArea();
		Pair[] positions = currentPiece.getPositions();
		
		//draw grid lines
		g.setColor(Color.GRAY);
		g2.setStroke(new BasicStroke(1));
		for(int i = 0; i <= area.getWidth(); i++) {
			g.drawLine((int)(width * 0.5) + (int)(i * block_width), 0, (int)(width * 0.5) + (int)(i * block_width), height);
		}
		
		for(int i = 0; i <= area.getHeight(); i++) {
			g.drawLine((int)(width * 0.5), (int)(i * block_height), (int)(width * 0.5) + width, (int)(i * block_height));
		}
		
		//draw pieces on ground
		for(int i = 0; i < areaArray.length; i++) {
			for(int j = 0; j < areaArray[0].length; j++) {
				if(areaArray[i][j] != null) {
					g.setColor(areaArray[i][j]);
					g.fillRect((int)(width * 0.5) + (int)(j * block_width), (int)((area.getHeight() - i - 1) * block_height), (int)block_width, (int)block_height);
				}
			}
		}

		//draw falling piece
		for(Pair p: positions) {
			g.setColor(currentPiece.c);
			g.fillRect((int)(width * 0.5) + (int)(p.getX() * block_width), (int)((area.getHeight() - (int)p.getY() - 1) * block_height), (int)block_width, (int)block_height);
		}
		
		//draw next
		g.setColor(Color.WHITE);
		g.setFont(new Font("SomeFont", Font.PLAIN, 50));
		g.drawString("Next", (int)(width * 1.5) + 130, 190);
		g2.setStroke(new BasicStroke(10));
		g.setColor(Color.RED);
		g.drawRect((int)(width * 1.6), (int)(height * 0.15), (int)(width * 0.3), (int)(height * 0.7));
		BufferedImage img1 = getImage(nextPieces[0]);
		g2.drawImage(img1, (int)(width * 1.75) - img1.getWidth()/2, (int)(height * 0.2), null);
		BufferedImage img2 = getImage(nextPieces[1]);
		g2.drawImage(img2, (int)(width * 1.75) - img2.getWidth()/2, (int)(height * 0.45), null);
		BufferedImage img3 = getImage(nextPieces[2]);
		g2.drawImage(img3, (int)(width * 1.75) - img3.getWidth()/2, (int)(height * 0.7), null);
		
		//draw hold
		g.setColor(Color.WHITE);
		g.drawString("Hold", 120, 120);
		g.setColor(Color.GREEN);
		g.drawRect((int)(width * 0.1), (int)(height * 0.1), 200, 200);
		BufferedImage img = getImage(savedPiece);
		if(savedPiece != null)
			g2.drawImage(img, 170 - img.getWidth()/2, 240 - img.getHeight()/2, null);
		
		g.setColor(Color.WHITE);
		g.drawString("Score: " + score, (int)(width * 0.5) + 20, 70);
	}
	
	BufferedImage getImage(AbstractPiece p) {
		if(p instanceof T)
			return T_Image;
		else if(p instanceof O)
			return O_Image;
		else if(p instanceof L)
			return L_Image;
		else if(p instanceof S)
			return S_Image;
		else if(p instanceof I)
			return I_Image;
		else if(p instanceof Z)
			return Z_Image;
		else
			return J_Image;
	}
}

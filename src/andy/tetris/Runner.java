package andy.tetris;
import java.awt.Color;
import java.awt.Insets;

import javax.swing.JFrame;

public class Runner {
	
	JFrame frame = new JFrame();
	int width = 700;
	int height = 1400;
	
	public static void main(String args[]) {
		new Runner();
	}
	
	Runner(){
		frame.setBackground(Color.BLACK); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		
		frame.add(new Game(width, height));	
		
		frame.validate();		
		frame.setVisible(true);
		frame.repaint();
		
		frame.setTitle("Tetris");
		Insets insets = frame.getInsets();
		frame.setSize(width * 2 + insets.left + insets.right, height + insets.top + insets.bottom); 
		frame.setLocationRelativeTo(null); 
	}
}

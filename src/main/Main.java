package main;

import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		int boardWidth = 360;
		int boardHeight=640;
		
		JFrame frame = new JFrame("Fly bird");
		frame.setSize(boardWidth,boardHeight);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		FlappyBird flappyBird = new FlappyBird();
		frame.add(flappyBird);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	
}

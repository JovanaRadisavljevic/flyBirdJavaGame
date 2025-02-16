package main;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;


public class FlappyBird extends JPanel implements ActionListener {
	private int boardWidth=360;
	private int boardHeight = 640;
	
	private BufferedImage backgroundImg;
	private BufferedImage birdImg;
	private BufferedImage topPipeImg;
	private BufferedImage bottomPipeImg;
	//bird
	int birdX=boardWidth/8;
	int birdY=boardHeight/2;
	int birdWidth=34;
	int birdHeigth=24;
	
	class Bird{
		int x=birdX;
		int y = birdY;
		int width = birdWidth;
		int height = birdHeigth;
		BufferedImage img;
		
		Bird(BufferedImage img){
			this.img=img;
		}
	}
	//game logic
	Bird bird;
	int brzinaY=-6;
	int gravitacija=1;
	Timer gameLoop;
	
	public FlappyBird() {
		setPreferredSize(new Dimension(boardWidth, boardHeight));
		loadImgs();
		
		//bird
		bird = new Bird(birdImg);
		//GAME timer
		//1000/6 60 puta u jednoj sekundi kao varijabla frame per second
		gameLoop=new Timer(1000/6, this);
		gameLoop.start();
	}
	private void loadImgs() {
		//load img
		String bird="flappybird.png";
		String background="flappybirdbg.png";
		String tpipe = "toppipe.png";
		String bpipe= "bottompipe.png";
		
		backgroundImg = loadImage(background);
		birdImg = loadImage(bird);
		topPipeImg=loadImage(tpipe);
		bottomPipeImg=loadImage(bpipe);
		
	}
	private BufferedImage loadImage(String background) {
		File imageFile = new File("src/res/"+background);
		if (imageFile.exists()) {
		    try {
				return ImageIO.read(imageFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
		    System.out.println("Slika nije pronađena!");
		}
		return null;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	public void draw(Graphics g) {
		g.drawImage(backgroundImg, 0, 0, null);
		g.drawImage(birdImg, bird.x, bird.y, bird.width,bird.height,null);
		System.out.println("draw");
		System.out.println("bird.x: " + bird.x + ", bird.y: " + bird.y);
	}
	public void move() {
		//bird
		System.out.println("move");
		 System.out.println("brzinaY: " + brzinaY);  // Dodajemo ispis za brzinu
		    bird.y += brzinaY;

		    // Dodaj gravitaciju da ptica pada sve brže
		    brzinaY += gravitacija;

		    bird.y = Math.max(bird.y, 0);

		    bird.y = Math.min(bird.y, boardHeight - bird.height);
		/*bird.y+= brzinaY;
		bird.y=Math.max(birdY, 0);//jer se meni trenutna vrednost npr 140 smanjuje svaki put -6
		*/
		//kad dodje do 0 i smanji ga na -6 ja cu da uzmem 0 i onda ce ptica da ostane u frejmu 
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		//60 puta u sekundi se pozivaju navedene metode
		move();
		repaint(); //ova metoda poziva paint komponentu koja poziva draw
	}
}

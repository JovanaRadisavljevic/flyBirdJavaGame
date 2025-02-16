package main;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;


public class FlappyBird extends JPanel implements ActionListener, KeyListener {
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
	//Pipes
	int pipeX=boardWidth;
	int pipeY=0;//da pocne od samog vrha ekrana
	int pipeWidth = 64;
	int pipeHeight = 512;
	class Pipe{
		int x=pipeX;
		int y = pipeY;
		int width = pipeWidth;
		int height = pipeHeight;
		BufferedImage img;
		boolean passed=false;
		
		Pipe(BufferedImage img) {
			this.img=img;
		}
	}
	
	//GAME LOGIC
	Bird bird;
	int brzinaY=0;
	int gravitacija=1;
	//pipe
	int brzinaX=-4; //pomeram cev ka levo
	ArrayList<Pipe> pipes;
	Random random =new Random();
	//loops
	Timer gameLoop;
	Timer placePipesTimer;
	//collisions
	boolean gameOver = false;
	double score = 0;
	
	public FlappyBird() {
		setPreferredSize(new Dimension(boardWidth, boardHeight));
		setFocusable(true);
		addKeyListener(this);
		loadImgs();
		
		//bird
		bird = new Bird(birdImg);
		//pipes
		pipes=new ArrayList<Pipe>();
		placePipesTimer = new Timer(1500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				placePipes();
			}
		});
		placePipesTimer.start();
		
		//GAME timer
		//1000/60 60 puta u jednoj sekundi kao varijabla frame per second
		gameLoop=new Timer(1000/60, this);
		gameLoop.start();
	}
	public void placePipes() {
		//math.random - daje vrednost izmedju 0 i 1 * 512/2 =  raspon od 0 - 256
		//512/4=128
		//0 pipeY
		//0-128-(0 do 256) --> raspon od pipeheight/4 do 3/4 pipeheight
		int randomPipeY= (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
		int rupaOpenSpace = boardHeight/4;
		
		Pipe topPipe = new Pipe(topPipeImg);
		topPipe.y=randomPipeY;
		pipes.add(topPipe);
		
		Pipe bottomPipe = new Pipe(bottomPipeImg);
		bottomPipe.y=topPipe.y + pipeHeight + rupaOpenSpace;
		pipes.add(bottomPipe);
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
		//PIPES
		 for (int i = 0; i < pipes.size(); i++) {
	        Pipe pipe = pipes.get(i);
	        g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
	    }
		 //SCORE
		
		 
		 if(gameOver) {
			 g.setFont(new Font("Arial", Font.PLAIN, 17));
			// pozicija i dimenzije pravougaonika
			 int rectX = 30;
			 int rectY = 300;
			 int rectWidth = 300;
			 int rectHeight = 80;

			 //sivi pravougaonik 
			 g.setColor(new Color(0, 0, 0, 150)); // RGB (0,0,0) - crna,  150 - providnost
			 g.fillRect(rectX, rectY, rectWidth, rectHeight);

			 g.setColor(Color.WHITE);
			 g.drawString("Izgubili ste. Vaš skor je: " + (int)score, rectX + 10, rectY + 20);
			 g.drawString("Ako želite ponovo da otpočnete igru", rectX + 10, rectY + 40);
			 g.drawString("pritisnite dugme: SPACE", rectX + 10, rectY + 60);

		 }else {
			 g.setColor(Color.white);
			 g.setFont(new Font("Arial", Font.BOLD, 30));
			 g.drawString(String.valueOf((int)score), 10,35);
		 }
	}
	public void update() {
		//bird
	    bird.y += brzinaY;
		brzinaY += gravitacija;
		bird.y = Math.max(bird.y, 0);//kad dodje do 0 i smanji ga na -6 ja cu da uzmem 0 i onda ce ptica da ostane u frejmu na vrhu
		
		//pipes
		for (int i = 0; i < pipes.size(); i++) {
			Pipe pipe = pipes.get(i);
			pipe.x+=brzinaX;
			if(!pipe.passed && bird.x>pipe.x+pipe.width) {
				//jos uvek nisam presla cev i moja x koordinata
				//krece tacno tamo gde se zavrsava cev ,znaci u tom trenutku sam je presla
				pipe.passed=true;
				score+=0.5;
				//0.5 jer imamo dve cev gornju i donju koju treba da prodjem
				//jer kad prodjem kroz tu petlju meni odgovaraju tacno dve cevi koje u zbiru daju 1
			}
			
			if(collision(bird, pipe)) {
				gameOver=true;
			}
		}
		if(bird.y>boardHeight) {
			gameOver=true;
		}
	}
	public boolean collision(Bird b, Pipe p) {
		return b.x < p.x+p.width &&  //gornji levi ugao ne prelazi gornji desni ugao (kraj cevi)
				b.x +b.width > p.x && //gornji desni ugao prelazi x koordinatu od cevi
				b.y<p.y + p.height && // gornji levi ugao ptice ne dostize gornji deo cevi
				b.y + b.height > p.y; //donji levi ugao ptice ne udara nalazi se iznad donje cevi
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		//60 puta u sekundi se pozivaju navedene metode
		update();
		repaint(); //ova metoda poziva paint komponentu koja poziva draw
		if(gameOver) {
			placePipesTimer.stop();
			gameLoop.stop();
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_SPACE) {
			brzinaY=-9;
			if(gameOver) {
				//restart game
				resetGame();
			}
		}
	}
	private void resetGame() {
		bird.y=birdY;
		brzinaY=0;
		pipes.clear();
		score=0;
		gameOver=false;
		placePipesTimer.start();
		gameLoop.start();
	}
	@Override
	public void keyReleased(KeyEvent e) {}
}

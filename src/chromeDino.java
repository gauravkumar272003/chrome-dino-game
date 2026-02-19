import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class chromeDino extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 750;
    int boardHeight = 250;

    Image dinosaurImg;
    Image dinosaurDead;
    Image dinosaurJump;

    Image cactus1;
    Image cactus2;
    Image cactus3;

    Image bigcactus1;
    Image bigcactus2;
    Image bigcactus3;

    Image birdImg;

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;
        public Image img;

        Block(int x, int y, int width, int height, Image image) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.image = image;
        }
    }

    // dino
    int dinoWidth = 88;
    int dinoHeight = 94;
    int dinoX = 50;
    int dinoY = boardHeight - dinoHeight;

    Block dinosaur;

    // cactus
    int cactus1Width = 34;
    int cactus2Width = 69;
    int cactus3Width = 102;

    int cactusHeight = 70;
    int cactusX = 700;
    int cactusY = boardHeight - cactusHeight;

    int bigcactus1Width = 40;
    int bigcactus2Width = 84;
    int bigcactus3Width = 111;

    int birdWidth = 80;
    int birdHeight = 64;
    int birdX = 700;
    int birdY = boardHeight - birdHeight - 30;

    ArrayList<Block> cactusArray;

    // physics of the game and gravity
    int velocityY = 0;
    int velocityX = -11;
    int gravity = 1;

    boolean gameOver = false;
    int score = 0;

    Timer gameLoop;
    Timer cactusGenerate;

    public chromeDino() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.white);
        setFocusable(true);
        addKeyListener(this);

        dinosaurImg = new ImageIcon("game_image/dino-run.gif").getImage();
        dinosaurDead = new ImageIcon("game_image/dino-dead.png").getImage();
        dinosaurJump = new ImageIcon("game_image/dino-jump.png").getImage();

        cactus1 = new ImageIcon("game_image/cactus1.png").getImage();
        cactus2 = new ImageIcon("game_image/cactus2.png").getImage();
        cactus3 = new ImageIcon("game_image/cactus3.png").getImage();

        bigcactus1 = new ImageIcon("game_image/big-cactus1.png").getImage();
        bigcactus2 = new ImageIcon("game_image/big-cactus2.png").getImage();
        bigcactus3 = new ImageIcon("game_image/big-cactus3.png").getImage();
        birdImg = new ImageIcon("game_image/bird.gif").getImage();

        dinosaur = new Block(dinoX, dinoY, dinoWidth, dinoHeight, dinosaurImg);
        cactusArray = new ArrayList<Block>();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        cactusGenerate = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeCactus();
            }
        });

        cactusGenerate.start();
    }

    void placeCactus() {

        if (gameOver) {
            return;
        }

        double placeCactusRandom = Math.random();
        if (placeCactusRandom > 0.90) {
            Block cactus = new Block(cactusX, cactusY, cactus3Width, cactusHeight, cactus3);
            cactusArray.add(cactus);
        } else if (placeCactusRandom > 0.80) {
            Block cactus = new Block(cactusX, cactusY, cactus2Width, cactusHeight, cactus2);
            cactusArray.add(cactus);
        } else if (placeCactusRandom > 0.70) {
            Block cactus = new Block(cactusX, cactusY, cactus1Width, cactusHeight, cactus1);
            cactusArray.add(cactus);
        } else if (placeCactusRandom > 0.60) {
            Block cactus = new Block(birdX, birdY, birdWidth, birdHeight, birdImg);
            cactusArray.add(cactus);
        } else if (placeCactusRandom > 0.50) {
            Block cactus = new Block(cactusX, cactusY, bigcactus1Width, cactusHeight, bigcactus1);
            cactusArray.add(cactus);
        } else if (placeCactusRandom > 0.30) {
            Block cactus = new Block(cactusX, cactusY, bigcactus2Width, cactusHeight, bigcactus2);
            cactusArray.add(cactus);
        } else if (placeCactusRandom > 0.10) {
            Block cactus = new Block(cactusX, cactusY, bigcactus3Width, cactusHeight, bigcactus3);
            cactusArray.add(cactus);
        }

        double placeBirdsRandome = Math.random();

        if (cactusArray.size() > 5) {
            cactusArray.remove(0);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(dinosaur.image, dinosaur.x, dinosaur.y, dinosaur.width, dinosaur.height, null);

        for (int i = 0; i < cactusArray.size(); i++) {
            Block cactus = cactusArray.get(i);
            g.drawImage(cactus.image, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }

        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        if (gameOver) {
            g.drawString("Game Over : )", 310, 35);
            g.drawString("You Scored " + String.valueOf(score), 310, 65);
        } else {
            g.drawString("Score: " + String.valueOf(score), 10, 35);
        }

    }

    public void move() {

        velocityY += gravity;
        dinosaur.y += velocityY;

        if (dinosaur.y >= dinoY) { // stop the dinosaur from falling past the ground
            dinosaur.y = dinoY;
            velocityY = 0;
            dinosaur.image = dinosaurImg;
        }

        for (int i = 0; i < cactusArray.size(); i++) {
            Block cactus = cactusArray.get(i);
            cactus.x += velocityX;

            if (collision(cactus, dinosaur)) {
                gameOver = true;
                dinosaur.image = dinosaurDead;
            }
        }
        score++;
    }

    boolean collision(Block a, Block b) { // AABB collision
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver == true) {
            cactusGenerate.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // jump
            if (dinosaur.y == dinoY) {
                velocityY = -17;
                dinosaur.image = dinosaurJump;
                // dinosaur.image = dinosaurImg;
            }

            if (gameOver) {
                dinosaur.y = dinoY;
                dinosaur.image = dinosaurImg;
                velocityY = 0;

                cactusArray.clear();

                score = 0;
                gameOver = false;
                gameLoop.start();
                cactusGenerate.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
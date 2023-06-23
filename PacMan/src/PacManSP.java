import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;

public class PacManSP extends JFrame implements KeyListener {

    // attributes for the game
    private Rectangle pacman;
    private int score;
    private int lives;

    private List<Rectangle> dots;
    private List<Rectangle> ghosts;
    private List<Rectangle> walls;
    private Map<Rectangle, Color> dotMap;

    /**
     * PacMan single player simple game with seven ghosts randomly moving.
     * Goal is to collect 1000 points: each dot is worth 10 points.
     * At the end of the game, you will get a message with lives left and score that you got.
     *
     * @author Josip Ivanović
     * @version 23.06.2023
     */

    // game constructor
    public PacManSP() {
        setTitle("Pac - Man SinglePlayer");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // creating new panel (GUI)
        GamePanel gp = new GamePanel();
        add(gp);

        // generating the attributes and setting them
        pacman = new Rectangle(50, 50, 20, 20);
        dots = new ArrayList<>();
        ghosts = new ArrayList<>();
        walls = new ArrayList<>();
        score = 0;
        lives = 3;
        dotMap = new HashMap<>();

        // listener for movements
        addKeyListener(this);
        setFocusable(true);

        // spawns all of game decorations and points
        spawnDots();
        spawnGhosts();
        spawnWalls();

        // used to control the speed and movement ghosts
        Timer ghostControl = new Timer(110, ke -> ghostMovement());
        ghostControl.start();

        // spawns instructions before the game starts
        doInstructions();
        setVisible(true);

    }

    // separate class for the game panel
    private class GamePanel extends JPanel {

        protected void paintComponent(Graphics gr) {

            // setting up the screen
            super.paintComponent(gr);

            Image img = createImage(getWidth(), getHeight());
            Graphics graph = img.getGraphics();

            // drawing pacman, with mouth
            graph.setColor(Color.YELLOW);
            graph.fillArc(pacman.x, pacman.y, pacman.width, pacman.height, 45, 270);

            // drawing the score and lives label
            graph.setColor(Color.BLACK);
            graph.drawString("Score: " + score, 20, 20);
            graph.drawString("Lives: " + lives, 20, 40);

            // drawing the dots
            for (Rectangle dot : dots) {
                if (!dotMap.containsKey(dot)) {
                    graph.setColor(Color.GRAY);
                }
                graph.fillRect(dot.x, dot.y, dot.width, dot.height);
            }
            // drawing ghosts
            graph.setColor(Color.BLUE);
            for (Rectangle ghost : ghosts) {
                graph.fillRect(ghost.x, ghost.y, ghost.width, ghost.height);
            }
            // drawing walls
            graph.setColor(Color.BLACK);
            for (Rectangle wall : walls) {
                graph.fillRect(wall.x, wall.y, wall.width, wall.height);
            }

            // drawing the image which contains ghosts, pacman, walls and dots
            gr.drawImage(img, 0, 0, null);

        }

    }

    // method for spawning the dots for points
    private void spawnDots() {
        dots.clear();
        dotMap.clear();

        int dotSize = 5;
        int dotSpacing = 35;

        // two "for" loops for grid-like dots look (used spacing for setting them on the
        // same distance)
        for (int i = 10; i < getWidth(); i += dotSpacing) {

            for (int j = 10; j < getHeight(); j += dotSpacing) {

                Rectangle dot = new Rectangle(i, j, dotSize, dotSize);
                dots.add(dot);

                Color dotColor = Color.GRAY;

                dotMap.put(dot, dotColor);

            }

        }

    }

    // method for spawning ghosts
    private void spawnGhosts() {

        int ghostSize = 25;
        int ghostSpacing = 140;

        ghosts.clear();

        // separately created every ghost on screen
        Rectangle ghost = new Rectangle(ghostSpacing, ghostSpacing, ghostSize, ghostSize);
        Rectangle ghost1 = new Rectangle(getWidth() - ghostSpacing - ghostSize, ghostSpacing, ghostSize, ghostSize);
        Rectangle ghost2 = new Rectangle(ghostSpacing, getHeight() - ghostSpacing - ghostSize, ghostSize, ghostSize);
        Rectangle ghost3 = new Rectangle(getWidth() - ghostSpacing - ghostSize, getHeight() - ghostSpacing - ghostSize,
                ghostSize, ghostSize);
        Rectangle ghost4 = new Rectangle(getWidth() / 2 - ghostSize / 2, getHeight() / 2 - ghostSize / 2,
                ghostSize, ghostSize);

        Rectangle ghost5 = new Rectangle(ghostSpacing, ghostSpacing + ghostSpacing, ghostSize, ghostSize);
        Rectangle ghost6 = new Rectangle(getWidth() - ghostSpacing - ghostSize, ghostSpacing + ghostSpacing, ghostSize,
                ghostSize);

        ghosts.add(ghost);
        ghosts.add(ghost1);
        ghosts.add(ghost2);
        ghosts.add(ghost3);
        ghosts.add(ghost4);
        ghosts.add(ghost5);
        ghosts.add(ghost6);

    }

    // method for spawning walls in the game
    private void spawnWalls() {

        walls.clear();

        int wallThick = 10;

        // separate wall generating
        Rectangle outWall1 = new Rectangle(0, 0, getWidth(), wallThick);
        Rectangle outWall2 = new Rectangle(0, getHeight() - wallThick, getWidth(), wallThick);
        Rectangle outWall3 = new Rectangle(0, 0, wallThick, getHeight());
        Rectangle outWall4 = new Rectangle(getWidth() - wallThick, 0, wallThick, getHeight());

        Rectangle inWall = new Rectangle(150, 50, wallThick, 150);
        Rectangle inWall1 = new Rectangle(400, 50, wallThick, 150);
        Rectangle inWall2 = new Rectangle(150, getHeight() - 200, wallThick, 150);
        Rectangle inWall3 = new Rectangle(400, getHeight() - 200, wallThick, 150);

        walls.add(outWall1);
        walls.add(outWall2);
        walls.add(outWall3);
        walls.add(outWall4);
        walls.add(inWall);
        walls.add(inWall1);
        walls.add(inWall2);
        walls.add(inWall3);

    }

    // method for ghost movement
    private void ghostMovement() {

        Random rnd = new Random();

        // "for" loop with switch for each direction
        for (Rectangle ghost : ghosts) {
            int direction = rnd.nextInt(4);

            switch (direction) {
                case 0:
                    ghost.setLocation(ghost.x, ghost.y - 10);
                    break;
                case 1:
                    ghost.setLocation(ghost.x, ghost.y + 10);
                    break;
                case 2:
                    ghost.setLocation(ghost.x - 10, ghost.y);
                    break;
                case 3:
                    ghost.setLocation(ghost.x + 10, ghost.y);
                    break;

            }

        }
        repaint();

    }

    // method for checking if the wall is touched by pacman
    private boolean collisionCheck(int x, int y) {

        Rectangle nextPos = new Rectangle(x, y, pacman.width, pacman.height);

        for (Rectangle wall : walls) {
            if (wall.intersects(nextPos)) {
                return true;
            }

        }
        return false;

    }

    /*
     * method for dots collision checking for pacman if he touches the dots, then
     * points are added (10 points for each)
     * The dot also removes when touched
     * and an if statement for the game over and the results printed
     */
    private void pointCollision() {

        Rectangle pacmanBnds = new Rectangle(pacman.x, pacman.y, pacman.width, pacman.height);

        List<Rectangle> collectedDots = new ArrayList<>();

        for (Rectangle dot : dots) {

            if (dot.intersects(pacmanBnds)) {
                collectedDots.add(dot);

                if (dotMap.containsKey(dot)) {
                    score += 10;
                }
            }
        }
        dots.removeAll(collectedDots);

        if (score >= 1000 || dots.isEmpty()) {
            gameOver();
        }
    }

    // method for ghost collision with pacman, and if statement for lives lost and
    // game over
    private void ghostCollision() {

        Rectangle pacmanBnds = new Rectangle(pacman.x, pacman.y, pacman.width, pacman.height);

        for (Rectangle ghost : ghosts) {
            if (ghost.intersects(pacmanBnds)) {

                lives--;

                if (lives == 0 || score >= 1000) {
                    gameOver();
                } else {
                    gameReset();
                }
                score = 0;
                break;
            }

        }

    }

    // method for reseting the game to the beginning, with lives unchanged
    private void gameReset() {
        pacman.setLocation(60, 60);
        spawnDots();
        spawnGhosts();
    }

    // method for the game over
    private void gameOver() {
        String message = "Game over!\nScore: " + score + "\nLives: " + lives;

        JOptionPane.showMessageDialog(this, message, "Game is over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);

    }

    // instructions printed out at the beginning
    private void doInstructions() {

        String message = "Welcome to PacMan!\n\n"
                + "Instruction:\n"
                + "Use arrows to move.\n"
                + "Collect points to complete the game.\n"
                + "Keep safe from the ghosts, because they will take your lives!\n";

        JOptionPane.showMessageDialog(this, message, "Instructions", JOptionPane.INFORMATION_MESSAGE);
    }

    // methods for keyEvent
    public void keyTyped(KeyEvent ke) {
    }

    // method implemented with collision checking
    public void keyPressed(KeyEvent ke) {

        int key = ke.getKeyCode();

        switch (key) {
            case KeyEvent.VK_UP:
                if (!collisionCheck(pacman.x, pacman.y - 10)) {
                    pacman.setLocation(pacman.x, pacman.y - 10);
                }
                break;
            case KeyEvent.VK_DOWN:
                if (!collisionCheck(pacman.x, pacman.y + 10)) {
                    pacman.setLocation(pacman.x, pacman.y + 10);
                }
                break;
            case KeyEvent.VK_LEFT:
                if (!collisionCheck(pacman.x - 10, pacman.y)) {
                    pacman.setLocation(pacman.x - 10, pacman.y);
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (!collisionCheck(pacman.x + 10, pacman.y)) {
                    pacman.setLocation(pacman.x + 10, pacman.y);
                }
                break;
        }
        pointCollision();
        ghostCollision();
        repaint();

    }

    public void keyReleased(KeyEvent ke) {

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PacManSP::new);
    }

}

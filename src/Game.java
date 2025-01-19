import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Game extends JFrame{
    private final Board board;
    private Thread gameThread;
    private boolean running = false;
    private BufferedImage staticImage;

    public static final int ROWS = 30;
    public static final int COLUMNS = 30;
    public static final int WAIT_TIME = 200;
    public static final int BORDER_SIZE = 100;
    public static final int MAX_WIDTH = 1500;
    public static final int MAX_HEIGHT = 1300;
    public static final int SPACING = 20;
    public static final int TEXT_SIZE = 50;
    public static final int FONT_SIZE = 20;
    public static final String TITLE = "Game of Life";
    public static final ArrayList<String> RULES = new ArrayList<>() {{
        add("Click to toggle cells");
        add("Press ENTER to start/stop the game");
        add("Press C to clear the grid");
        add("Press R to randomize the grid");
        add("Have fun!");
    }};
    public static final int X_OFFSET = BORDER_SIZE / 2;
    public static final int Y_OFFSET = RULES.size() * SPACING + BORDER_SIZE / 2;

    public Game() {
        setTitle("Game of Life");
        int width = ROWS * Board.CELL_SIZE + BORDER_SIZE;
        int height = COLUMNS * Board.CELL_SIZE + RULES.size() * SPACING + BORDER_SIZE;

        if (width > MAX_WIDTH) {
            width = MAX_WIDTH;
        }

        if (height > MAX_HEIGHT) {
            height = MAX_HEIGHT;
        }

        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        createBufferStrategy(2);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e);
            }
        });

        prepareStatic();
        this.board = new Board(ROWS, COLUMNS);
    }

    private void handleClick(MouseEvent e) {
        if (running) {
            return;
        }
        int row = (e.getY() - Y_OFFSET) / Board.CELL_SIZE;
        int column = (e.getX() - X_OFFSET) / Board.CELL_SIZE;
        if (row >= 0 && row < ROWS && column >= 0 && column < COLUMNS) {
            this.board.getGrid()[row][column] = !this.board.getGrid()[row][column];
        }
        repaint();
    }

    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (running) {
                    stop();
                } else {
                    start();
                }
                break;
            case KeyEvent.VK_C:
                clear();
                repaint();
                break;
            case KeyEvent.VK_R:
                randomize();
                repaint();
                break;
        }
    }

    private void randomize() {
        stop();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                this.board.getGrid()[i][j] = Math.random() > 0.5;
            }
        }
        repaint();
    }

    private void clear() {
        stop();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                this.board.getGrid()[i][j] = false;
            }
        }
        repaint();
    }

    private void start() {
        running = true;
        gameThread = new Thread(() -> {
            while (true) {
                update();
                repaint();
                try {
                    Thread.sleep(WAIT_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        gameThread.start();
    }

    private void stop() {
        if (gameThread != null) {
            gameThread.interrupt();
        }
        running = false;
    }

    public void update() {
        this.board.updateGrid();
    }

    public void prepareStatic(){
        staticImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = staticImage.createGraphics();

        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
        g2d.drawString(TITLE, getWidth()/2 - TEXT_SIZE, BORDER_SIZE / 2);

        for (int i = 0; i < RULES.size(); i++) {
            g2d.drawString(RULES.get(i), SPACING, TEXT_SIZE + SPACING * i);
        }

        g2d.dispose();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        BufferStrategy bufferStrategy = getBufferStrategy();
        if (bufferStrategy != null) {
            Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
            g2d.drawImage(staticImage, 0, 0, null);
            this.board.drawGrid(g2d, X_OFFSET, Y_OFFSET);
            g2d.dispose();
            bufferStrategy.show();
        }
    }

}

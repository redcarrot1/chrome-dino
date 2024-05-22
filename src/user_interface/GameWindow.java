package user_interface;

import javax.swing.*;

public class GameWindow extends JFrame {

    public static final int SCREEN_WIDTH = (int) (1200 * 1.6);
    public static final int SCREEN_HEIGHT = (int) (300 * 1.6);

    private final GameScreen gameScreen;

    public GameWindow() {
        super("Dino");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);

        this.gameScreen = new GameScreen();
        add(gameScreen);
    }

    private void startGame() {
        gameScreen.startThread();
    }

    public static void main(String[] args) {
        GameWindow gameWindow = new GameWindow();
        gameWindow.startGame();
        gameWindow.setVisible(true);
    }

}

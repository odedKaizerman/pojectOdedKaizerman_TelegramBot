import javax.swing.*;

public class Window extends JFrame {
    public Window(int WINDOW_WIDTH, int WINDOW_HEIGHT) {
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(null);
        MainMenu mainMenu = new MainMenu(WINDOW_WIDTH, WINDOW_HEIGHT, this);
        this.add(mainMenu);
        mainMenu.setVisible(true);
        this.setVisible(true);

    }
}

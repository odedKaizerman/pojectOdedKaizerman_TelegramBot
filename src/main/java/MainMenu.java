import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainMenu extends JPanel {
    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;
    private Window window;
    private MyBot myBot;
    private ImageIcon image;
    private ImageIcon imageForButtonNewSurvey; // תמונה לכפתור
    private JButton buttonNewSurvey; // כפתור סקר חדש
    private SystemSurvey systemSurvey;

    private JLabel imageLabelForNumberUsers; // מספר משתמשים טקסט


    public MainMenu(int WINDOW_WIDTH, int WINDOW_HEIGHT, Window window) {
        this.systemSurvey = new SystemSurvey(this);
        this.window = window;
        creatBot(); // יצירת בוט
        this.WINDOW_HEIGHT = WINDOW_HEIGHT;
        this.WINDOW_WIDTH = WINDOW_WIDTH;
        this.setBounds(0, 0, this.WINDOW_WIDTH, this.WINDOW_HEIGHT);
        setLayout(null);
        creatBackgroundImage(); // יצירת תמונת רקע
        createButtons(); // יצירת כפתורים
        this.buttonNewSurvey.setVisible(true); // מפעיל כפתור יצירת סקר
        getNumberOfUsers();
    }





    public void creatBot() {
        this.myBot = new MyBot();
        this.myBot.setMainMenu(this);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);


            botsApi.registerBot(this.myBot);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("Error removing old webhook")) {
            } else {
                e.printStackTrace();
            }
        }
    }

    public void creatBackgroundImage() {
        URL imageUrl = getClass().getResource("/MainMenu.png");
        if (imageUrl == null) {
        } else {
            this.image = new ImageIcon(imageUrl);
        }
    }


    public void getNumberOfUsers() {
        this.imageLabelForNumberUsers = new JLabel("מספר משתמשים: " + this.myBot.getUsersChatIds().size());
        this.imageLabelForNumberUsers.setBounds(this.WINDOW_WIDTH - 220, -5, 200, 50); // מיקום וגודל
        this.imageLabelForNumberUsers.setHorizontalAlignment(SwingConstants.CENTER);
        this.imageLabelForNumberUsers.setVerticalAlignment(SwingConstants.CENTER);
        this.imageLabelForNumberUsers.setFont(new Font("Arial", Font.BOLD, 24));
        this.imageLabelForNumberUsers.setForeground(Color.WHITE); // צבע טקסט
        this.setLayout(null);
        this.add(this.imageLabelForNumberUsers);
    }
    public void BiggerCountUsers() {
        this.imageLabelForNumberUsers.setText("מספר משתמשים: " + this.myBot.getUsersChatIds().size());
        this.revalidate();
        this.repaint();
    }

    public void createButtons() {

        ImageIcon originalIcon1 = new ImageIcon(getClass().getResource("/ButtonNewSurveys.png")); // תמונה לכפתור:
        Image scaledImage1 = originalIcon1.getImage().getScaledInstance(360, 240, Image.SCALE_SMOOTH); // קובע גודל
        this.imageForButtonNewSurvey = new ImageIcon(scaledImage1);

        this.buttonNewSurvey = new JButton(this.imageForButtonNewSurvey);
        this.buttonNewSurvey.setBounds(485, 300, 240, 90);
        this.buttonNewSurvey.setOpaque(false);
        this.buttonNewSurvey.setContentAreaFilled(false);
        this.buttonNewSurvey.setBorderPainted(false);
        this.buttonNewSurvey.setFocusPainted(false);
        this.buttonNewSurvey.setBorder(null);
        this.buttonNewSurvey.setText("צור סקר חדש");
        this.buttonNewSurvey.setFont(new Font("Arial", Font.BOLD, 35)); // גודל הטקסט
        this.buttonNewSurvey.setForeground(Color.WHITE); // צבע הטקסט
        this.buttonNewSurvey.setHorizontalTextPosition(SwingConstants.CENTER);
        this.buttonNewSurvey.setVerticalTextPosition(SwingConstants.CENTER);
        this.buttonNewSurvey.addActionListener((event) -> { // פעולת הכפתור
            this.buttonNewSurvey.setVisible(false);
            if (this.myBot.getUsersChatIds().size() >= 0) {
                startSurvey();
            }
            else {
                JLabel q = new JLabel("לא ניתן ליצור סקר אם יש פחות מ3 משתמשים");
                q.setBounds(300, 200, 600, 50); // מיקום וגודל
                q.setHorizontalAlignment(SwingConstants.CENTER);
                q.setVerticalAlignment(SwingConstants.CENTER);
                q.setFont(new Font("Arial", Font.BOLD, 25));
                q.setForeground(Color.WHITE);
                this.setLayout(null);
                this.add(q);
                this.revalidate();
                this.repaint();
                Timer timer = new Timer(4500, e -> {
                    q.setVisible(false);
                    this.buttonNewSurvey.setVisible(true);
                    this.repaint();
                });
                timer.setRepeats(false);
                timer.start();
            }

        });
        this.add(this.buttonNewSurvey);
    }
    public void startSurvey() {
        this.systemSurvey.startSystem();
    }
    public int getWINDOW_WIDTH() {
        return WINDOW_WIDTH;
    }

    public int getWINDOW_HEIGHT() {
        return WINDOW_HEIGHT;
    }

    public JButton getButtonNewSurvey() {
        return buttonNewSurvey;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }

    public MyBot getBot() {
        return myBot;
    }

    public SystemSurvey getSystemSurvey() {
        return systemSurvey;
    }
}

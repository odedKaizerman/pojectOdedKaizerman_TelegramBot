import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class setQuestions {
    private int x;
    private int y;
    private int numberQuestion;
    private MainMenu mainMenu;
    private JLabel titleQuestion;
    private JLabel titleSelect;
    private ArrayList<JButton> questionButtons = new ArrayList<>();
    private JTextField topicTextFieldForQuetion;
    private SystemSurvey systemSurvey;
    private JButton save;
    private ArrayList<JTextField> setAnss;
    private ArrayList<JLabel> numberAns;

    private int countAns;
    private ArrayList<String> anss = new ArrayList<>();

    public setQuestions(int x, int y, int numberQuestion, MainMenu mainMenu, SystemSurvey systemSurvey, ArrayList<DataSurvey> dataSurvey) {
        this.save = new JButton("שמור");
        this.setAnss = new ArrayList<>();
        this.numberAns = new ArrayList<>();
        this.systemSurvey = systemSurvey;
        this.x = x;
        this.y = y;
        this.numberQuestion = numberQuestion;
        this.mainMenu = mainMenu;
        startCreat();
    }

    public void startCreat() {
        this.titleQuestion = new JLabel("שאלה " + this.numberQuestion);
        titleQuestion.setFont(new Font("Arial", Font.BOLD, 23));
        titleQuestion.setForeground(Color.WHITE);
        titleQuestion.setBounds(this.x, this.y, this.mainMenu.getWINDOW_WIDTH(), 50);
        titleQuestion.setHorizontalAlignment(SwingConstants.CENTER);
        this.mainMenu.add(titleQuestion);
        this.titleQuestion.setVisible(false);

        this.titleSelect = new JLabel("בחר מספר תשובות:");
        titleSelect.setFont(new Font("Arial", Font.BOLD, 16));
        titleSelect.setForeground(Color.WHITE);
        titleSelect.setBounds(this.x, this.y + 80, this.mainMenu.getWINDOW_WIDTH(), 50);
        titleSelect.setHorizontalAlignment(SwingConstants.CENTER);
        this.mainMenu.add(titleSelect);
        this.titleSelect.setVisible(false);

        int x = this.x + 532;
        for (int i = 0; i < 3; i++) {
            this.questionButtons.add(new JButton(String.valueOf(i + 2)));
            this.questionButtons.get(i).setBounds(x, this.y + 115, 40, 24);
            this.questionButtons.get(i).setFont(new Font("Arial", Font.BOLD, 11));
            this.questionButtons.get(i).setVisible(false);
            this.questionButtons.get(i).setBackground(Color.LIGHT_GRAY);
            this.mainMenu.add(this.questionButtons.get(i));
            x += 45;
        }
        this.questionButtons.get(0).addActionListener((event) -> {
            removeCountAnss(); this.countAns = 2; creatFieldAnss();
        });
        this.questionButtons.get(1).addActionListener((event) -> {
            removeCountAnss(); this.countAns = 3; creatFieldAnss();
        });
        this.questionButtons.get(2).addActionListener((event) -> {
            removeCountAnss(); this.countAns = 4; creatFieldAnss();
        });

        this.topicTextFieldForQuetion = new JTextField();
        this.topicTextFieldForQuetion.setBounds(this.x + 495, this.y + 40, 200, 20);
        this.topicTextFieldForQuetion.setFont(new Font("Arial", Font.PLAIN, 18));
        this.topicTextFieldForQuetion.setVisible(false);
        this.mainMenu.add(this.topicTextFieldForQuetion);
    }

    public void play() {
        this.titleSelect.setVisible(true);
        this.titleQuestion.setVisible(true);
        for (JButton btn : this.questionButtons) btn.setVisible(true);
        this.topicTextFieldForQuetion.setVisible(true);
    }

    public void remove() {
        this.titleSelect.setVisible(false);
        this.titleQuestion.setVisible(false);
        for (JButton btn : this.questionButtons) btn.setVisible(false);
        this.topicTextFieldForQuetion.setVisible(false);
    }

    public void removeCountAnss() {
        for (JButton btn : this.questionButtons) btn.setVisible(false);
        this.titleSelect.setVisible(false);
    }

    public void creatFieldAnss() {
        int yYY = this.y + 100;
        for (int i = 0; i < this.countAns; i++) {
            JTextField jTextField = new JTextField();
            jTextField.setBounds(this.x + 495, yYY, 200, 20);
            jTextField.setFont(new Font("Arial", Font.PLAIN, 18));
            jTextField.setVisible(true);
            this.setAnss.add(jTextField);
            this.mainMenu.add(jTextField);

            JLabel jLabel = new JLabel("תשובה " + (i + 1));
            jLabel.setFont(new Font("Arial", Font.BOLD, 15));
            jLabel.setForeground(Color.WHITE);
            jLabel.setBounds(this.x, yYY - 38, this.mainMenu.getWINDOW_WIDTH(), 50);
            jLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.numberAns.add(jLabel);
            this.mainMenu.add(jLabel);
            yYY += 50;
        }
        startSave(yYY);
        this.mainMenu.revalidate();
        this.mainMenu.repaint();
    }

    public void removeSetAnss() {
        for (int i = 0; i < this.countAns; i++) {
            this.setAnss.get(i).setVisible(false);
            this.numberAns.get(i).setVisible(false);
        }
        this.mainMenu.revalidate();
        this.mainMenu.repaint();
    }

    public void startSave(int y) {
        this.save.setBounds(this.x + 557, y - 20, 80, 30);
        this.save.setFont(new Font("Arial", Font.BOLD, 18));
        this.save.setForeground(Color.WHITE);
        this.save.setBackground(Color.BLUE);
        this.save.setVisible(false);

        this.save.addActionListener((event) -> {
            if (!isValid()) return;

            DataSurvey newData = new DataSurvey(this.systemSurvey, this.topicTextFieldForQuetion.getText(), returnAnss());
            this.systemSurvey.getDataSurvey().add(newData);
            this.systemSurvey.bigCountSave();

            removeSetAnss();
            remove();
            this.save.setVisible(false);

            if (this.systemSurvey.getCountSave() == this.systemSurvey.getCountQuestions()) {
                this.systemSurvey.showSendSurveyButton();
            }
        });

        this.mainMenu.add(this.save);
        this.save.setVisible(true);
    }

    public boolean isValid() {
        if (this.topicTextFieldForQuetion.getText().trim().isEmpty()) return false;
        for (JTextField tf : this.setAnss) {
            if (tf.getText().trim().isEmpty()) return false;
        }
        return true;
    }

    public ArrayList<String> returnAnss() {
        ArrayList<String> ansList = new ArrayList<>();
        for (JTextField tf : this.setAnss) {
            ansList.add(tf.getText());
        }
        return ansList;
    }

    public JButton getSave() {
        return save;
    }

    public String getTopicTextFieldForQuetion() {
        return this.topicTextFieldForQuetion.getText();
    }
}

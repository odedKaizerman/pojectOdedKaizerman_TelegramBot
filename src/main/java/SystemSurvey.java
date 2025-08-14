import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;


public class SystemSurvey {
    private MainMenu mainMenu;
    private JTextField topicTextField; // תיבת טקסט לנושא
    private JButton confirmTopicButton; // כפתור אישור
    private String subjectOfTheSurvey;
    private int countQuestions; // כמות שאלות
    private JLabel questionLabel;
    private JButton buttonReturn1; // כפתור חזרה
    private JLabel titleSelectOptionsLabel;
    private JLabel topicSelectionTitle;
    private ImageIcon imageForButtonReturn1; // תמונה חזרה
    private ArrayList<JButton> questionButtons = new ArrayList<>();
    private JLabel errorLabelTopic;
    private ArrayList<JButton> buttonsOptions = new ArrayList<>();
    private ArrayList<setQuestions> setQuestions = new ArrayList<>();
    private JButton buttonNext; // כפתור המשך
    private int countQ; // מספר שאלות
    private ArrayList<String> questuins = new ArrayList<>();
    private setGpt gpt;
    private int countSave;
    private JPanel resultsPanel;
    private ArrayList<DataSurvey> dataSurvey;// השאלות המוכנות
    private Timer countdownTimer;
    private JLabel timerLabel;
    private long timeRemaining = 15; // 5 דקות
    private ArrayList<int[]> pollResults;
    private ArrayList<Integer> pollTotalVotes;
    private ArrayList<Integer> pollMessages;
    private Long activeChatId;
    private JButton sendNowButton;
    private JButton scheduleButton;
    private JTextField scheduleMinutesField;
    private JButton scheduleConfirmButton;
    private JLabel scheduleTitleLabel;
    private JLabel preSendTimerLabel;
    private Timer preSendTimer;
    private int preSecondsRemaining;

    public SystemSurvey(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        this.dataSurvey = new ArrayList<>();
        this.subjectOfTheSurvey = " ";
        this.countQuestions = 0;
    }
    public void sendToBot() {
        hidePreSendUI();

        if (this.mainMenu.getBot().getUsersChatIds().isEmpty()) {
            JOptionPane.showMessageDialog(null, " אין משתמשים בקהילה – אי אפשר לשלוח סקר!", "שגיאה", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.buttonReturn1.setVisible(false);

        MyBot bot = this.mainMenu.getBot();
        ArrayList<Long> users = bot.getUsersChatIds();
        ArrayList<DataSurvey> surveyData = this.dataSurvey;
        this.pollResults = new ArrayList<>();
        this.pollTotalVotes = new ArrayList<>();
        this.pollMessages = new ArrayList<>();

        for (int i = 0; i < surveyData.size(); i++) {
            DataSurvey q = surveyData.get(i);
            String question = q.getNameOfQuestion();
            List<String> answers = q.getTextOfAnswers();

            this.pollResults.add(new int[answers.size()]);
            this.pollTotalVotes.add(0);
            this.pollMessages.add(null);

            SendPoll poll = new SendPoll();
            poll.setQuestion(question);
            poll.setOptions(answers);
            poll.setIsAnonymous(false);
            poll.setAllowMultipleAnswers(false);

            try {
                for (Long chatId : users) {
                    poll.setChatId(chatId.toString());
                    Message message = bot.execute(poll);
                    q.addPollId(message.getPoll().getId());

                    if (this.pollMessages.get(i) == null) {
                        this.pollMessages.set(i, message.getMessageId());
                        this.activeChatId = message.getChatId();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startCountdownTimer();
    }

    public void resetTimer() {
        if (this.countdownTimer != null) {
            this.countdownTimer.stop();
            this.countdownTimer = null;
        }
        if (this.timerLabel != null) {
            this.mainMenu.remove(this.timerLabel);
            this.timerLabel = null;
        }
    }

    public ArrayList<Integer> getPollMessages() { return pollMessages; }
    public ArrayList<int[]> getPollResults() { return pollResults; }
    public ArrayList<Integer> getPollTotalVotes() { return pollTotalVotes; }

    public void clearResultsPanel() {
        if (this.resultsPanel != null) {
            this.mainMenu.remove(this.resultsPanel);
            this.resultsPanel = null;
        }
    }

    public void setDataSurvey(ArrayList<DataSurvey> newData) {
        this.dataSurvey = newData;
    }


    public void showResults() {
        if (this.resultsPanel != null) {
            this.mainMenu.remove(this.resultsPanel);
            this.resultsPanel = null;
        }
        if (this.timerLabel != null) {
            this.mainMenu.remove(this.timerLabel);
            this.timerLabel = null;
        }
        this.buttonReturn1.setVisible(true);

        this.resultsPanel = new JPanel();
        this.resultsPanel.setLayout(new BoxLayout(this.resultsPanel, BoxLayout.Y_AXIS));
        this.resultsPanel.setBounds(200, 100, 800, 500);
        this.resultsPanel.setBackground(new Color(0, 0, 0, 180));

        for (int i = 0; i < this.dataSurvey.size(); i++) {
            DataSurvey q = this.dataSurvey.get(i);
            String question = q.getNameOfQuestion();
            List<String> answers = q.getTextOfAnswers();

            int[] votes = pollResults.get(i);
            int total = pollTotalVotes.get(i);

            JLabel qLabel = new JLabel("שאלה " + (i + 1) + ": " + question);
            qLabel.setForeground(Color.YELLOW);
            qLabel.setFont(new Font("Arial", Font.BOLD, 20));
            qLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            this.resultsPanel.add(qLabel);
            Integer[] order = new Integer[answers.size()];
            for (int idx = 0; idx < answers.size(); idx++) order[idx] = idx;

            Arrays.sort(order, new Comparator<Integer>() {
                @Override
                public int compare(Integer a, Integer b) {
                    int byVotesDesc = Integer.compare(votes[b], votes[a]);
                    if (byVotesDesc != 0) return byVotesDesc;
                    return answers.get(a).compareToIgnoreCase(answers.get(b));
                }
            });
            for (int k = 0; k < order.length; k++) {
                int j = order[k];
                String ans = answers.get(j);
                int count = votes[j];
                int percent = total == 0 ? 0 : (int) ((count * 100.0) / total);

                JLabel aLabel = new JLabel("- " + ans + ": " + count + " קולות (" + percent + "%)");
                aLabel.setForeground(Color.WHITE);
                aLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                aLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
                this.resultsPanel.add(aLabel);
            }

            this.resultsPanel.add(Box.createVerticalStrut(20));
        }

        if (this.resultsPanel.getParent() != null) {
            this.mainMenu.remove(this.resultsPanel);
        }

        this.mainMenu.add(this.resultsPanel);
        this.mainMenu.revalidate();
        this.mainMenu.repaint();
    }
    public void startCountdownTimer() {
        if (this.timerLabel == null) {
            this.timerLabel = new JLabel();
            this.timerLabel.setFont(new Font("Arial", Font.BOLD, 26));
            this.timerLabel.setForeground(Color.RED);
            this.timerLabel.setBounds(520, 20, 400, 40);
            this.mainMenu.add(this.timerLabel);
        }

        this.timeRemaining = 15;

        countdownTimer = new Timer(1000, e -> {
            long minutes = timeRemaining / 60;
            long seconds = timeRemaining % 60;
            this.timerLabel.setText("זמן להצבעה: " + String.format("%02d:%02d", minutes, seconds));
            this.mainMenu.repaint();
            timeRemaining--;

            if (timeRemaining < 0) {
                countdownTimer.stop();
                showResults();
            }
        });

        countdownTimer.start();
    }

    public int getCountQuestions() { return countQuestions; }
    public void bigCountSave() { this.countSave++; }
    public int getCountSave() { return this.countSave; }

    public void startSystem() {
        if (this.questionButtons.isEmpty()) {
            creatButtons();
        }
        startChoosingTopicSurvey();
    }

    public void setQuestions() {
        if (this.countQ == 1) {
            this.questuins.add(this.setQuestions.get(0).getTopicTextFieldForQuetion());
        } else if (this.countQ == 2) {
            this.questuins.add(this.setQuestions.get(0).getTopicTextFieldForQuetion());
            this.questuins.add(this.setQuestions.get(1).getTopicTextFieldForQuetion());
        } else {
            this.questuins.add(this.setQuestions.get(0).getTopicTextFieldForQuetion());
            this.questuins.add(this.setQuestions.get(1).getTopicTextFieldForQuetion());
            this.questuins.add(this.setQuestions.get(2).getTopicTextFieldForQuetion());
        }
    }

    public ArrayList<DataSurvey> getDataSurvey() { return dataSurvey; }

    public void addQuestion(int n) {
        if (n == 1) {
            this.setQuestions.add(new setQuestions(0, 100, 1, this.mainMenu, this, this.dataSurvey));
            this.setQuestions.get(0).play();
        }
        if (n == 2) {
            this.setQuestions.add(new setQuestions(-300, 100, 1, this.mainMenu, this, this.dataSurvey));
            this.setQuestions.add(new setQuestions(300, 100, 2, this.mainMenu, this, this.dataSurvey));
            for (int i = 0; i < this.setQuestions.size(); i++) {
                this.setQuestions.get(i).play();
            }
        }
        if (n == 3) {
            this.setQuestions.add(new setQuestions(-350, 100, 1, this.mainMenu, this, this.dataSurvey));
            this.setQuestions.add(new setQuestions(0, 100, 2, this.mainMenu, this, this.dataSurvey));
            this.setQuestions.add(new setQuestions(350, 100, 3, this.mainMenu, this, this.dataSurvey));
            for (int i = 0; i < this.setQuestions.size(); i++) {
                this.setQuestions.get(i).play();
            }
        }
    }

    public void toGptSelect() {
        this.gpt = new setGpt(0, 20, this.mainMenu, this.countQ, this.subjectOfTheSurvey, this);
        this.gpt.start();
    }
    public void creatButtons() {
        this.errorLabelTopic = new JLabel("הקלד נושא סקר!");
        this.errorLabelTopic.setFont(new Font("Arial", Font.BOLD, 40));
        this.errorLabelTopic.setForeground(Color.WHITE);
        this.errorLabelTopic.setBounds(0, 200, this.mainMenu.getWINDOW_WIDTH(), 50);
        this.errorLabelTopic.setHorizontalAlignment(SwingConstants.CENTER);
        this.errorLabelTopic.setVisible(false);
        this.mainMenu.add(this.errorLabelTopic);

        this.buttonNext = new JButton("המשך");
        this.buttonNext.setBounds(620, 70, 75, 30);
        this.buttonNext.setFont(new Font("Arial", Font.BOLD, 16));
        this.buttonNext.setForeground(Color.WHITE);
        this.buttonNext.setBackground(Color.black);
        this.buttonNext.setVisible(false);
        this.buttonNext.addActionListener((event) -> {
            if (!(this.topicTextField.getText().equals("") || this.topicTextField.getText().equals(" "))) {
                this.titleSelectOptionsLabel.setVisible(true);
                if (this.buttonsOptions.isEmpty()) {
                    this.buttonsOptions.add(new JButton("ידנית"));
                    this.buttonsOptions.add(new JButton("GPT"));
                    int Z = 515;
                    for (int i = 0; i < this.buttonsOptions.size(); i++) {
                        this.buttonsOptions.get(i).setBounds(Z, 400, 80, 40);
                        this.buttonsOptions.get(i).setFont(new Font("Arial", Font.BOLD, 21));
                        this.buttonsOptions.get(i).setForeground(Color.WHITE);
                        this.buttonsOptions.get(i).setBackground(Color.black);
                        this.buttonsOptions.get(i).setVisible(true);
                        this.mainMenu.add(this.buttonsOptions.get(i));
                        this.mainMenu.repaint();
                        Z += 90;
                    }
                    this.buttonsOptions.get(0).addActionListener((event1) -> {
                        addQuestion(this.countQ);
                        for (JButton b : this.buttonsOptions) b.setVisible(false);
                        this.titleSelectOptionsLabel.setVisible(false);
                    });
                    this.buttonsOptions.get(1).addActionListener((event1) -> {
                        for (JButton b : this.buttonsOptions) b.setVisible(false);
                        this.titleSelectOptionsLabel.setVisible(false);
                        toGptSelect();
                    });
                } else {
                    for (JButton b : this.buttonsOptions) b.setVisible(true);
                }
                this.questionLabel.setVisible(false);
                for (JButton b : this.questionButtons) b.setVisible(false);
                this.buttonNext.setVisible(false);
                this.topicTextField.setVisible(false);
                this.topicSelectionTitle.setVisible(false);
                this.subjectOfTheSurvey = this.topicTextField.getText();

            } else {
                this.errorLabelTopic.setVisible(true);
                this.mainMenu.revalidate();
                this.mainMenu.repaint();

                Timer timer = new Timer(3000, e -> {
                    this.errorLabelTopic.setVisible(false);
                    this.mainMenu.revalidate();
                    this.mainMenu.repaint();
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
        this.mainMenu.add(this.buttonNext);

        this.titleSelectOptionsLabel = new JLabel("בחר אופציה להגדרת שאלות ותשובות הסקר:");
        titleSelectOptionsLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleSelectOptionsLabel.setForeground(Color.WHITE);
        titleSelectOptionsLabel.setBounds(0, 100, this.mainMenu.getWINDOW_WIDTH(), 50);
        titleSelectOptionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.mainMenu.add(this.titleSelectOptionsLabel);
        this.titleSelectOptionsLabel.setVisible(false);

        this.topicSelectionTitle = new JLabel("הקלד את נושא הסקר:");
        this.topicSelectionTitle.setFont(new Font("Arial", Font.BOLD, 23));
        this.topicSelectionTitle.setForeground(Color.WHITE);
        this.topicSelectionTitle.setBounds(470, 37, this.mainMenu.getWINDOW_WIDTH(), 50);
        this.topicSelectionTitle.setHorizontalAlignment(SwingConstants.CENTER);
        this.mainMenu.add(this.topicSelectionTitle);
        this.topicSelectionTitle.setVisible(false);

        ImageIcon returnIcon = new ImageIcon(getClass().getResource("/Red.png"));
        Image scaledReturnImage = returnIcon.getImage().getScaledInstance(95, 70, Image.SCALE_SMOOTH);
        this.imageForButtonReturn1 = new ImageIcon(scaledReturnImage);

        this.buttonReturn1 = new JButton(this.imageForButtonReturn1);
        this.buttonReturn1.setBounds(0, 0, 68, 30);
        this.buttonReturn1.setOpaque(false);
        this.buttonReturn1.setContentAreaFilled(false);
        this.buttonReturn1.setBorderPainted(false);
        this.buttonReturn1.setFocusPainted(false);
        this.buttonReturn1.setBorder(null);
        this.buttonReturn1.setText("חזור");
        this.buttonReturn1.setFont(new Font("Arial", Font.BOLD, 23));
        this.buttonReturn1.setForeground(Color.WHITE);
        this.buttonReturn1.setHorizontalTextPosition(SwingConstants.CENTER);
        this.buttonReturn1.setVerticalTextPosition(SwingConstants.CENTER);
        this.buttonReturn1.setVisible(false);

        this.buttonReturn1.addActionListener((event) -> {
            if (this.preSendTimer != null) {
                this.preSendTimer.stop();
                this.preSendTimer = null;
            }
            hidePreSendUI();

            if (this.resultsPanel != null) {
                this.mainMenu.remove(this.resultsPanel);
                this.resultsPanel = null;
            }

            this.topicTextField.setText("");
            this.subjectOfTheSurvey = "";
            this.countQuestions = 0;
            this.mainMenu.getButtonNewSurvey().setVisible(true);
            this.buttonNext.setVisible(false);

            for (int i = 0; i < this.setQuestions.size(); i++) {
                this.setQuestions.get(i).remove();
                this.setQuestions.get(i).getSave().setVisible(false);
                this.setQuestions.get(i).removeSetAnss();
            }
            for (JButton b : this.buttonsOptions) b.setVisible(false);
            this.questuins.clear();
            this.resetTimer();

            this.titleSelectOptionsLabel.setVisible(false);
            this.setQuestions.clear();
            this.countSave = 0;

            this.dataSurvey.clear();
            this.pollResults = null;
            this.pollTotalVotes = null;
            this.pollMessages = null;

            if (this.gpt != null) {
                this.gpt.removeGptText();
            }
            removeChoosingTopicSurvey();
        });

        this.mainMenu.add(this.buttonReturn1);

        this.topicTextField = new JTextField();
        this.topicTextField.setBounds(973, 75, 200, 25);
        this.topicTextField.setFont(new Font("Arial", Font.PLAIN, 20));
        this.topicTextField.setVisible(false);
        this.mainMenu.add(this.topicTextField);

        this.confirmTopicButton = new JButton("הבא");
        this.confirmTopicButton.setBounds(3, this.mainMenu.getWINDOW_HEIGHT() - 73, 80, 30);
        this.confirmTopicButton.setFont(new Font("Arial", Font.BOLD, 25));
        this.confirmTopicButton.setForeground(Color.WHITE);
        this.confirmTopicButton.setBackground(Color.RED);
        this.confirmTopicButton.setVisible(false);
        this.confirmTopicButton.addActionListener((event) -> {
            setQuestions();

            this.subjectOfTheSurvey = this.topicTextField.getText();
            if (!this.subjectOfTheSurvey.trim().equals("") && this.countQuestions >= 1 && this.countQuestions <= 3) {
                removeChoosingTopicSurvey();
            } else {
            }
        });

        this.questionLabel = new JLabel("בחר כמות שאלות:");
        this.questionLabel.setFont(new Font("Arial", Font.BOLD, 23));
        this.questionLabel.setForeground(Color.WHITE);
        this.questionLabel.setBounds(730, 40, 300, 40);
        this.questionLabel.setVisible(false);
        this.mainMenu.add(this.questionLabel);

        int x = 735;
        for (int i = 0; i < 3; i++) {
            this.questionButtons.add(new JButton(String.valueOf(i + 1)));
            this.questionButtons.get(i).setBounds(x, 73, 50, 30);
            this.questionButtons.get(i).setFont(new Font("Arial", Font.BOLD, 13));
            this.questionButtons.get(i).setVisible(false);
            this.questionButtons.get(i).setBackground(Color.LIGHT_GRAY);
            this.mainMenu.add(this.questionButtons.get(i));
            x += 53;
        }
        this.questionButtons.get(0).addActionListener((event) -> {
            this.countQuestions = 1;
            this.questionButtons.get(0).setBackground(Color.GREEN);
            this.questionButtons.get(1).setVisible(false);
            this.questionButtons.get(2).setVisible(false);
            this.buttonNext.setVisible(true);
            this.countQ = 1;
        });
        this.questionButtons.get(1).addActionListener((event) -> {
            this.countQuestions = 2;
            this.questionButtons.get(1).setBackground(Color.GREEN);
            this.questionButtons.get(0).setVisible(false);
            this.questionButtons.get(2).setVisible(false);
            this.buttonNext.setVisible(true);
            this.countQ = 2;
        });
        this.questionButtons.get(2).addActionListener((event) -> {
            this.countQuestions = 3;
            this.questionButtons.get(2).setBackground(Color.GREEN);
            this.questionButtons.get(0).setVisible(false);
            this.questionButtons.get(1).setVisible(false);
            this.buttonNext.setVisible(true);
            this.countQ = 3;
        });
    }

    public void removeChoosingTopicSurvey() {
        this.topicSelectionTitle.setVisible(false);
        this.buttonReturn1.setVisible(false);
        this.topicTextField.setVisible(false);
        this.confirmTopicButton.setVisible(false);
        this.questionLabel.setVisible(false);
        for (JButton b : this.questionButtons) b.setVisible(false);
        this.countQuestions = 0;
        for (JButton b : this.questionButtons) b.setBackground(Color.LIGHT_GRAY);
        this.mainMenu.revalidate();
        this.mainMenu.repaint();
    }

    public void startChoosingTopicSurvey() {
        this.topicSelectionTitle.setVisible(true);
        this.buttonReturn1.setVisible(true);
        this.topicTextField.setVisible(true);
        this.confirmTopicButton.setVisible(true);
        this.questionLabel.setVisible(true);
        for (JButton b : this.questionButtons) b.setVisible(true);
        this.countQuestions = 0;
        this.mainMenu.revalidate();
        this.mainMenu.repaint();
    }

    public void showSendSurveyButton() {
        hidePreSendUI();

        // כפתור שליחה מיידית
        sendNowButton = new JButton("שליחה מיידית");
        sendNowButton.setFont(new Font("Arial", Font.BOLD, 22));
        sendNowButton.setBounds(405, 250, 180, 40);
        sendNowButton.setForeground(Color.WHITE);
        sendNowButton.setBackground(new Color(46, 204, 113));
        sendNowButton.addActionListener(e -> {
            this.mainMenu.remove(sendNowButton);
            if (scheduleButton != null) this.mainMenu.remove(scheduleButton);
            if (scheduleTitleLabel != null) this.mainMenu.remove(scheduleTitleLabel);
            if (scheduleMinutesField != null) this.mainMenu.remove(scheduleMinutesField);
            if (scheduleConfirmButton != null) this.mainMenu.remove(scheduleConfirmButton);
            this.mainMenu.revalidate();
            this.mainMenu.repaint();

            sendToBot();
        });
        this.mainMenu.add(sendNowButton);

        // כפתור תזמון
        scheduleButton = new JButton("תזמון לעוד X דקות");
        scheduleButton.setFont(new Font("Arial", Font.BOLD, 22));
        scheduleButton.setBounds(605, 250, 220, 40);
        scheduleButton.setForeground(Color.WHITE);
        scheduleButton.setBackground(new Color(52, 152, 219));
        scheduleButton.addActionListener(e -> showScheduleControls());
        this.mainMenu.add(scheduleButton);

        this.mainMenu.revalidate();
        this.mainMenu.repaint();
    }

    private void showScheduleControls() {
        if (scheduleTitleLabel == null) {
            scheduleTitleLabel = new JLabel("הזן כמה דקות להמתין לפני השליחה:");
            scheduleTitleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            scheduleTitleLabel.setForeground(Color.WHITE);
            scheduleTitleLabel.setBounds(545, 350, 400, 30);
            scheduleTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.mainMenu.add(scheduleTitleLabel);
        } else {
            scheduleTitleLabel.setVisible(true);
        }

        if (scheduleMinutesField == null) {
            scheduleMinutesField = new JTextField();
            scheduleMinutesField.setBounds(508, 350, 60, 30);
            scheduleMinutesField.setFont(new Font("Arial", Font.PLAIN, 20));
            this.mainMenu.add(scheduleMinutesField);
        } else {
            scheduleMinutesField.setVisible(true);
        }

        if (scheduleConfirmButton == null) {
            scheduleConfirmButton = new JButton("אישור תזמון");
            scheduleConfirmButton.setFont(new Font("Arial", Font.BOLD, 18));
            scheduleConfirmButton.setBounds(358, 350, 140, 30);
            scheduleConfirmButton.setForeground(Color.WHITE);
            scheduleConfirmButton.setBackground(new Color(41, 128, 185));
            scheduleConfirmButton.addActionListener(e -> {
                try {
                    int minutes = Integer.parseInt(scheduleMinutesField.getText().trim());
                    if (minutes <= 0) throw new NumberFormatException();
                    startPreSendCountdown(minutes);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "אנא הזן מספר דקות חוקי (> 0).", "שגיאה", JOptionPane.ERROR_MESSAGE);
                }
            });
            this.mainMenu.add(scheduleConfirmButton);
        } else {
            scheduleConfirmButton.setVisible(true);
        }

        this.mainMenu.revalidate();
        this.mainMenu.repaint();
    }

    private void startPreSendCountdown(int minutes) {
        if (sendNowButton != null) sendNowButton.setVisible(false);
        if (scheduleButton != null) scheduleButton.setVisible(false);
        if (scheduleTitleLabel != null) scheduleTitleLabel.setVisible(false);
        if (scheduleMinutesField != null) scheduleMinutesField.setVisible(false);
        if (scheduleConfirmButton != null) scheduleConfirmButton.setVisible(false);

        if (preSendTimerLabel == null) {
            preSendTimerLabel = new JLabel();
            preSendTimerLabel.setFont(new Font("Arial", Font.BOLD, 26));
            preSendTimerLabel.setForeground(new Color(255, 215, 0));
            preSendTimerLabel.setBounds(460, 20, 500, 40);
            this.mainMenu.add(preSendTimerLabel);
        } else {
            preSendTimerLabel.setVisible(true);
        }

        preSecondsRemaining = minutes * 60;

        preSendTimer = new Timer(1000, e -> {
            int m = preSecondsRemaining / 60;
            int s = preSecondsRemaining % 60;
            preSendTimerLabel.setText("זמן עד שליחת הסקר: " + String.format("%02d:%02d", m, s));
            this.mainMenu.repaint();
            preSecondsRemaining--;

            if (preSecondsRemaining < 0) {
                preSendTimer.stop();
                preSendTimer = null;
                sendToBot();
            }
        });
        preSendTimer.start();
    }

    private void hidePreSendUI() {
        if (sendNowButton != null) this.mainMenu.remove(sendNowButton);
        if (scheduleButton != null) this.mainMenu.remove(scheduleButton);
        if (scheduleTitleLabel != null) { this.mainMenu.remove(scheduleTitleLabel); scheduleTitleLabel = null; }
        if (scheduleMinutesField != null) { this.mainMenu.remove(scheduleMinutesField); scheduleMinutesField = null; }
        if (scheduleConfirmButton != null) { this.mainMenu.remove(scheduleConfirmButton); scheduleConfirmButton = null; }
        if (preSendTimerLabel != null) {
            this.mainMenu.remove(preSendTimerLabel);
            preSendTimerLabel = null;
        }
        if (preSendTimer != null) {
            preSendTimer.stop();
            preSendTimer = null;
        }
        this.mainMenu.revalidate();
        this.mainMenu.repaint();
    }

    public void tryCloseIfAllVoted() {
        if (this.pollTotalVotes == null || this.pollTotalVotes.isEmpty()) return;

        int usersCount = this.mainMenu.getBot().getUsersChatIds().size();
        if (usersCount <= 0) return;

        for (Integer t : this.pollTotalVotes) {
            if (t == null || t < usersCount) {
                return;
            }
        }

        if (this.countdownTimer != null) {
            this.countdownTimer.stop();
            this.countdownTimer = null;
        }

        javax.swing.SwingUtilities.invokeLater(this::showResults);
    }

}

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class setGpt {
    private int x;
    private int y;
    private int numberQuestion;
    private MainMenu mainMenu;
    private JLabel titleSelect;
    private ArrayList<Integer> countAnsForQ = new ArrayList<>();
    private ArrayList<JLabel> questionLabels = new ArrayList<>();
    private ArrayList<ArrayList<JButton>> allButtons = new ArrayList<>();
    private JButton nextButton;
    private String subjectOfTheSurvey;
    private JLabel gptInstructionLabel;
    private JTextField gptInputField;
    private JButton sendToGptButton;
    private SystemSurvey systemSurvey;
    private String userForGPT;
    private ArrayList<JComponent> currentGptComponents = new ArrayList<>();

    public setGpt(int x, int y, MainMenu mainMenu, int numberQuestion, String subjectOfTheSurvey, SystemSurvey systemSurvey) {
        this.x = x;
        this.y = y;
        this.systemSurvey = systemSurvey;
        this.mainMenu = mainMenu;
        this.numberQuestion = numberQuestion;
        this.subjectOfTheSurvey = subjectOfTheSurvey;
        this.titleSelect = new JLabel("בחר מספר תשובות לכל שאלה:");
        titleSelect.setFont(new Font("Arial", Font.BOLD, 30));
        titleSelect.setForeground(Color.WHITE);
        titleSelect.setBounds(this.x, this.y, this.mainMenu.getWINDOW_WIDTH(), 50);
        titleSelect.setHorizontalAlignment(SwingConstants.CENTER);
        this.mainMenu.add(titleSelect);
        createNextButton();
    }

    public void start() {
        this.titleSelect.setVisible(true);
        showAllQuestionSelections();
        this.mainMenu.revalidate();
        this.mainMenu.repaint();
    }

    private void showAllQuestionSelections() {
        int spacingY = 80;
        for (int i = 0; i < this.numberQuestion; i++) {
            JLabel questionLabel = new JLabel("שאלה " + (i + 1) + ":");
            questionLabel.setFont(new Font("Arial", Font.BOLD, 20));
            questionLabel.setForeground(Color.WHITE);
            questionLabel.setBounds(this.x, this.y + spacingY, this.mainMenu.getWINDOW_WIDTH(), 30);
            questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.mainMenu.add(questionLabel);
            this.questionLabels.add(questionLabel);
            addButtonsForQuestion(i, this.y + spacingY + 40);
            spacingY += 100;
        }
    }

    private void addButtonsForQuestion(int questionIndex, int yPos) {
        ArrayList<JButton> buttonsRow = new ArrayList<>();
        int startX = this.x + 480;
        for (int i = 0; i < 3; i++) {
            int count = i + 2;
            JButton button = new JButton(String.valueOf(count));
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setBounds(startX + (i * 55) + 45, yPos, 50, 30);
            button.setBackground(Color.LIGHT_GRAY);
            int finalIndex = questionIndex;
            button.addActionListener(e -> {
                while (countAnsForQ.size() <= finalIndex) countAnsForQ.add(0);
                countAnsForQ.set(finalIndex, count);
                for (JButton b : allButtons.get(finalIndex)) b.setBackground(Color.LIGHT_GRAY);
                button.setBackground(Color.GREEN);
                if (isAllAnswersSelected()) {
                    nextButton.setVisible(true);
                    mainMenu.repaint();
                }
            });
            this.mainMenu.add(button);
            buttonsRow.add(button);
        }
        this.allButtons.add(buttonsRow);
    }

    private boolean isAllAnswersSelected() {
        if (countAnsForQ.size() < numberQuestion) return false;
        for (int i = 0; i < numberQuestion; i++) {
            if (countAnsForQ.get(i) == 0) return false;
        }
        return true;
    }

    private void createNextButton() {
        this.nextButton = new JButton("המשך");
        this.nextButton.setBounds(this.x + 545, this.y + 100 + (numberQuestion * 100), 120, 40);
        this.nextButton.setFont(new Font("Arial", Font.BOLD, 20));
        this.nextButton.setBackground(Color.BLUE);
        this.nextButton.setForeground(Color.WHITE);
        this.nextButton.setVisible(false);
        this.nextButton.addActionListener(e -> {
            removeGptText();
            enterGptInstructions();
        });
        this.mainMenu.add(this.nextButton);
    }

    public void enterGptInstructions() {
        this.gptInstructionLabel = new JLabel("הכנס הנחיות מיוחדות לGPT. (אופציונלי)");
        gptInstructionLabel.setFont(new Font("Arial", Font.BOLD, 22));
        gptInstructionLabel.setForeground(Color.WHITE);
        gptInstructionLabel.setBounds(this.x, this.y, this.mainMenu.getWINDOW_WIDTH(), 40);
        gptInstructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.mainMenu.add(gptInstructionLabel);
        gptInstructionLabel.setVisible(true);

        this.gptInputField = new JTextField();
        gptInputField.setBounds(this.x + 420, this.y + 50, 400, 30);
        gptInputField.setFont(new Font("Arial", Font.PLAIN, 18));
        this.mainMenu.add(gptInputField);
        gptInputField.setVisible(true);

        this.sendToGptButton = new JButton("שלח ל-GPT");
        sendToGptButton.setBounds(this.x + 530, this.y + 100, 160, 35);
        sendToGptButton.setFont(new Font("Arial", Font.BOLD, 18));
        sendToGptButton.setBackground(Color.ORANGE);
        sendToGptButton.setForeground(Color.BLACK);
        sendToGptButton.addActionListener(e -> {
            this.userForGPT = gptInputField.getText();
            removeGptText();
            String json = getGptResponse(subjectOfTheSurvey, numberQuestion, countAnsForQ, userForGPT);
            showSurveyFromJson(json);
        });
        this.mainMenu.add(sendToGptButton);
        sendToGptButton.setVisible(true);

        this.mainMenu.revalidate();
        this.mainMenu.repaint();
    }

    public void removeGptText() {
        this.titleSelect.setVisible(false);
        this.nextButton.setVisible(false);
        for (JLabel label : questionLabels) label.setVisible(false);
        for (ArrayList<JButton> row : allButtons) for (JButton b : row) b.setVisible(false);
        if (gptInstructionLabel != null) gptInstructionLabel.setVisible(false);
        if (gptInputField != null) gptInputField.setVisible(false);
        if (sendToGptButton != null) sendToGptButton.setVisible(false);
        for (JComponent c : currentGptComponents) c.setVisible(false);
        currentGptComponents.clear();
        this.mainMenu.repaint();
    }


    public void showSurveyFromJson(String json) {
        try {
            if (json == null || json.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "תקלה: לא התקבלה תגובה מ-GPT.", "שגיאה", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JSONObject wrapper = new JSONObject(json);
            JSONArray array = wrapper.getJSONArray("survey");

            int yOffset = this.y + 45;
            ArrayList<DataSurvey> tempList = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String q = obj.getString("question");
                JLabel qLabel = new JLabel("שאלה " + (i + 1) + ": " + q);
                qLabel.setBounds(this.x, yOffset, this.mainMenu.getWINDOW_WIDTH(), 30);
                qLabel.setHorizontalAlignment(SwingConstants.CENTER);
                qLabel.setFont(new Font("Arial", Font.BOLD, 20));
                qLabel.setForeground(Color.YELLOW);
                this.mainMenu.add(qLabel);
                currentGptComponents.add(qLabel);
                yOffset += 40;

                JSONArray answers = obj.getJSONArray("answers");
                ArrayList<String> answerList = new ArrayList<>();
                for (int j = 0; j < answers.length(); j++) {
                    String answer = answers.getString(j);
                    answerList.add(answer);
                    JLabel aLabel = new JLabel("- " + answer);
                    aLabel.setBounds(this.x, yOffset, this.mainMenu.getWINDOW_WIDTH(), 25);
                    aLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    aLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                    aLabel.setForeground(Color.WHITE);
                    this.mainMenu.add(aLabel);
                    currentGptComponents.add(aLabel);
                    yOffset += 30;
                }
                tempList.add(new DataSurvey(this.mainMenu.getSystemSurvey(), q, answerList));
                yOffset += 20;
            }

            JButton retryBtn = new JButton("🔁 שלח ל-GPT שוב");
            retryBtn.setBounds(this.x + 440, yOffset, 180, 35);
            retryBtn.setFont(new Font("Arial", Font.BOLD, 16));
            retryBtn.setBackground(Color.GRAY);
            retryBtn.setForeground(Color.WHITE);
            retryBtn.addActionListener(e -> {
                removeGptText();
                String newJson = getGptResponse(subjectOfTheSurvey, numberQuestion, countAnsForQ, userForGPT);
                showSurveyFromJson(newJson);
            });
            this.mainMenu.add(retryBtn);
            currentGptComponents.add(retryBtn);

            JButton confirmBtn = new JButton("✅ אישור והמשך");
            confirmBtn.setBounds(this.x + 640, yOffset, 180, 35);
            confirmBtn.setFont(new Font("Arial", Font.BOLD, 16));
            confirmBtn.setBackground(Color.GREEN);
            confirmBtn.setForeground(Color.BLACK);
            confirmBtn.addActionListener(e -> {
                this.systemSurvey.setDataSurvey(tempList);
                removeGptText();
                this.systemSurvey.showSendSurveyButton();
            });



            this.mainMenu.add(confirmBtn);
            currentGptComponents.add(confirmBtn);

            this.mainMenu.revalidate();
            this.mainMenu.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "שגיאה בעת קריאת הסקר מ-GPT.", "שגיאה", JOptionPane.ERROR_MESSAGE);
        }
    }



    private String getGptResponse(String topic, int questionCount, List<Integer> answersPerQuestion, String userInstructions) {
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("צור סקר בנושא: ").append(topic).append(".\n");
            prompt.append("הסקר צריך להכיל ").append(questionCount).append(" שאלות.\n");
            prompt.append("לשאלות יש את מספר התשובות הבאות (לפי הסדר): ").append(answersPerQuestion).append("\n");
            if (userInstructions != null && !userInstructions.trim().isEmpty()) {
                prompt.append("הנחיות נוספות: ").append(userInstructions).append("\n");
            }
            prompt.append("החזר את הסקר בפורמט JSON בלבד, כך שכל שאלה היא אובייקט עם שדה \"question\" ושדה \"answers\" (מערך).\n");
            prompt.append("אין להוסיף טקסט נוסף מעבר ל-JSON. אל תשתמש ב-Markdown או ```.");

            String encodedPrompt = URLEncoder.encode(prompt.toString(), "UTF-8");
            String apiUrl = "https://app.seker.live/fm1/send-message?id=324941780&text=" + encodedPrompt;

            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            JSONObject response = new JSONObject(content.toString());
            String raw = response.getString("extra");
            return raw.replace("```json", "").replace("```", "").trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

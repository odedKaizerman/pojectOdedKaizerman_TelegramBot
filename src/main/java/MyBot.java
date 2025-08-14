import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {
    private ArrayList<Long> usersChatIds = new ArrayList<>();
    private MainMenu mainMenu;

    @Override
    public void onUpdateReceived(Update update) {
        // הצטרפות לקהילה
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (!this.usersChatIds.contains(update.getMessage().getChatId())) {
                String msg = update.getMessage().getText();
                if (msg.equals("/start") || msg.equals("היי") || msg.equals("Hi")) {
                    addUserNew(update);
                }
            }

        }

        // קבלת תשובות לסקרים
        if (update.hasPollAnswer()) {
            PollAnswer pollAnswer = update.getPollAnswer();
            List<Integer> optionIds = pollAnswer.getOptionIds();
            String pollId = pollAnswer.getPollId();

            SystemSurvey survey = this.mainMenu.getSystemSurvey();
            List<DataSurvey> questions = survey.getDataSurvey();
            List<int[]> results = survey.getPollResults();
            List<Integer> totals = survey.getPollTotalVotes();

            for (int i = 0; i < questions.size(); i++) {
                DataSurvey q = questions.get(i);
                if (q.getPollIds() != null && q.getPollIds().contains(pollId)) {
                    int[] votes = results.get(i);
                    for (int opt : optionIds) {
                        if (opt >= 0 && opt < votes.length) {
                            votes[opt]++;
                        }
                    }
                    totals.set(i, totals.get(i) + 1);
                    this.mainMenu.getSystemSurvey().tryCloseIfAllVoted();
                    break;
                }
            }
        }
    }
    public void addUserNew(Update update) {
        Long newUserId = update.getMessage().getChatId();
        this.usersChatIds.add(newUserId);
        this.mainMenu.BiggerCountUsers();
        String first = update.getMessage().getFrom() != null ? update.getMessage().getFrom().getFirstName() : null;
        String last  = update.getMessage().getFrom() != null ? update.getMessage().getFrom().getLastName()  : null;
        String user  = update.getMessage().getFrom() != null ? update.getMessage().getFrom().getUserName()  : null;

        StringBuilder nameBuilder = new StringBuilder();
        if (first != null && !first.isEmpty()) nameBuilder.append(first);
        if (last  != null && !last.isEmpty())  {
            if (nameBuilder.length() > 0) nameBuilder.append(" ");
            nameBuilder.append(last);
        }
        String displayName = nameBuilder.toString().trim();
        if (displayName.isEmpty() && user != null && !user.isEmpty()) {
            displayName = "@" + user;
        }
        if (displayName.isEmpty()) {
            displayName = "משתמש חדש";
        }
        String broadcastText = "חבר חדש הצטרף: " + displayName +
                "\nגודל הקהילה כעת: " + this.usersChatIds.size();
        for (Long chatId : this.usersChatIds) {
            if (!chatId.equals(newUserId)) {
                SendMessage msg = new SendMessage(chatId.toString(), broadcastText);
                try {
                    execute(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void setMainMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public ArrayList<Long> getUsersChatIds() {
        return usersChatIds;
    }

    public String getBotToken() {
        return "7698122228:AAGeEtcoAZSCVCZig_Q3hZdKeA0XSop5xaI";
    }

    @Override
    public String getBotUsername() {
        return "oded_Survey2025Bot";
    }
}

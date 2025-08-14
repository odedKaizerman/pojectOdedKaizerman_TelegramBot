import java.util.ArrayList;

public class DataSurvey {
    private String namesQuestions;
    private ArrayList<String> textAnssWers;
    private SystemSurvey systemSurvey;

    private ArrayList<String> pollIds = new ArrayList<>();

    public void addPollId(String pollId) {
        this.pollIds.add(pollId);
    }

    public ArrayList<String> getPollIds() {
        return this.pollIds;
    }


    public DataSurvey(SystemSurvey systemSurvey, String namesQuestions, ArrayList<String> textAnssWers) {
        this.namesQuestions = namesQuestions;
        this.textAnssWers = textAnssWers;
        this.systemSurvey = systemSurvey;
//        setToSystem();
    }
    public void setToSystem() {
        this.systemSurvey.getDataSurvey().add(this);
    }
    public String toString() {
        String textAnswers = "";
        for (int i = 0; i < this.textAnssWers.size(); i++) {
            textAnswers += this.textAnssWers.get(i);
            textAnswers += "\n";
        }
        return
                "שם שאלה: " + this.namesQuestions +"\n" +textAnswers;

    }
    public String getNameOfQuestion() {
        return this.namesQuestions;
    }

    public ArrayList<String> getTextOfAnswers() {
        return this.textAnssWers;
    }



}

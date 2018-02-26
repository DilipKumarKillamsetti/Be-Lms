package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android5 on 10/6/16.
 */

public class QuizQuestion implements Parcelable {
    public static final Creator<QuizQuestion> CREATOR = new Creator<QuizQuestion>() {
        @Override
        public QuizQuestion createFromParcel(Parcel source) {
            return new QuizQuestion(source);
        }

        @Override
        public QuizQuestion[] newArray(int size) {
            return new QuizQuestion[size];
        }
    };
    private String id;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int answer;
    private int selected_answer = 0;

    public QuizQuestion(String id, String question, String option1, String option2, String option3, String option4, int answer, int selected_answer) {
        this.id = id;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answer = answer;
        this.selected_answer = selected_answer;
    }

    protected QuizQuestion(Parcel in) {
        this.id = in.readString();
        this.question = in.readString();
        this.option1 = in.readString();
        this.option2 = in.readString();
        this.option3 = in.readString();
        this.option4 = in.readString();
        this.answer = in.readInt();
        this.selected_answer = in.readInt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public int getSelected_answer() {
        return selected_answer;
    }

    public void setSelected_answer(int selected_answer) {
        this.selected_answer = selected_answer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.question);
        dest.writeString(this.option1);
        dest.writeString(this.option2);
        dest.writeString(this.option3);
        dest.writeString(this.option4);
        dest.writeInt(this.answer);
        dest.writeInt(this.selected_answer);
    }
}

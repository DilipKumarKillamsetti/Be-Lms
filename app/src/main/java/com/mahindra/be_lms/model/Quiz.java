package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chaitali on 10/6/16.
 */

public class Quiz implements Parcelable {
    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel source) {
            return new Quiz(source);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };
    private String id;
    private String quiz_title;
    private String quiz_desc;
    private List<QuizQuestion> quizQuestionsList;

    public Quiz() {
    }

    public Quiz(String id, String quiz_title, String quiz_desc, List<QuizQuestion> quizQuestionsList) {
        this.id = id;
        this.quiz_title = quiz_title;
        this.quiz_desc = quiz_desc;
        this.quizQuestionsList = quizQuestionsList;
    }

    protected Quiz(Parcel in) {
        this.id = in.readString();
        this.quiz_title = in.readString();
        this.quiz_desc = in.readString();
        this.quizQuestionsList = new ArrayList<>();
        in.readList(this.quizQuestionsList, QuizQuestion.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuiz_title() {
        return quiz_title;
    }

    public void setQuiz_title(String quiz_title) {
        this.quiz_title = quiz_title;
    }

    public String getQuiz_desc() {
        return quiz_desc;
    }

    public void setQuiz_desc(String quiz_desc) {
        this.quiz_desc = quiz_desc;
    }

    public List<QuizQuestion> getQuizQuestionsList() {
        return quizQuestionsList;
    }

    public void setQuizQuestionsList(List<QuizQuestion> quizQuestionsList) {
        this.quizQuestionsList = quizQuestionsList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.quiz_title);
        dest.writeString(this.quiz_desc);
        dest.writeList(this.quizQuestionsList);
    }
}

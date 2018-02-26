package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by Chaitali Chavan on 11/18/16.
 * Modified By Chaitali Chavan on 11/18/16,
 */

public class Survey implements Parcelable {
    public static final Creator<Survey> CREATOR = new Creator<Survey>() {
        @Override
        public Survey createFromParcel(Parcel source) {
            return new Survey(source);
        }

        @Override
        public Survey[] newArray(int size) {
            return new Survey[size];
        }
    };
    @SerializedName("id")
    private String survey_id;
    @SerializedName("name")
    private String survey_question;
    @SerializedName("type")
    private String survey_question_type;
    @SerializedName("choices")
    private String survey_question_options;
    @SerializedName("options")
    private List<String> optionArrayList;
    @SerializedName("none")
    private String user_answer;
    @SerializedName("")
    private JSONArray mcq_answer = new JSONArray();

    public Survey() {
    }

    public Survey(String survey_id, String survey_question, String survey_question_type, String user_answer, List<String> optionArrayList, JSONArray mcq_answer,String survey_question_options) {
        this.survey_id = survey_id;
        this.survey_question = survey_question;
        this.survey_question_type = survey_question_type;
        this.user_answer = user_answer;
        this.optionArrayList = optionArrayList;
        this.mcq_answer = mcq_answer;
        this.survey_question_options = survey_question_options;
    }

    protected Survey(Parcel in) {
        this.survey_id = in.readString();
        this.survey_question = in.readString();
        this.survey_question_type = in.readString();
        this.optionArrayList = in.createStringArrayList();
        this.user_answer = in.readString();
        this.survey_question_options = in.readString();
        this.mcq_answer = in.readParcelable(JSONArray.class.getClassLoader());
    }

    public List<String> getOptionArrayList() {
        return optionArrayList;
    }

    public void setOptionArrayList(List<String> optionArrayList) {
        this.optionArrayList = optionArrayList;
    }

    public String getSurvey_id() {
        return survey_id;
    }

    public void setSurvey_id(String survey_id) {
        this.survey_id = survey_id;
    }

    public String getSurvey_question() {
        return survey_question;
    }

    public void setSurvey_question(String survey_question) {
        this.survey_question = survey_question;
    }

    public String getSurvey_question_type() {
        return survey_question_type;
    }

    public void setSurvey_question_type(String survey_question_type) {
        this.survey_question_type = survey_question_type;
    }

    public String getUser_answer() {
        return user_answer;
    }

    public void setUser_answer(String user_answer) {
        this.user_answer = user_answer;
    }

    public String getSurvey_question_options() {
        return survey_question_options;
    }

    public void setSurvey_question_options(String survey_question_options) {
        this.survey_question_options = survey_question_options;
    }

    public JSONArray getMcq_answer() {
        return mcq_answer;
    }

    public void setMcq_answer(JSONArray mcq_answer) {
        this.mcq_answer = mcq_answer;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "mcq_answer=" + mcq_answer +
                ", survey_id='" + survey_id + '\'' +
                ", survey_question='" + survey_question + '\'' +
                ", survey_question_type='" + survey_question_type + '\'' +
                ", optionArrayList=" + optionArrayList +
                ", user_answer='" + user_answer + '\'' +
                ", survey_question_options='" + survey_question_options + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.survey_id);
        dest.writeString(this.survey_question);
        dest.writeString(this.survey_question_type);
        dest.writeStringList(this.optionArrayList);
        dest.writeString(this.user_answer);
        dest.writeString(this.survey_question_options);
        dest.writeParcelable((Parcelable) this.mcq_answer, flags);
    }
}

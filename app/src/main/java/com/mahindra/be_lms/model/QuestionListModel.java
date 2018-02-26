package com.mahindra.be_lms.model;


/**
 * Created by anupamchugh on 09/02/16.
 */
public class QuestionListModel {


    public static final int TEXT_TYPE=0;
    public static final int IMAGE_TYPE=1;
    public static final int AUDIO_TYPE=2;
    public static final int RADIO_TYPE=3;
    public static final int T_F_TYPE=4;
    public static final int CK_BOX_TYPE=5;
    public static final int DESC_TYPE=6;

    public int type;
    public int data;
    public String text;



    public QuestionListModel(int type, String text, int data)
    {
        this.type=type;
        this.data=data;
        this.text=text;

    }

}

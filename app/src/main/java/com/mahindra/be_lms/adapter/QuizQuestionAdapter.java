package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.model.QuizQuestion;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by android5 on 10/6/16.
 */
public class QuizQuestionAdapter extends BaseAdapter {
    public final List<QuizQuestion> quizQuestionList;
    private final Context context;

    public QuizQuestionAdapter(Context context, List<QuizQuestion> quizQuestionsList) {
        this.context = context;
        this.quizQuestionList = quizQuestionsList;
    }

    @Override
    public int getCount() {
        return quizQuestionList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.custom_quiz_question_layout, parent, false);
        MyQuizQuestionViewHolder holder = new MyQuizQuestionViewHolder(view);
        holder.tvQuizQuestion.setText(quizQuestionList.get(position).getQuestion());
        holder.rbOption1.setText(quizQuestionList.get(position).getOption1());
        holder.rbOption2.setText(quizQuestionList.get(position).getOption2());
        holder.rbOption3.setText(quizQuestionList.get(position).getOption3());
        holder.rbOption4.setText(quizQuestionList.get(position).getOption4());

        holder.rgQuizOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) view.findViewById(checkedId);
                int selected_option = group.indexOfChild(radioButton) + 1;
                L.t("selected answer: " + radioButton.getText() + ": " + selected_option);
                quizQuestionList.get(position).setSelected_answer(selected_option);
            }
        });
        L.l("selected: " + quizQuestionList.get(position).getSelected_answer() + " position: " + position + 1);
        if (quizQuestionList.get(position).getSelected_answer() == 1) {
            holder.rbOption1.setChecked(true);
        } else if (quizQuestionList.get(position).getSelected_answer() == 2) {
            holder.rbOption2.setChecked(true);
        } else if (quizQuestionList.get(position).getSelected_answer() == 3) {
            holder.rbOption3.setChecked(true);
        } else if (quizQuestionList.get(position).getSelected_answer() == 4) {
            holder.rbOption4.setChecked(true);
        }
        return view;
    }

    public void setmCallback(MyCallback mCallback) {
        MyCallback myCallback = mCallback;
    }

    public class MyQuizQuestionViewHolder {
        @BindView(R.id.tvQuizQuestion)
        TextView tvQuizQuestion;
        @BindView(R.id.rgQuizOption)
        RadioGroup rgQuizOption;
        @BindView(R.id.rbOption1)
        RadioButton rbOption1;
        @BindView(R.id.rbOption2)
        RadioButton rbOption2;
        @BindView(R.id.rbOption3)
        RadioButton rbOption3;
        @BindView(R.id.rbOption4)
        RadioButton rbOption4;

        public MyQuizQuestionViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

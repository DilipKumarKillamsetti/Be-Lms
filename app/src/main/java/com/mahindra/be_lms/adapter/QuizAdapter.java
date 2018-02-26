package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.Quiz;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Chaitali on 10/6/16.
 */
public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.MyQuizViewHolder> {

    private final Context context;
    private final List<Quiz> quizList;
    Callback mCallback;

    public QuizAdapter(Context context, List<Quiz> quizList) {
        this.context = context;
        this.quizList = quizList;
    }

    @Override
    public MyQuizViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_quiz_layout, parent, false);
        return new MyQuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyQuizViewHolder holder, int position) {
        holder.tvQuizTitle.setText(quizList.get(position).getQuiz_title());
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public class MyQuizViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvQuizTitle)
        TextView tvQuizTitle;

        public MyQuizViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallback.myCallback(getAdapterPosition());
        }
    }
}

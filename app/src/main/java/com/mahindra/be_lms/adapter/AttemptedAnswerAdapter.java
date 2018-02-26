package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.AttemptModel;
import com.mahindra.be_lms.model.QuizModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 9/25/2017.
 */

public class AttemptedAnswerAdapter extends RecyclerView.Adapter<AttemptedAnswerAdapter.CourseViewHolder> {

    private final List<AttemptModel> attemptsList;
    private final Context context;
    private Callback mCallback;

    public AttemptedAnswerAdapter(Context context, List<AttemptModel> attemptsList) {
        this.context = context;
        this.attemptsList = attemptsList;
    }
    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public AttemptedAnswerAdapter.CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_answered_items, parent, false);
        return new AttemptedAnswerAdapter.CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttemptedAnswerAdapter.CourseViewHolder holder, final int position) {

        holder.questionNo.setText(attemptsList.get(position).getId());
        holder.answerStatus.setText(attemptsList.get(position).getStatus());


    }

    @Override
    public int getItemCount() {
        return attemptsList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.questionNo)
        TextView questionNo;
        @BindView(R.id.answerStatus)
        TextView answerStatus;

        public CourseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

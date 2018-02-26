package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.QuizResultModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 9/27/2017.
 */

public class QuizResultAdapter  extends RecyclerView.Adapter<QuizResultAdapter.AttemptsViewHolder> {

    private  List<QuizResultModel> quizResultModels;
    private final Context context;
    private Callback mCallback;

    public QuizResultAdapter(Context context, List<QuizResultModel> quizResultModels) {
        this.context = context;
        this.quizResultModels = quizResultModels;
    }
    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public void setAttemptsList(List<QuizResultModel> quizResultModels) {
        this.quizResultModels = quizResultModels;
    }

    @Override
    public QuizResultAdapter.AttemptsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_result_item, parent, false);
        return new QuizResultAdapter.AttemptsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final QuizResultAdapter.AttemptsViewHolder holder, final int position) {
               QuizResultModel quizResultModel = quizResultModels.get(position);
            holder.attemptsText.setText(quizResultModel.getQuizAttempt());
        if(null == quizResultModel.getQuizSumGrades()){
            holder.gradeText.setText("-");
        }else{
            holder.gradeText.setText(quizResultModel.getQuizSumGrades());
        }
           // holder.marksText.setText(quizResultModel.getQuizSumGrades());
            //holder.previewText.setText();
            if(quizResultModel.getQuizState().equalsIgnoreCase("finished")){
                holder.previewText.setPaintFlags(holder.previewText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.previewText.setText(context.getString(R.string.review_text));
                holder.stateText.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                holder.previewText.setTextColor(context.getResources().getColor(R.color.green));
            }else{
                holder.previewText.setText("-");
                holder.stateText.setTextColor(context.getResources().getColor(R.color.green));
            }
            holder.stateText.setText(quizResultModel.getQuizState().toUpperCase());

        holder.previewText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCallback.myCallback(position, context.getString(R.string.quiz_tag));
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizResultModels.size();
    }

    public class AttemptsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.attemptsText)
        TextView attemptsText;
        @BindView(R.id.stateText)
        TextView stateText;
        @BindView(R.id.marksText)
        TextView marksText;
        @BindView(R.id.gradeText)
        TextView gradeText;
        @BindView(R.id.previewText)
        TextView previewText;

        public AttemptsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}


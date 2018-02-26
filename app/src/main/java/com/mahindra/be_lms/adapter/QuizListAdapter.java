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
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.QuizDataModel;
import com.mahindra.be_lms.model.QuizModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 9/22/2017.
 */

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.CourseViewHolder> {

    private  List<QuizDataModel> quizList;

    private final Context context;
    private Callback mCallback;

    public QuizListAdapter(Context context, List<QuizDataModel> quizList) {
        this.context = context;
        this.quizList = quizList;
    }
    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public QuizListAdapter.CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_list_item, parent, false);
        return new QuizListAdapter.CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuizListAdapter.CourseViewHolder holder, final int position) {

        holder.quizName.setText(quizList.get(position).getQuizTitle());
        holder.btnStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCallback.myCallback(position, context.getString(R.string.quiz_tag));
            }
        });


    }
    public void setCourseList(List<QuizDataModel> quizList) {
        this.quizList = quizList;
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.quizName)
        TextView quizName;
        @BindView(R.id.btnStart)
        Button btnStart;

        public CourseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

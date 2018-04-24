package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.SurveyFeedback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 9/21/2017.
 */

public class FeedBackListAdapter extends RecyclerView.Adapter<FeedBackListAdapter.CourseViewHolder> {

    private  List<SurveyFeedback> surveyFeedbacks;
    private final Context context;
    private Callback mCallback;

    public FeedBackListAdapter(Context context, List<SurveyFeedback> surveyFeedbacks) {
        this.context = context;
        this.surveyFeedbacks = surveyFeedbacks;
    }
    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public FeedBackListAdapter.CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feedback_status_list_item, parent, false);
        return new FeedBackListAdapter.CourseViewHolder(view);
    }
    public void setCourseList(List<SurveyFeedback> surveyFeedbacks) {
        this.surveyFeedbacks = surveyFeedbacks;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final FeedBackListAdapter.CourseViewHolder holder, final int position) {

        holder.surveyName.setText(surveyFeedbacks.get(position).getSurveyName());
        holder.feedbackStatus.setText(surveyFeedbacks.get(position).getStatus());
        if(surveyFeedbacks.get(position).getStatus().equalsIgnoreCase("Attempted")){
            holder.feedbackStatus.setTextColor(context.getResources().getColor(R.color.green));
        }else{
            holder.feedbackStatus.setTextColor(context.getResources().getColor(R.color.red_btn_bg_color));
        }
        holder.attemptDate.setText(surveyFeedbacks.get(position).getAttemptdate());

    }

    @Override
    public int getItemCount() {
        return surveyFeedbacks.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.surveyName)
        TextView surveyName;
        @BindView(R.id.feedbackStatus)
        TextView feedbackStatus;
        @BindView(R.id.attemptDate)
        TextView attemptDate;


        public CourseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }



    }
}

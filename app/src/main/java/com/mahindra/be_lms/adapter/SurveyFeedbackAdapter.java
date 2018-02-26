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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MEDHVI-CONT on 9/29/2017.
 */

public class SurveyFeedbackAdapter extends RecyclerView.Adapter<SurveyFeedbackAdapter.SurveyViewHolder> {

    private  List<SurveyFeedback> surveyFeedbackList;
    private final Context context;
    private Callback mCallback;

    public SurveyFeedbackAdapter(Context context, List<SurveyFeedback> surveyFeedbackList) {
        this.context = context;
        this.surveyFeedbackList = surveyFeedbackList;
    }

    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }


    @Override
    public SurveyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.survey_list_item, parent, false);
        return new SurveyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final SurveyViewHolder holder, final int position) {

        holder.courseName.setText(surveyFeedbackList.get(position).getSurveyName());
        holder.courseStatus.setText(surveyFeedbackList.get(position).getStatus());
        if(surveyFeedbackList.get(position).getSurveyDescription().contains("<")){
            if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)){
                holder.courseDescription.setText(Html.fromHtml(surveyFeedbackList.get(position).getSurveyDescription(), Html.FROM_HTML_MODE_COMPACT));

            }else{
                holder.courseDescription.setText(Html.fromHtml(surveyFeedbackList.get(position).getSurveyDescription()));

            }
        }else{
            holder.courseDescription.setText(surveyFeedbackList.get(position).getSurveyDescription());
        }

        holder.btnLearnTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCallback.myCallback(position, context.getString(R.string.quiz_tag));
            }
        });
        holder.courseDescription.setMaxLines(1);
//        if(surveyFeedbackList.get(position).getSurveyDescription().isEmpty()){
//            holder.moreBtn.setVisibility(View.GONE);
//        }else{
//            holder.moreBtn.setVisibility(View.VISIBLE);
//        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                holder.moreBtn.setVisibility(View.GONE);
                holder.hideBtn.setVisibility(View.VISIBLE);
                holder.courseDescription.setMaxLines(Integer.MAX_VALUE);
            }
        });
        holder.hideBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                holder.moreBtn.setVisibility(View.VISIBLE);
                holder.hideBtn.setVisibility(View.GONE);
                holder.courseDescription.setMaxLines(1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return surveyFeedbackList.size();
    }

    public void setSFeedbackList(ArrayList<SurveyFeedback> getSurveyFeedback) {
        this.surveyFeedbackList = getSurveyFeedback;
    }

    public class SurveyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.courseName)
        TextView courseName;
        @BindView(R.id.courseStatus)
        TextView courseStatus;
        @BindView(R.id.courseDescription)
        TextView courseDescription;
        @BindView(R.id.btnLearnTest)
        Button btnLearnTest;
        @BindView(R.id.moreBtn)
        TextView moreBtn;
        @BindView(R.id.hideBtn)
        TextView hideBtn;

        public SurveyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }



    }
}

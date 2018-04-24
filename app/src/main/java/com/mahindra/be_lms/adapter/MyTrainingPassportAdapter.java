package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.MyTrainningPassportModel;
import com.mahindra.be_lms.model.QuizResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by android7 on 10/5/16.
 */
public class MyTrainingPassportAdapter extends RecyclerView.Adapter<MyTrainingPassportAdapter.MyTrainingPasswordViewHolder> {
    private final Context context;
    public List<MyTrainningPassportModel> myTrainningPassportModels;
    private Callback callback;

    public MyTrainingPassportAdapter(Context context, List<MyTrainningPassportModel> myTrainningPassportModels) {
        this.context = context;
        this.myTrainningPassportModels = myTrainningPassportModels;
    }

    @Override
    public MyTrainingPasswordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_my_training_passport_layout, parent, false);
        return new MyTrainingPasswordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyTrainingPasswordViewHolder holder, int position) {
        holder.tvMyTrainingPassportFragmentCourseName.setText(myTrainningPassportModels.get(position).getCoursename());
        holder.tvMyTrainingPassportFragmentQuizName.setText(myTrainningPassportModels.get(position).getQuizname());
        holder.tvMyTrainingPassportFragmentQuizDate.setText(myTrainningPassportModels.get(position).getDate());
        holder.tvStatus.setText(myTrainningPassportModels.get(position).getState());
        Drawable quizResultIcon = context.getResources().getDrawable(R.drawable.fail);
        if (myTrainningPassportModels.get(position).getResult().equalsIgnoreCase("passed")) {
            quizResultIcon = context.getResources().getDrawable(R.drawable.passed);
        }
        holder.ivMyTrainingPassportFragmentQuizIcon.setImageDrawable(quizResultIcon);
//        if (quizResultList.get(position).getFeedback().equalsIgnoreCase("1") && quizResultList.get(position).getPost_feedback().equalsIgnoreCase("1")){
//            holder.llMyTrainingPassportFragmentFeedback.setVisibility(View.GONE);
//        }else{
//            if (quizResultList.get(position).getFeedback().equalsIgnoreCase("1")){
//                holder.tvMyTrainingPassportFragmentFeedback.setVisibility(View.GONE);
//            }if (quizResultList.get(position).getPost_feedback().equalsIgnoreCase("1")){
//                holder.tvMyTrainingPassportFragmentPostFeedback.setVisibility(View.GONE);
//            }
//        }

    }

    @Override
    public int getItemCount() {
        return myTrainningPassportModels.size();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public class MyTrainingPasswordViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMyTrainingPassportFragmentCourseName)
        TextView tvMyTrainingPassportFragmentCourseName;
        @BindView(R.id.tvMyTrainingPassportFragmentQuizName)
        TextView tvMyTrainingPassportFragmentQuizName;
        @BindView(R.id.tvMyTrainingPassportFragmentQuizDate)
        TextView tvMyTrainingPassportFragmentQuizDate;
        @BindView(R.id.ivMyTrainingPassportFragmentQuizIcon)
        ImageView ivMyTrainingPassportFragmentQuizIcon;
        @BindView(R.id.tvMyTrainingPassportFragmentFeedback)
        TextView tvMyTrainingPassportFragmentFeedback;
        @BindView(R.id.tvMyTrainingPassportFragmentPostFeedback)
        TextView tvMyTrainingPassportFragmentPostFeedback;
        @BindView(R.id.llMyTrainingPassportFragmentFeedback)
        LinearLayout llMyTrainingPassportFragmentFeedback;
        @BindView(R.id.llCustomProgramDetailFragment)
        LinearLayout llCustomProgramDetailFragment;
        @BindView(R.id.tvStatus)
        TextView tvStatus;

        public MyTrainingPasswordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

//        @OnClick(R.id.tvMyTrainingPassportFragmentFeedback)
//        public void btnMyTrainingPassportFragmentFeedback() {
//            callback.myCallback(getAdapterPosition(), "feedback");
//        }
//
//        @OnClick(R.id.tvMyTrainingPassportFragmentPostFeedback)
//        public void btnMyTrainingPassportFragmentPostFeedback() {
//            callback.myCallback(getAdapterPosition(), "postFeedback");
//        }
//
//        @OnClick(R.id.llCustomProgramDetailFragment)
//        public void llCustomProgramDetailFragmentClick() {
//            callback.myCallback(getAdapterPosition(), "quizDetail");
//        }


    }
}

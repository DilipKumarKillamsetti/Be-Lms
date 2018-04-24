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
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.MyTrainningPassportModel;
import com.mahindra.be_lms.model.NominatedEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by android7 on 10/5/16.
 */
public class MyTrainingAdapter extends RecyclerView.Adapter<MyTrainingAdapter.MyTrainingPasswordViewHolder> {
    private final Context context;
    public List<NominatedEvent> myTrainningPassportModels;
    private Callback callback;

    public MyTrainingAdapter(Context context, List<NominatedEvent> myTrainningPassportModels) {
        this.context = context;
        this.myTrainningPassportModels = myTrainningPassportModels;
    }

    @Override
    public MyTrainingPasswordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_my_training_layout, parent, false);
        return new MyTrainingPasswordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyTrainingPasswordViewHolder holder, int position) {
        holder.tvMyTrainingPassportFragmentCourseName.setText(" "+myTrainningPassportModels.get(position).getEventName());
        holder.tvMyTrainingPassportFragmentQuizName.setText(myTrainningPassportModels.get(position).getEventdate());
        holder.tvStatus.setText(" "+myTrainningPassportModels.get(position).getStatus());
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
        @BindView(R.id.tvStatus)
        TextView tvStatus;

        public MyTrainingPasswordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setCourseList(ArrayList<NominatedEvent> nominatedEvents) {
            myTrainningPassportModels = nominatedEvents;
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

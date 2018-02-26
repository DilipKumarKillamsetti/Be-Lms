package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.Course;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chaitali on 10/5/16.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private final List<Course> courseList;
    private final Context context;
    int mExpandedPosition = -1;
    private Callback mCallback;

    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_course_layout, parent, false);
        return new CourseViewHolder(view);
    }

    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public void onBindViewHolder(final CourseViewHolder holder, int position) {
        holder.tvCourseFragmentTitle.setText(courseList.get(position).getCourse_title());
        // holder.tvCourseFragmentDesc.setText(courseList.get(position).getCourse_desc());

        final boolean isExpanded = position == mExpandedPosition;
        holder.custom_learn_quiztest_layout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }


    public class CourseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvCourseFragmentTitle)
        TextView tvCourseFragmentTitle;
        @BindView(R.id.tvCourseFragmentDesc)
        TextView tvCourseFragmentDesc;
        @BindView(R.id.custom_learn_quiztest_layout)
        LinearLayout custom_learn_quiztest_layout;

        public CourseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.tvCourseQuiz)
        public void onClickQuiz() {
            mCallback.myCallback(getAdapterPosition(), context.getString(R.string.quiz_tag));
        }

        @OnClick(R.id.tvCourseTestPaper)
        public void onClickTestpaper() {
            mCallback.myCallback(getAdapterPosition(), context.getString(R.string.test_paper_tag));
        }
    }
}

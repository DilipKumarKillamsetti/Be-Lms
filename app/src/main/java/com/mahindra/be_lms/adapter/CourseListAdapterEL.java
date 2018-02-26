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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.TechnicalUpload;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 9/21/2017.
 */

public class CourseListAdapterEL extends RecyclerView.Adapter<CourseListAdapterEL.CourseViewHolder> {

    private  List<CourseModel> courseList;
    private final Context context;
    private Callback mCallback;

    public CourseListAdapterEL(Context context, List<CourseModel> courseList) {
        this.context = context;
        this.courseList = courseList;
    }
    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public CourseListAdapterEL.CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_list_item_el, parent, false);
        return new CourseListAdapterEL.CourseViewHolder(view);
    }
    public void setCourseList(List<CourseModel> courseList) {
        this.courseList = courseList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final CourseListAdapterEL.CourseViewHolder holder, final int position) {

        holder.courseName.setText(courseList.get(position).getCourseName());


        holder.rl_course.setOnClickListener(new View.OnClickListener()
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
        return courseList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.courseName)
        TextView courseName;
        @BindView(R.id.rl_course)
        RelativeLayout rl_course;

        public CourseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }



    }
}

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

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseViewHolder> {

    private  List<CourseModel> courseList;
    private final Context context;
    private Callback mCallback;

    public CourseListAdapter(Context context, List<CourseModel> courseList) {
        this.context = context;
        this.courseList = courseList;
    }
    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public CourseListAdapter.CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_list_item, parent, false);
        return new CourseListAdapter.CourseViewHolder(view);
    }
    public void setCourseList(List<CourseModel> courseList) {
        this.courseList = courseList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final CourseListAdapter.CourseViewHolder holder, final int position) {

        holder.courseName.setText(courseList.get(position).getCourseName());
        holder.courseStatus.setText(courseList.get(position).getStatus());

        if(courseList.get(position).getCourseDescription().contains("<")){
            if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)){
                holder.courseDescription.setText(Html.fromHtml(courseList.get(position).getCourseDescription(), Html.FROM_HTML_MODE_COMPACT));

            }else{
                holder.courseDescription.setText(Html.fromHtml(courseList.get(position).getCourseDescription()));

            }
        }else{
            holder.courseDescription.setText(courseList.get(position).getCourseDescription());
        }

        holder.courseDescription.setMaxLines(1);
        holder.btnLearnTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCallback.myCallback(position, context.getString(R.string.quiz_tag));
            }
        });

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
        return courseList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
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

        public CourseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }



    }
}

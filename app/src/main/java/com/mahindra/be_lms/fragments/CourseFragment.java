package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.CourseAdapter;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.model.ApprovedProfile;
import com.mahindra.be_lms.model.Course;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Chaitali Chavan on 14/11/16.
 */
public class CourseFragment extends Fragment implements Callback {
    public static final String TAG = CourseFragment.class.getSimpleName();
    @BindView(R.id.rvCourseFragment)
    RecyclerView rvLearnTestQuizFragment;
    @BindView(R.id.tvCourseFragmentNoRecordFound)
    TextView tvCourseFragmentNoRecordFound;
    private ApprovedProfile approvedProfile;
    private MainActivity mainActivity;
    private List<Course> courseList;

    public CourseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        // courseList = DataProvider.getDummyCourseList();
        Bundle bundle = getArguments();
        if (bundle != null) {
            approvedProfile = new ApprovedProfile();
            approvedProfile = bundle.getParcelable(getString(R.string.profile_parceable_tag));
            assert approvedProfile != null;
            L.l(TAG, "PROFILE OBJECT: " + approvedProfile.toString());
            String course_array = approvedProfile.getProfile_json();
            L.l(TAG, "COURSE JSON ARRAY STRING LIST: " + course_array.toString());
            try {
                //[{"coursename":"SKILL TEST L0","courseid":"352"}]}
                JSONArray jsonArray = new JSONArray(course_array);
                courseList = new ArrayList<>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    Course course = new Course();
                    course.setId(jsonArray.getJSONObject(j).getString("courseid"));
                    course.setCourse_title(jsonArray.getJSONObject(j).getString("coursename"));
                    courseList.add(course);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (courseList.isEmpty()) {
                tvCourseFragmentNoRecordFound.setVisibility(View.VISIBLE);
            } else {
                tvCourseFragmentNoRecordFound.setVisibility(View.GONE);
            }
            rvLearnTestQuizFragment.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvLearnTestQuizFragment.setHasFixedSize(true);
            rvLearnTestQuizFragment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            CourseAdapter adapter = new CourseAdapter(getActivity(), courseList);
            adapter.setmCallback(this);
            rvLearnTestQuizFragment.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setTitle("Courses");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void myCallback(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.course_parcelable_tag), courseList.get(position));
        QuizTestPaperFragment quizTestPaperFragment = new QuizTestPaperFragment();
        quizTestPaperFragment.setArguments(bundle);
        mainActivity.replaceFrgament(quizTestPaperFragment);
    }

    @Override
    public void myCallback(int position, String tag) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.course_parcelable_tag), courseList.get(position));
        if (tag.equalsIgnoreCase(getString(R.string.test_paper_tag))) {
            TestPaperFragment testPaperFragment = new TestPaperFragment();
            testPaperFragment.setArguments(bundle);
            mainActivity.replaceFrgament(testPaperFragment);
        } else if (tag.equalsIgnoreCase(getString(R.string.quiz_tag))) {
            QuizFragment quizFragment = new QuizFragment();
            quizFragment.setArguments(bundle);
            mainActivity.replaceFrgament(quizFragment);
        }
    }
}

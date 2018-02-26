package com.mahindra.be_lms.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.NetworkUtil;
import com.mahindra.be_lms.model.Course;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.volley.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuizTestPaperFragment extends Fragment implements NetworkMethod {
    private static final String TAG = QuizTestPaperFragment.class.getSimpleName();
    @BindView(R.id.tvQuizTestPaperFragmentRecordNotFound)
    TextView tvQuizTestPaperFragmentRecordNotFound;
    //http://192.168.1.19/powerol_lms/webservice/rest/server.php?wstoken=b5a3ccd8331a2ed6e9def6b84e8a52ef&wsfunction=core_course_get_contents&courseid=9&moodlewsrestformat=json
    private Course course;

    public QuizTestPaperFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_test_paper, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            course = bundle.getParcelable(getString(R.string.course_parcelable_tag));
            assert course != null;
            L.l("Course: " + course.toString());
            try {
                if (NetworkUtil.getConnectivityStatus(getActivity()) != 0) {
                        request("http://192.168.1.19/powerol_lms/webservice/rest/server.php");
                } else {
                    L.t_long(getString(R.string.err_network_connection));
                }
            } catch (Exception e) {
                e.printStackTrace();
                tvQuizTestPaperFragmentRecordNotFound.setVisibility(View.VISIBLE);
            }
        } else {
            tvQuizTestPaperFragmentRecordNotFound.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void request(String url) {
        L.pd("Fetching data", getString(R.string.dialog_please_wait), getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                L.l(TAG, "COURSE DOC FETCH RESPONSE: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(TAG, "VOLLEY ERROR: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("wsfunction", "core_course_get_contents");
                params.put("wstoken", Constants.MOODLE_TOKEN);
                params.put("courseid", course.getId());
                params.put("moodlewsrestformat", "json");
                L.l(TAG, "QUIZ, TEST PAPER PARAMS: " + params.toString());
                return VolleySingleton.checkRequestparam(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }
}

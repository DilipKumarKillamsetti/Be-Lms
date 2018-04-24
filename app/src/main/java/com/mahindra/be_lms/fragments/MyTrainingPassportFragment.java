package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.MyTrainingPassportAdapter;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.MyTrainningPassportModel;
import com.mahindra.be_lms.model.QuizResult;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.CustomProgressDialog;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyTrainingPassportFragment extends Fragment implements Callback, NetworkMethod {
    private static final String TAG = MyTrainingPassportFragment.class.getSimpleName();
    @BindView(R.id.rvMyTrainingPassportFragment)
    RecyclerView rvMyTrainingPassportFragment;
      @BindView(R.id.tvMyTrainingPassportFragmentRecordNotFound)
     TextView tvMyTrainingPassportFragmentRecordNotFound;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.btnRetry)
    Button btnRetry;
//    private List<QuizResult> quizResultList;
    /*@BindView(R.id.btnTraningPassportOption1)
    Button btnTraningPassportOption1;
    @BindView(R.id.btnTraningPassportOption2)
    Button btnTraningPassportOption2;
    @BindView(R.id.btnTraningPassportOption3)
    Button btnTraningPassportOption3;
    @BindView(R.id.btnTraningPassportOption4)
    Button btnTraningPassportOption4;
    @BindView(R.id.btnTraningPassportOption5)
    Button btnTraningPassportOption5;*/
    private DashboardActivity mainActivity;
    private MyTrainingPassportAdapter adapter;
    private AlertDialog builder;
    private ArrayList<MyTrainningPassportModel> myTrainningPassportModels;
    private ProgressDialog progressDialog;
    public MyTrainingPassportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_training_passport, container, false);
        ButterKnife.bind(this, view);
        try {
            mainActivity.getSupportActionBar().setTitle("");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        init();
        // Inflate the layout for this fragment
        return view;
    }

    private void init() {
        myTrainningPassportModels = new ArrayList<>();
        rvMyTrainingPassportFragment.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvMyTrainingPassportFragment.setHasFixedSize(true);
        //rvMyTrainingPassportFragment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        callApi();
    }

    private void callApi() {

        if (L.isNetworkAvailable(getActivity())) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rvMyTrainingPassportFragment.setVisibility(View.VISIBLE);
            }
            request(Constants.BE_LMS_ROOT_URL+ MyApplication.mySharedPreference.getUserToken()+"&wsfunction=custommobwebsrvices_mypassport&moodlewsrestformat=json");
        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            rvMyTrainingPassportFragment.setVisibility(View.GONE);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callApi();
                }
            });
        }

    }

    /*@OnClick({R.id.btnTraningPassportOption1,R.id.btnTraningPassportOption2,R.id.btnTraningPassportOption3,R.id.btnTraningPassportOption4,R.id.btnTraningPassportOption5})
    public void btnClick(View v){
        //startActivity(new Intent(getActivity(), ComingSoonActivity.class));
        Bundle bundle = new Bundle();
        MyTrainingPassportWebViewFragment myTrainingPassportWebViewFragment = new MyTrainingPassportWebViewFragment();
        switch (v.getId()){
            case R.id.btnTraningPassportOption1:
                bundle.putString("flag", "1");
                myTrainingPassportWebViewFragment.setArguments(bundle);
                mainActivity.replaceFrgament(myTrainingPassportWebViewFragment);
                break;
            case R.id.btnTraningPassportOption2:
                bundle.putString("flag", "2");
                myTrainingPassportWebViewFragment.setArguments(bundle);
                mainActivity.replaceFrgament(myTrainingPassportWebViewFragment);
                break;
            case R.id.btnTraningPassportOption3:
                bundle.putString("flag", "3");
                myTrainingPassportWebViewFragment.setArguments(bundle);
                mainActivity.replaceFrgament(myTrainingPassportWebViewFragment);
                break;
            case R.id.btnTraningPassportOption4:
                bundle.putString("flag", "4");
                myTrainingPassportWebViewFragment.setArguments(bundle);
                mainActivity.replaceFrgament(myTrainingPassportWebViewFragment);
                break;
            case R.id.btnTraningPassportOption5:
                bundle.putString("flag", "5");
                myTrainingPassportWebViewFragment.setArguments(bundle);
                mainActivity.replaceFrgament(myTrainingPassportWebViewFragment);
                break;
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (DashboardActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        try {
            mainActivity.getSupportActionBar().setTitle("");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void request(String url) {
       progressDialog = new CustomProgressDialog(getActivity(),"");
        progressDialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressDialog.dismiss();
                        try {
                            updateDisplay(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                L.l(TAG, "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) ;
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(jsonArrayRequest);
    }

    private void updateDisplay(JSONArray response) throws JSONException {
        if (!TextUtils.isEmpty(response.toString())) {
            JSONObject jsonObject = (JSONObject) response.get(0) ;
            if (jsonObject.getString("Status").equalsIgnoreCase("success")) {
                JSONArray quizResultArray = jsonObject.getJSONArray("completedquizes");
                if (quizResultArray.length() > 0) {
                    for (int i = 0; i < quizResultArray.length(); i++) {
                        MyTrainningPassportModel quizResult = new Gson().fromJson(String.valueOf(quizResultArray.get(i)), MyTrainningPassportModel.class);
                        myTrainningPassportModels.add(quizResult);
                    }
                     adapter = new MyTrainingPassportAdapter(getActivity(), myTrainningPassportModels);
                     adapter.setCallback(this);
                     rvMyTrainingPassportFragment.setAdapter(adapter);
                } else {
                     tvMyTrainingPassportFragmentRecordNotFound.setVisibility(View.VISIBLE);
                }
            } else {
                tvMyTrainingPassportFragmentRecordNotFound.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public void myCallback(int position) {
    }

    @Override
    public void myCallback(int position, String tag) {
        if (tag.equalsIgnoreCase("feedback")) {
            //  L.t(quizResultList.get(position).getCourse() + " Feedback call");
            //  startActivity(new Intent(getActivity(), QuizResultFeedback.class).putExtra("feedback_type", "1").putExtra("courseid", quizResultList.get(position).getCourseid()));
        } else if (tag.equalsIgnoreCase("postFeedback")) {
            //L.t(quizResultList.get(position).getCourse() + " Post Feedback call");
            //   startActivity(new Intent(getActivity(), QuizResultFeedback.class).putExtra("feedback_type", "2").putExtra("courseid", quizResultList.get(position).getCourseid()));
        } else if (tag.equalsIgnoreCase("quizDetail")) {
            View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.custom_quiz_result_dialog_layout, null, false);
            TextView tvMyTrainingPassportFragmentQuizName = (TextView) dialogView.findViewById(R.id.tvMyTrainingPassportFragmentQuizName);
            TextView tvMyTrainingPassportFragmentAttemptNo = (TextView) dialogView.findViewById(R.id.tvMyTrainingPassportFragmentAttemptNo);
            TextView tvMyTrainingPassportFragmentQuizDate = (TextView) dialogView.findViewById(R.id.tvMyTrainingPassportFragmentQuizDate);
            TextView tvMyTrainingPassportFragmentQuizPercentage = (TextView) dialogView.findViewById(R.id.tvMyTrainingPassportFragmentQuizPercentage);
            //  tvMyTrainingPassportFragmentQuizName.setText(quizResultList.get(position).getCourse());
            //   tvMyTrainingPassportFragmentAttemptNo.setText(quizResultList.get(position).getQuizResultDetails().getAttempt());
            //   tvMyTrainingPassportFragmentQuizDate.setText(quizResultList.get(position).getCdate());
            // String showPercentage = quizResultList.get(position).getQuizResultDetails().getPercentage().substring(0, quizResultList.get(position).getQuizResultDetails().getPercentage().lastIndexOf("."));
            //  tvMyTrainingPassportFragmentQuizPercentage.setText(showPercentage + "%");
            dialogView.findViewById(R.id.btnCloseQuizDialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                }
            });
            builder = new AlertDialog.Builder(getActivity())
                    .setView(dialogView)
                    .setCancelable(false)
                    .show();

        }
    }

    @Override
    public void myCallback(int position, String tag, String id, String action) {

    }

}

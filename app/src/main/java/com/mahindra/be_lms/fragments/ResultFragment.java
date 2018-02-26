package com.mahindra.be_lms.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.QuestionActivity;
import com.mahindra.be_lms.adapter.CourseListAdapter;
import com.mahindra.be_lms.adapter.QuizResultAdapter;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.QuizDataModel;
import com.mahindra.be_lms.model.QuizResultModel;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 9/27/2017.
 */

public class ResultFragment extends Fragment implements Callback,NetworkMethod,View.OnClickListener {
    private static final String TAG = ResultFragment.class.getSimpleName();
    @BindView(R.id.rvResultList)
    RecyclerView rvResultList;
    @BindView(R.id.quizName)
    TextView quizName;
    @BindView(R.id.returnAttempt)
    Button returnAttempt;
    @BindView(R.id.quizDescription)
    TextView quizDescription;
    @BindView(R.id.gradingMethod)
    TextView gradingMethod;
    @BindView(R.id.tvNoRecordFound)
    TextView tvNoRecordFound;
    @BindView(R.id.dataListCard)
    CardView dataListCard;
//    @BindView(R.id.marks)
//    TextView marks;
    @BindView(R.id.grades)
    TextView grades;
    private  QuizDataModel quizDataModel;
    private QuizResultAdapter quizResultAdapter;
    private MainActivity mainActivity;
    String [] gradeList = {"","Highest Grade","Average Grade","First Attempt","Last Attempt"};
    private ArrayList<QuizResultModel> quizResultModels;
    public ResultFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;
        try {
            view = inflater.inflate(R.layout.qiuz_result_fragment, container, false);
            ButterKnife.bind(this, view);
            setHasOptionsMenu(false);
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void init() {
        mainActivity.setDrawerEnabled(false);
        quizResultModels = new ArrayList<>();
        returnAttempt.setOnClickListener(this);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            quizDataModel = bundle.getParcelable("quizData");
            quizName.setText(quizDataModel.getQuizTitle());
            if(quizDataModel.getQuizDesc().contains("<")){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    quizDescription.setText(Html.fromHtml(quizDataModel.getQuizDesc(), Html.FROM_HTML_MODE_COMPACT));
                }else{
                    quizDescription.setText(Html.fromHtml(quizDataModel.getQuizDesc()));
                }


            }else{
                quizDescription.setText(quizDataModel.getQuizDesc());
            }

            gradingMethod.setText("Grading Method: "+gradeList[Integer.parseInt(quizDataModel.getGradeMethod())]);
            //marks.setText("Marks/"+quizDataModel.getSumGrades());
            grades.setText("Grades/"+quizDataModel.getGrade());



                if (L.isNetworkAvailable(getActivity())) {

                    L.pd(getString(R.string.dialog_please_wait), getActivity());
                    request(Constants.BE_LMS_ROOT_URL+MyApplication.mySharedPreference.getUserToken()+"&wsfunction=mod_quiz_get_user_attempts&quizid="+quizDataModel.getQuizID()+"&userid="+MyApplication.mySharedPreference.getUserId()+"&status=all&includepreviews=1&moodlewsrestformat=json");

                } else {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText(getString(R.string.err_network_connection))
                            .setConfirmText("OK")
                            .show();
                }


        }

        rvResultList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvResultList.setHasFixedSize(true);
        //rvLearnTestQuizFragment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        quizResultAdapter = new QuizResultAdapter(getActivity(),quizResultModels);
        quizResultAdapter.setmCallback(this);
        rvResultList.setAdapter(quizResultAdapter);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }


    @Override
    public void myCallback(int position) {
        mainActivity.replaceFrgament(new QuizListFragment());
    }

    @Override
    public void myCallback(int position, String tag) {
        replaceFrgament(new PreviewAnswerFragment(),quizResultModels.get(position).getID());
    }


    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: call");
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            try {
                mainActivity.getSupportActionBar().show();
                mainActivity.getSupportActionBar().setTitle("");
//                if (adapter != null) {
//                    Log.d(TAG, "onResume: adapternot null");
//                    queriesList = new DBHelper().getQueriesList();
//                    adapter = new QueriesAdapter(getActivity(), queriesList);
//                    adapter.setMyCallback(this);
//                    rvMyQueriesFragmentList.setAdapter(adapter);
//                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void request(String url) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.e("_++_+_+_",response.toString());
                            if(response.getString("Status").equalsIgnoreCase("Success")){
                                JSONArray jsonArray = response.getJSONArray("attempts");
                                if(jsonArray.length() == 0){
                                    returnAttempt.setText("Attempt quiz now");
                                    dataListCard.setVisibility(View.GONE);
                                    tvNoRecordFound.setVisibility(View.VISIBLE);
                                }else{
                                    dataListCard.setVisibility(View.VISIBLE);
                                    tvNoRecordFound.setVisibility(View.GONE);
                                    returnAttempt.setText("Continue the last attempt");
                                    for(int i = 0 ; i<jsonArray.length();i++){
                                        QuizResultModel quizResultModel = new Gson().fromJson(String.valueOf(jsonArray.get(i)), QuizResultModel.class);
                                        quizResultModels.add(quizResultModel);
                                    }
                                    if (quizResultModels.size() > 0) {
                                        quizResultAdapter.setAttemptsList(quizResultModels);
                                        quizResultAdapter.notifyDataSetChanged();
                                    } else {
                                         tvNoRecordFound.setVisibility(View.VISIBLE);
                                        dataListCard.setVisibility(View.GONE);
                                    }
                                }


                            }else{
                                //tvNoRecordFound.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        L.dismiss_pd();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(getActivity(), "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) ;
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(jsonObjReq);
    }


    public void replaceFrgament(Fragment fragment,String attemptId) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
        Bundle args = new Bundle();
        args.putString("id",attemptId);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.myContainer, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.returnAttempt:
                if(returnAttempt.getText().toString().equalsIgnoreCase("Attempt quiz now")){

                    if (L.isNetworkAvailable(getActivity())) {

                        L.pd(getString(R.string.dialog_please_wait), getActivity());
                        startQuiz(Constants.BE_LMS_ROOT_URL+MyApplication.mySharedPreference.getUserToken()+"&wsfunction=mod_quiz_start_attempt&quizid="+quizDataModel.getQuizID()+"&forcenew=1&moodlewsrestformat=json");

                    } else {
                        new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                .setContentText(getString(R.string.err_network_connection))
                                .setConfirmText("OK")
                                .show();
                    }

                }else{

                    Intent i = new Intent(getActivity(),QuestionActivity.class);
                    i.putExtra("state","continue");
                    i.putExtra("attemptId",quizResultModels.get(0).getID());
                    startActivity(i);

                }
                break;
            default:
                break;
        }

    }

    private void startQuiz(String url) {

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.e("_++_+_+_",response.toString());
                            if(response.getString("Status").equalsIgnoreCase("Success")){
                                JSONObject data = response.getJSONObject("attempt");
                                Intent i = new Intent(getActivity(), QuestionActivity.class);
                                startActivity(i);

                            }else{
                                //tvNoRecordFound.setVisibility(View.VISIBLE);
                                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                        .setContentText(response.getString("message")).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        L.dismiss_pd();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                L.l(getActivity(), "ERROR : " + error.getMessage());
                if (L.checkNull(error.getMessage())) {
                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                            .setContentText("Network problem please try again").show();
                }
            }
        }) ;
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(jsonObjReq);
    }
}


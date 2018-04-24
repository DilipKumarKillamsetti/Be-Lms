package com.mahindra.be_lms.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.CourseListAdapter;
import com.mahindra.be_lms.adapter.QuizListAdapter;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.CourseModel;
import com.mahindra.be_lms.model.QuizDataModel;
import com.mahindra.be_lms.model.QuizModel;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 9/22/2017.
 */

public class QuizListFragment extends Fragment implements Callback,NetworkMethod {
    private static final String TAG = QuizListFragment.class.getSimpleName();
    @BindView(R.id.quiz_list)
    RecyclerView quiz_list;
    @BindView(R.id.tvNoRecordFound)
    TextView tvNoRecordFound;
    String id;
    public QuizListAdapter quizListAdapter;
    private MainActivity mainActivity;
    private ArrayList<QuizDataModel> quizModels;
    public QuizListFragment(){
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        init();
        return view;
    }

    private void init() {
        mainActivity.setDrawerEnabled(false);
        id = getArguments().getString("id");
        if (L.isNetworkAvailable(getActivity())) {

            L.pd(getString(R.string.dialog_please_wait), getActivity());
            request(Constants.BE_LMS_ROOT_URL+MyApplication.mySharedPreference.getUserToken()+"&wsfunction=mod_quiz_get_quizzes_by_courses&courseids[0]="+id+"&moodlewsrestformat=json");

        } else {
            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.err_network_connection))
                    .setConfirmText("OK")
                    .show();
        }

        quizModels = new ArrayList<>();

        quiz_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        quiz_list.setHasFixedSize(true);
        quizListAdapter = new QuizListAdapter(getActivity(),quizModels);
        quizListAdapter.setmCallback(this);
        quiz_list.setAdapter(quizListAdapter);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
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
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void myCallback(int position) {

    }

    @Override
    public void myCallback(int position, String tag) {
       // mainActivity.replaceFrgament(new FragmentQuizDescription());
        //mainActivity.replaceFrgament(new QuizQuestionListFragment());
        //replaceFrgament(new ResultFragment(),quizModels.get(position));
        replaceFrgament(new LearnTestQuizFragment(),quizModels.get(position));
        //LearnTestQuizFragment()
    }

    @Override
    public void myCallback(int position, String tag, String id, String action) {

    }


    @Override
    public void request(String url) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

            try {

                if(response.getString("Status").equalsIgnoreCase("Success")){
                    JSONArray jsonArray = response.getJSONArray("quizzes");
                    for(int i = 0 ; i<jsonArray.length();i++){
                        QuizDataModel quizDataModel = new Gson().fromJson(String.valueOf(jsonArray.get(i)), QuizDataModel.class);
                        quizModels.add(quizDataModel);
                    }
                    if (quizModels.size() > 0) {
                        quizListAdapter.setCourseList(quizModels);
                        quizListAdapter.notifyDataSetChanged();
                    } else {
                        tvNoRecordFound.setVisibility(View.VISIBLE);
                    }

                }else{
                    tvNoRecordFound.setVisibility(View.VISIBLE);
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


    public void replaceFrgament(Fragment fragment,QuizDataModel quizDataModel) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
        Bundle args = new Bundle();
        args.putParcelable("quizData",quizDataModel);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.myContainer, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

}

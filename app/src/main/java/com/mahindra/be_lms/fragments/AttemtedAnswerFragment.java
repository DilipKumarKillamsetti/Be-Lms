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
        import android.widget.Button;

        import com.mahindra.be_lms.MyApplication;
        import com.mahindra.be_lms.R;
        import com.mahindra.be_lms.activities.LoginActivity;
        import com.mahindra.be_lms.activities.MainActivity;
        import com.mahindra.be_lms.adapter.AttemptedAnswerAdapter;
        import com.mahindra.be_lms.adapter.CourseListAdapter;
        import com.mahindra.be_lms.model.AttemptModel;
        import com.mahindra.be_lms.model.CourseModel;
        import com.mahindra.be_lms.model.QuizResult;

        import java.util.ArrayList;

        import butterknife.BindView;
        import butterknife.ButterKnife;

/**
 * Created by Dell on 9/26/2017.
 */

public class AttemtedAnswerFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = AttemtedAnswerFragment.class.getSimpleName();
    @BindView(R.id.rvAttemptedAnswerList)
    RecyclerView rvAttemptedAnswerList;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;


    private AttemptedAnswerAdapter attemptedAnswerAdapter;
    private MainActivity mainActivity;
    private ArrayList<AttemptModel> attemptModels = new ArrayList<>();

    public AttemtedAnswerFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_answer_status, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        init();
        return view;
    }

    private void init() {
        mainActivity.setDrawerEnabled(false);
        btnSubmit.setOnClickListener(this);

        for(int i = 0 ; i < 4 ; i++){

            AttemptModel attemptModel = new AttemptModel();
            attemptModel.setId(""+i);
            attemptModel.setStatus("Answer Attempted");
            attemptModels.add(attemptModel);
        }
        rvAttemptedAnswerList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvAttemptedAnswerList.setHasFixedSize(true);
        //rvLearnTestQuizFragment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        attemptedAnswerAdapter = new AttemptedAnswerAdapter(getActivity(),attemptModels);
       // attemptedAnswerAdapter.setmCallback(this);
        rvAttemptedAnswerList.setAdapter(attemptedAnswerAdapter);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }


//    @Override
//    public void myCallback(int position) {
//        mainActivity.replaceFrgament(new QuizListFragment());
//    }
//
//    @Override
//    public void myCallback(int position, String tag) {
//        mainActivity.replaceFrgament(new QuizListFragment());
//    }


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
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btnSubmit:

                mainActivity.replaceFrgament(new ResultFragment());

                break;
            default:
                break;
        }

    }
}

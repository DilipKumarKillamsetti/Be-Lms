package com.mahindra.be_lms.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.QueryResponseAdapter;
import com.mahindra.be_lms.adapter.SurveyAdapter;
import com.mahindra.be_lms.db.QueryResponse;
import com.mahindra.be_lms.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 2/22/2018.
 */

public class CommentsActivityDialog extends AppCompatActivity {

    private QueryResponseAdapter queryResponseAdapter;
        @BindView(R.id.rvcommentResponse)
        RecyclerView rvcommentResponse;
    private List<QueryResponse> queryResponseList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_comments);
        ButterKnife.bind(this);
        init();
    }

    public void init(){

        queryResponseList = new ArrayList<>();
        QueryResponse queryResponse = new QueryResponse();
        queryResponse.setMsg_type("response");
        queryResponse.setMessage("Hi How are you.");
        queryResponseList.add(queryResponse);
        rvcommentResponse.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvcommentResponse.setHasFixedSize(true);
        //rvcommentResponse.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        queryResponseAdapter = new QueryResponseAdapter(this, queryResponseList,100);
        rvcommentResponse.setAdapter(queryResponseAdapter);
       // tvQueryResponseNotFound.setVisibility(View.GONE);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

}

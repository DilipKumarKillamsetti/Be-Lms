package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.mahindra.be_lms.activities.MyQueryDetailActivity;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.QueriesAdapter;
import com.mahindra.be_lms.model.Queries;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.QuizDataModel;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyQueriesFragment extends Fragment implements MyCallback ,NetworkMethod {
    private static final String TAG = MyQueriesFragment.class.getSimpleName();
    @BindView(R.id.rvMyQueriesFragmentList)
    RecyclerView rvMyQueriesFragmentList;
    @BindView(R.id.tvMyQueriesFragmentRecordNotFound)
    TextView tvMyQueriesFragmentRecordNotFound;
    private List<Queries> queriesList;
    private QueriesAdapter adapter;
    private MainActivity mainActivity;
    @BindView(R.id.retryButtonLayout)
    LinearLayout retryButtonLayout;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    @BindView(R.id.floatingbtnAddQuery)
    FloatingActionButton floatingbtnAddQuery;
    public MyQueriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_queries, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        init();
        return view;
    }

    private void init() {
        mainActivity.setDrawerEnabled(false);
        queriesList = new ArrayList<>();
            rvMyQueriesFragmentList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvMyQueriesFragmentList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            rvMyQueriesFragmentList.setHasFixedSize(true);
            adapter = new QueriesAdapter(getActivity(), queriesList);
            adapter.setMyCallback(this);
            rvMyQueriesFragmentList.setAdapter(adapter);
            callApi();

    }

    private void callApi() {
        if (L.isNetworkAvailable(getActivity())) {
            if (retryButtonLayout.getVisibility() == View.VISIBLE) {
                retryButtonLayout.setVisibility(View.GONE);
                rvMyQueriesFragmentList.setVisibility(View.VISIBLE);
                floatingbtnAddQuery.setVisibility(View.VISIBLE);
            }
            L.pd(getString(R.string.dialog_please_wait), getActivity());
            request(Constants.BE_LMS_ROOT_URL+MyApplication.mySharedPreference.getUserToken()+"&wsfunction=custommobwebsrvices_viewproblem&userid="+MyApplication.mySharedPreference.getUserId()+"&moodlewsrestformat=json");

        } else {
            retryButtonLayout.setVisibility(View.VISIBLE);
            rvMyQueriesFragmentList.setVisibility(View.GONE);
            floatingbtnAddQuery.setVisibility(View.GONE);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callApi();
                }
            });

        }
    }

    @Override
    public void myCallback(int position) {
        L.l("Callback call myQuery");
        Queries queries = queriesList.get(position);
        L.l("CALLBACK QUERY " + queries.toString());
        startActivity(new Intent(getActivity(), MyQueryDetailActivity.class).putExtra("test", queries));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) mainActivity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(mainActivity.getComponentName()));
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }

    @OnClick({R.id.floatingbtnAddQuery})
    public void OnClickbtnQuery(View v) {
        L.l("btnclick");
        mainActivity.replaceFrgament(new QueryFragment());
    }

    @Override
    public void request(String url) {
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        L.dismiss_pd();
                        try {
                            if(response.length() != 0){
                                JSONObject quiriesObject = (JSONObject) response.get(0);
                                if(quiriesObject.getString("Status").equalsIgnoreCase("Success")){
                                    JSONArray jsonArray = quiriesObject.getJSONArray("queries");
                                    for(int i = 0 ; i<jsonArray.length();i++){
                                        Queries queries = new Gson().fromJson(String.valueOf(jsonArray.get(i)), Queries.class);
                                        queriesList.add(queries);
                                    }
                                    if (queriesList.size() > 0) {
                                        adapter.setQueriesList(queriesList);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        tvMyQueriesFragmentRecordNotFound.setVisibility(View.VISIBLE);
                                    }
                                    }else{
                                    tvMyQueriesFragmentRecordNotFound.setVisibility(View.VISIBLE);
                                }
                            }else{
                                tvMyQueriesFragmentRecordNotFound.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        L.dismiss_pd();


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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(jsonObjReq);
    }
}

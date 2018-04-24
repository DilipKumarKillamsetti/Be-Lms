package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.ProductAdapter;
import com.mahindra.be_lms.db.Product;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.interfaces.NetworkMethod;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.DateManagement;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductFragment extends Fragment implements Callback, NetworkMethod {
    private static final String TAG = "ProductFragment";
    @BindView(R.id.product_swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rvHomeFragmentProductList)
    RecyclerView rvHomeFragmentProductList;
    @BindView(R.id.tvProductFragmentRNF)
    TextView tvProductFragmentRNF;
    private MainActivity mainActivity;
    private List<Product> productList;

    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        ButterKnife.bind(this, view);
        // setHasOptionsMenu(true);
        init();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //call webserive default only first open this fragment
        Log.d(TAG, "onCreate: ");
        if (L.isNetworkAvailable(getActivity())) {
            request(Constants.LMS_URL);
        } else {
            Log.d(TAG, "onCreate: Network Connection not available");
            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                    .setContentText(getString(R.string.err_network_connection))
                    .setConfirmText("OK")
                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                        @Override
                        public void onClick(PKBDialog customDialog) {
                            customDialog.dismiss();
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }).show();
        }

    }

    private void init() {
        mainActivity.setDrawerEnabled(false);
        swipeRefreshLayout.setPadding(0, 0, 0, 0);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.colorPrimary);
        productList = new ArrayList<>();
        productList = new DBHelper().getProductList();
        L.l("ProductList size ; " + productList.size());
        rvHomeFragmentProductList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvHomeFragmentProductList.setHasFixedSize(true);
        loadProduct(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (L.isNetworkAvailable(getActivity())) {
                    request(Constants.LMS_URL);
                } else {
                    L.t("Please check network connectivity");
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setTitle("");
            if (!MyApplication.mySharedPreference.checkUserLogin()) {
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }

    private void loadProduct(boolean onCreate) {
        Log.d(TAG, "loadProduct: Method call");
        productList = new DBHelper().getProductList();
        if (productList.size() > 0) {
            Collections.sort(productList, new Comparator<Product>() {
                @Override
                public int compare(Product product1, Product product2) {
                    return Integer.parseInt(product1.getProductSequence()) > Integer.parseInt(product2.getProductSequence()) ? 1 : Integer.parseInt(product1.getProductSequence()) < Integer.parseInt(product2.getProductSequence()) ? -1 : 0;
                }
            });
            ProductAdapter adapter = new ProductAdapter(getActivity(), productList);
            adapter.setmCallback(this);
            rvHomeFragmentProductList.setAdapter(adapter);
        }
//        else{
//            if (!onCreate) {
//                new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                        .setContentText("Network problem please try again")
//                        .setConfirmText("OK")
//                        .setCancelText("Cancel")
//                        .setCancelClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                            @Override
//                            public void onClick(PKBDialog customDialog) {
//                                customDialog.dismiss();
//                                getActivity().getSupportFragmentManager().popBackStack();
//                            }
//                        })
//                        .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                            @Override
//                            public void onClick(PKBDialog customDialog) {
//                                customDialog.dismiss();
//                                if (L.isNetworkAvailable(getActivity())) {
//                                    request(Constants.LMS_URL);
//                                } else {
//                                    Log.d(TAG, "onCreate: Network Connection not available");
//                                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
//                                            .setContentText(getString(R.string.err_network_connection))
//                                            .setConfirmText("OK")
//                                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
//                                                @Override
//                                                public void onClick(PKBDialog customDialog) {
//                                                    customDialog.dismiss();
//                                                    getActivity().getSupportFragmentManager().popBackStack();
//                                                }
//                                            }).show();
//                                }
//                            }
//                        }).show();
//            }
//        }

    }

    @Override
    public void myCallback(int position) {
    }

    @Override
    public void myCallback(int position, String tag) {
        Product product = productList.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.product_parcelabel_tag), product);
        if (tag.equalsIgnoreCase("Question")) {
            QueryFragment queryFragment = new QueryFragment();
            queryFragment.setArguments(bundle);
            mainActivity.replaceFrgament(queryFragment);
        } else {
            //preference.setProductHits();
            ModelFragment modelFragment = new ModelFragment();
            modelFragment.setArguments(bundle);
            mainActivity.replaceFrgament(modelFragment);
        }
    }

    @Override
    public void myCallback(int position, String tag, String id, String action) {

    }

    @Override
    public void request(String url) {
        Log.d(TAG, "request: Method Call");
        L.pd("Please Wait", getActivity());
        final String master_sync_date = MyApplication.mySharedPreference.getPrefisString(MySharedPreference.MASTER_SYNC_DATE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.dismiss_pd();
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Log.d(TAG, "onResponse: Method Call");
                L.l(getActivity(), "SYNC response: " + response.toString());
                updateDisplay(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.dismiss_pd();
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Log.d(TAG, "onErrorResponse: Method Call");
                L.l(getActivity(), "Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    if (error.getMessage().equalsIgnoreCase("null")) {
                        L.t("Something went wrong try again or check network connection");
                    }
                } else {
                    L.t("Something went wrong try again or check network connection");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("action", Constants.MASTER_DATA_ACTION);
                param.put("userid", MyApplication.mySharedPreference.getUserId());
                param.put("date", TextUtils.isEmpty(master_sync_date) ? "10-10-2011" : master_sync_date);
                param = VolleySingleton.checkRequestparam(param);
                L.l(getActivity(), "params: " + param.toString());
                return param;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(stringRequest);
    }

    private void updateDisplay(String response) {
        Log.d(TAG, "updateDisplay: Method Call");
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                Log.d(TAG, "updateDisplay: Data Recived");
                new MasterSyncTask().execute(response);
            } else {
                L.t("" + jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            L.t_long(getString(R.string.err_server_site));
        }
    }

    private class MasterSyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            L.pd("Updating data", getActivity());
        }

        @Override
        protected Void doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(params[0]);
                new DBHelper().sync_Master(jsonObject);
                MyApplication.mySharedPreference.putPref(MySharedPreference.MASTER_SYNC_DATE, DateManagement.getCurrentDate());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            L.dismiss_pd();
            Log.d(TAG, "onPostExecute: Method Call");
            loadProduct(false);
        }
    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        getActivity().getMenuInflater().inflate(R.menu.main, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.menu_search) {
//            startActivity(new Intent(getActivity(), SearchActivity.class));
//            return true;
//        } else if (id == R.id.menu_sync) {
//            startActivity(new Intent(getActivity(), SyncActivity.class));
//            return true;
//        } else if (id == R.id.menu_disclaimer) {
//            startActivity(new Intent(getActivity(), DisclaimerActivity.class));
//            return true;
//        } else if (id == R.id.menu_safety) {
//            startActivity(new Intent(getActivity(), SafetyAndWarningActivity.class));
//            return true;
//        } else if (id == R.id.menu_notification) {
//            startActivity(new Intent(getActivity(), NotificationActivity.class));
//            return true;
//        } else if (id == R.id.menu_about_us) {
//            startActivity(new Intent(getActivity(), AboutUsActivity.class).putExtra("about", "about"));
//            return true;
//        } else if (id == R.id.menu_feedback) {
//            startActivity(new Intent(getActivity(), FeedbackActivity.class));
//            return true;
//        } else if (id == R.id.menu_contact_us) {
//            startActivity(new Intent(getActivity(), AboutUsActivity.class).putExtra("about", "contact"));
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}

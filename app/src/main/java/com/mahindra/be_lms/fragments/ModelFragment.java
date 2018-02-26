package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.ModelAdapter;
import com.mahindra.be_lms.db.Model;
import com.mahindra.be_lms.db.Product;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.util.DBHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.dao.query.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModelFragment extends Fragment implements MyCallback {
    private static final String TAG = ModelFragment.class.getSimpleName();
    @BindView(R.id.rvModelFragmentProductList)
    RecyclerView rvModelFragmentProductList;
    @BindView(R.id.tvModelFragmentRecordNotFound)
    TextView tvModelFragmentRecordNotFound;
    private List<Model> modelList;
    private Product product;
    private MainActivity mainActivity;
    private Query<Model> modelQuery;

    public ModelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_model, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this, view);
        L.l(TAG, "[METHOD:onCreateView]");
        init();
        return view;
    }

    private void init() {
        mainActivity.setDrawerEnabled(false);
        L.l(TAG, "[METHOD:init]");
        Bundle bundle = getArguments();
        if (bundle != null) {
            product = bundle.getParcelable(getString(R.string.product_parcelabel_tag));
            L.l(TAG, "[METHOD:init] getProduct object from bundle Product ID:" + product.getProductID() + " Product Name: " + product.getProductName());
            modelQuery = new DBHelper().getModelList(product.getProductID());
            rvModelFragmentProductList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvModelFragmentProductList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            rvModelFragmentProductList.setHasFixedSize(true);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        L.l(TAG, "[METHOD:onAttach]");
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        L.l(TAG, "[METHOD:onResume]");
        if (!MyApplication.mySharedPreference.checkUserLogin()) {
            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    loadModel();
                }
            });
            try {
                mainActivity.getSupportActionBar().show();
                mainActivity.getSupportActionBar().setTitle("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadModel() {
        L.l(TAG, "[METHOD:loadModel]");
        if (product != null) {
            modelList = modelQuery.list();
            L.l("ModelList size ; " + modelList.size());
            Collections.sort(modelList, new Comparator<Model>() {
                @Override
                public int compare(Model model1, Model model2) {
                    return Integer.parseInt(model1.getModelSequence()) > Integer.parseInt(model2.getModelSequence()) ? 1 : Integer.parseInt(model1.getModelSequence()) < Integer.parseInt(model2.getModelSequence()) ? -1 : 0;
                }
            });
            if (modelList.size() == 0) {
                tvModelFragmentRecordNotFound.setVisibility(View.VISIBLE);
            } else {
                ModelAdapter adapter = new ModelAdapter(mainActivity, modelList);
                adapter.setMyCallback(this);
                rvModelFragmentProductList.setAdapter(adapter);
            }
        }
    }

    @Override
    public void myCallback(int position) {
        L.l(TAG, "[METHOD:myCallback]");
        // L.t("Position : " + position);
        // preference.setModelHits();
        DocumentTreeFragment documentTreeFragment = new DocumentTreeFragment();
        Bundle bundle = new Bundle();
        L.l(TAG, "MODEL : " + modelList.get(position).toString());
        bundle.putParcelable(getString(R.string.model_parcelable_tag), modelList.get(position));
        bundle.putString("product_id", product.getProductID());
        documentTreeFragment.setArguments(bundle);
        mainActivity.replaceFrgament(documentTreeFragment);
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }
}

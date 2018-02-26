package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.lib.L;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TechnicalDocumentFragment extends Fragment {
    @BindView(R.id.bottomBar_technical_document)
    BottomBar bottomBarTechnicalDocument;
    @BindView(R.id.contentContainer_technical_upload)
    FrameLayout contentContainerTechnicalDocument;
    private MainActivity mainActivity;

    public TechnicalDocumentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_technical_document, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    private void init() {
        mainActivity.setDrawerEnabled(false);
        bottomBarTechnicalDocument.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_view_technical_upload_new:
                        setTitle(getString(R.string.tab_technical_upload_new));
                        replaceFrgament(new UploadToServerFragment());
                        break;
                    case R.id.tab_view_technical_upload_by_me:
                        setTitle(getString(R.string.tab_technical_upload_by_me));
                        replaceFrgament(new MyTechnicalUploadFragment());
                        break;
                    case R.id.tab_technical_upload_other:
                        setTitle(getString(R.string.tab_technical_upload_by_other));
                        replaceFrgament(new TechnicalDocumentByOtherFragment());
                        break;
                }
            }
        });
        bottomBarTechnicalDocument.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_view_technical_upload_new:
                        setTitle(getString(R.string.tab_technical_upload_new));
                        replaceFrgament(new UploadToServerFragment());
                        break;
                    case R.id.tab_view_technical_upload_by_me:
                        setTitle(getString(R.string.tab_technical_upload_by_me));
                        replaceFrgament(new MyTechnicalUploadFragment());
                        break;
                    case R.id.tab_technical_upload_other:
                        setTitle(getString(R.string.tab_technical_upload_by_other));
                        replaceFrgament(new TechnicalDocumentByOtherFragment());
                        break;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setTitle(String title) {
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

    public void replaceFrgament(Fragment fragment) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
        getFragmentManager().beginTransaction()
                .replace(R.id.contentContainer_technical_upload, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }


}

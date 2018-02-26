package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mahindra.be_lms.R;
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
public class MostViewedFragment extends Fragment {
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @BindView(R.id.contentContainer)
    FrameLayout contentContainer;

    private MainActivity mainActivity;

    public MostViewedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_most_viewed, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mainActivity.setDrawerEnabled(false);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_view_by_me) {
                    setTitle(getString(R.string.tab_most_view_by_me));
                    replaceFrgament(new MostViewedByMeFragment());
                }
                if (tabId == R.id.tab_view_by_other) {
                    setTitle(getString(R.string.tab_most_view_by_other));
                    replaceFrgament(new MostViewedByOtherFragment());
                }
//                if (tabId == R.id.tab_my_other) {
//                    setTitle(getString(R.string.nav_my_history));
//                    replaceFrgament(new MyHistory());
//                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_view_by_me) {
                    setTitle(getString(R.string.tab_most_view_by_me));
                    MostViewWebViewFragment byme = MostViewWebViewFragment.newInstance("byme");
                    replaceFrgament(byme);
                }
                if (tabId == R.id.tab_view_by_other) {
                    setTitle(getString(R.string.tab_most_view_by_other));
                    MostViewWebViewFragment byother = MostViewWebViewFragment.newInstance("byother");
                    replaceFrgament(byother);
                }
//                if (tabId == R.id.tab_my_other) {
//                    setTitle(getString(R.string.nav_my_history));
//                    MostViewWebViewFragment history = MostViewWebViewFragment.newInstance("history");
//                    replaceFrgament(history);
//                }
            }
        });
    }

    public void replaceFrgament(Fragment fragment) {
        L.l("Fragment SIMPLE NAME : " + fragment.getClass().getSimpleName());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
        getFragmentManager().beginTransaction()
                .replace(R.id.contentContainer, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setTitle(String title) {
        try {
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setTitle("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.footertext)
    public void onClickHomeText() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }
}

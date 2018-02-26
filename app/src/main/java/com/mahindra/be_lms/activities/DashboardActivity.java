package com.mahindra.be_lms.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.fragments.HomeFragment;
import com.mahindra.be_lms.fragments.HomeMenuFragment;
import com.mahindra.be_lms.fragments.LearningHubFragment;
import com.mahindra.be_lms.fragments.MostViewedFragment;
import com.mahindra.be_lms.fragments.NewNotification;
import com.mahindra.be_lms.fragments.NewsAndUpdateFragment;
import com.mahindra.be_lms.fragments.ProfileViewFragment;
import com.mahindra.be_lms.fragments.TechnicalDocumentByOtherFragment;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.util.CircularImageView;
import com.mahindra.be_lms.util.DBHelper;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ntb.NavigationTabBar;

/**
 * Created by Dell on 1/31/2018.
 */

public class DashboardActivity  extends BaseActivity implements View.OnClickListener {

    private Fragment fragment;
    @BindView(R.id.contentContainer_home)
    FrameLayout contentContainer_home;
    @BindView(R.id.iv_header_imageView)
    CircularImageView iv_header_imageView;
    @BindView(R.id.tv_userName)
    TextView tv_userName;
    private User user;
    @BindView(R.id.menuBar_dashboard)
    BottomBar menuBar_dashboard;
    @BindView(R.id.ntb)
    NavigationTabBar navigationTabBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();

    }

    public void init(){


      //  final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.icn_dashbord_alfa_50),
                        Color.parseColor("#00ffffff")
                ).title("Heart")
                        .badgeTitle("NTB")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.icn_training_alfa_50),
                        Color.parseColor("#00ffffff")

                ).title("Cup")
                        .badgeTitle("with")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.icn_notification_alfa_50),
                        Color.parseColor("#00ffffff")

                ).title("Diploma")
                        .badgeTitle("state")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.icn_security_alfa_50),
                        Color.parseColor("#00ffffff")

                ).title("Flag")
                        .badgeTitle("icon")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setModelIndex(0, true);
        if(navigationTabBar.getModelIndex()==0){
            replaceFrgament(new NewsAndUpdateFragment());
        }
        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
               // Toast.makeText(DashboardActivity.this, String.format("onEndTabSelected #%d", index), Toast.LENGTH_SHORT).show();
                switch (index){
                    case 0:
                        Log.e("=========","==========");
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        replaceFrgament(new NewsAndUpdateFragment());
                        break;
                    case 1:
                        //getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        replaceFrgament(new LearningHubFragment());
                        break;
                    case 2:
                        replaceFrgament(new NewNotification());
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
            }


            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
               // Toast.makeText(DashboardActivity.this, String.format("onEndTabSelected #%d", index), Toast.LENGTH_SHORT).show();
            }
        });



//        menuBar_dashboard.setOnTabSelectListener(new OnTabSelectListener() {
//            @Override
//            public void onTabSelected(@IdRes int tabId) {
//                switch (tabId) {
//                    case R.id.excellence_update:
//                        //setTitle(getString(R.string.tab_technical_upload_new));
//                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        replaceFrgament(new NewsAndUpdateFragment());
//                        break;
//                    case R.id.tab_view_learning_hub:
//                        // setTitle(getString(R.string.tab_technical_upload_by_me));
//                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        replaceFrgament(new LearningHubFragment());
//                        break;
//                    case R.id.tab_technical_upload_other:
//                        //setTitle(getString(R.string.tab_technical_upload_by_other));
//                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        replaceFrgament(new TechnicalDocumentByOtherFragment());
//                        break;
//                }
//            }
//        });
//        menuBar_dashboard.setOnTabReselectListener(new OnTabReselectListener() {
//            @Override
//            public void onTabReSelected(@IdRes int tabId) {
//                switch (tabId) {
//                    case R.id.excellence_update:
//                        //setTitle(getString(R.string.tab_technical_upload_new));
//                        replaceFrgament(new NewsAndUpdateFragment());
//                        break;
//                    case R.id.tab_view_learning_hub:
//                        // setTitle(getString(R.string.tab_technical_upload_by_me));
//                        replaceFrgament(new LearningHubFragment());
//                        break;
//                    case R.id.tab_technical_upload_other:
//                        //setTitle(getString(R.string.tab_technical_upload_by_other));
//                        replaceFrgament(new TechnicalDocumentByOtherFragment());
//                        break;
//                }
//            }
//        });
        //replaceFrgament(new HomeMenuFragment());
        iv_header_imageView.setOnClickListener(this);
        user = new DBHelper().getUser();
        tv_userName.setText("Hi "+user.getUserFirstName());
        if (!user.getUserPicture().isEmpty()) {
            Picasso.with(DashboardActivity.this)
                    .load(user.getUserPicture()+"&token="+ MyApplication.mySharedPreference.getUserToken())
                    .resize(200, 200)
                    .placeholder(getResources().getDrawable(R.drawable.user_new))
                    .centerCrop()
                    .into(iv_header_imageView);
            Log.e("IS FILEEXIST FLAG: ","isFilexists");
        } else {
            Log.e("IS FILEEXIST FLAG: ","notFilexists");
            L.l(this, "Set Dummy Bitmap to header");
            iv_header_imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_new));
        }

    }

    public void replaceFrgament(Fragment fragment) {
        String fragmentTag = fragment.getClass().getSimpleName();
        setFragment(fragment);
        L.l(this, "Fragment SIMPLE NAME : " + fragmentTag);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contentContainer_home, fragment, fragmentTag)
                .addToBackStack(null)
                .commit();

    }

    public void replaceFrgament1(Fragment fragment) {
        String fragmentTag = fragment.getClass().getSimpleName();
        setFragment(fragment);
        L.l(this, "Fragment SIMPLE NAME : " + fragmentTag);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contentContainer_home, fragment, fragmentTag)
                .commit();

    }



//    public void replaceFrgament1(Fragment fragment) {
//        String fragmentTag = fragment.getClass().getSimpleName();
//        setFragment(fragment);
//        L.l(this, "Fragment SIMPLE NAME : " + fragmentTag);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.dashboard_container, fragment, fragmentTag)
//                .addToBackStack(null)
//                .commit();
//
//    }
    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.iv_header_imageView:
//                navigationTabBar.setVisibility(View.GONE);
//                ProfileViewFragment profileViewFragment = new ProfileViewFragment();
//                replaceFrgament(profileViewFragment);
                Intent i = new Intent(this,ViewProfileDetails.class);
                startActivity(i);
                break;

            default:
                break;

        }

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.contentContainer_home) instanceof NewsAndUpdateFragment) {

            //menuBar_dashboard.selectTabAtPosition(0);
            Log.e("=========","==========++++");
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            navigationTabBar.setModelIndex(0, true);

        }else if(getSupportFragmentManager().findFragmentById(R.id.contentContainer_home) instanceof HomeFragment){
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        else if(getSupportFragmentManager().findFragmentById(R.id.contentContainer_home) instanceof ProfileViewFragment){
            navigationTabBar.setVisibility(View.VISIBLE);
            Log.e("=========","==========@@@@@@@@");
//            if(getSupportFragmentManager().findFragmentById(R.id.contentContainer_home) instanceof LearningHubFragment){
//                navigationTabBar.setModelIndex(1, true);
//            }else{
//
//            }
          //  replaceFrgament(new NewsAndUpdateFragment());
           // getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }else if(getSupportFragmentManager().findFragmentById(R.id.contentContainer_home) instanceof LearningHubFragment){
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            navigationTabBar.setModelIndex(0, true);
            replaceFrgament(new NewsAndUpdateFragment());
        }
        else if(getSupportFragmentManager().findFragmentById(R.id.contentContainer_home) instanceof NewNotification){
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            navigationTabBar.setModelIndex(1, true);
            replaceFrgament(new LearningHubFragment());
        }
//        else{
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();

        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.app_name))
                    .setMessage(getString(R.string.dialog_exit_msg))
                    .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                               /* Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);/*//***Change Here***
                             startActivity(intent);*/
                            finish();
                            // System.exit(0);
                        }
                    }).setNegativeButton(getString(R.string.dialog_no), null).show();
        }
        //}
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
}


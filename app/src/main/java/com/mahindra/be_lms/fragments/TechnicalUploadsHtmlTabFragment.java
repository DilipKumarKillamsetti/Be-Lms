package com.mahindra.be_lms.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahindra.be_lms.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TechnicalUploadsHtmlTabFragment extends Fragment {
    @BindView(R.id.rvTUH)
    RecyclerView rvTUH;
    @BindView(R.id.tvTUHRecordNotFound)
    TextView tvTUHRecordNotFound;

    public TechnicalUploadsHtmlTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technical_uploads_html_tab, container, false);
        ButterKnife.bind(this, view);
        init();
        // Inflate the layout for this fragment
        return view;
    }

    private void init() {
       /* List<TechnicalUpload> TUHList = DataProvider.getDummyTUHList();
        L.l("TUH size: "+ TUHList.size());
        rvTUH.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        rvTUH.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        rvTUH.setHasFixedSize(true);*/
        // TechnicalUploadsAdapter adapter=new TechnicalUploadsAdapter(getActivity(), TUHList);
        // rvTUH.setAdapter(adapter);
    }

}

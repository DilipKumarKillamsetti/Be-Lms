package com.mahindra.be_lms.filter;

import android.widget.Filter;

import com.mahindra.be_lms.adapter.QueriesAdapter;
import com.mahindra.be_lms.model.Queries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pravin on 10/19/16.
 */

public class CustomFilterQueries extends Filter {
    private final QueriesAdapter mQueriesAdapter;
    private List<Queries> filterList = new ArrayList<>();

    public CustomFilterQueries(QueriesAdapter mQueriesAdapter, List<Queries> filterList) {
        this.mQueriesAdapter = mQueriesAdapter;
        this.filterList = filterList;
    }

    //Filter perform here
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        //CHECK VALIDITY
        if (constraint != null && constraint.length() > 0) {

            constraint = constraint.toString().toUpperCase();
            List<Queries> filteredQueries = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getQuery_subject().toUpperCase().contains(constraint)) {
                    filteredQueries.add(filterList.get(i));
                }
            }

            results.count = filteredQueries.size();
            results.values = filteredQueries;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        mQueriesAdapter.queriesList = (List<Queries>) results.values;

        //REFRESH
        mQueriesAdapter.notifyDataSetChanged();
    }
}

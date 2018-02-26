package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.Event;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.QuizDataModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 11/11/2017.
 */

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventViewHolder>  {

    private List<Event> events;
    private final Context context;
    private Callback mCallback;

    public EventsListAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }
    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public EventsListAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_list_item, parent, false);
        return new EventsListAdapter.EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventsListAdapter.EventViewHolder holder, final int position) {

        holder.quizName.setText(events.get(position).getEventName());
        holder.btnStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCallback.myCallback(position, context.getString(R.string.event_tag));
            }
        });


    }
    public void setEventList(List<Event> events) {
        this.events = events;
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.quizName)
        TextView quizName;
        @BindView(R.id.btnStart)
        Button btnStart;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btnStart.setText("View Details");
        }
    }


}

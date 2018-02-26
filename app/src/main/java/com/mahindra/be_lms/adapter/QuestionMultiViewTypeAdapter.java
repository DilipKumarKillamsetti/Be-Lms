package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.model.QuestionListModel;

import java.util.ArrayList;


/**
 * Created by anupamchugh on 09/02/16.
 */
public class QuestionMultiViewTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<QuestionListModel> dataSet;
    Context mContext;
    int total_types;
    MediaPlayer mPlayer;
    private boolean fabStateVolume = false;

    public static class TextTypeViewHolder extends RecyclerView.ViewHolder {


        TextView txtType;
        CardView cardView;

        public TextTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.type);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);

        }

    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {


        TextView txtType;
        ImageView image;

        public ImageTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.type);
            //this.image = (ImageView) itemView.findViewById(R.id.background);

        }

    }

    public static class RadioTypeViewHolder extends RecyclerView.ViewHolder {


        TextView txtType;
        ImageView image;

        public RadioTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.type);
          //  this.image = (ImageView) itemView.findViewById(R.id.background);

        }

    }

    public static class TFTypeViewHolder extends RecyclerView.ViewHolder {


        TextView txtType;
        ImageView image;

        public TFTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.type);
            //  this.image = (ImageView) itemView.findViewById(R.id.background);

        }

    }
    public static class CheckBoxTypeViewHolder extends RecyclerView.ViewHolder {


        TextView txtType;
        ImageView image;

        public CheckBoxTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.type);
            //  this.image = (ImageView) itemView.findViewById(R.id.background);

        }

    }


    public static class DescriptiveTypeViewHolder extends RecyclerView.ViewHolder {


        TextView txtType;
        ImageView image;

        public DescriptiveTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.type);
            //  this.image = (ImageView) itemView.findViewById(R.id.background);

        }
    }

    public QuestionMultiViewTypeAdapter(ArrayList<QuestionListModel> data, Context context) {
        this.dataSet = data;
        this.mContext = context;
        total_types = dataSet.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case QuestionListModel.RADIO_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_type, parent, false);
                return new RadioTypeViewHolder(view);
            case QuestionListModel.T_F_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tf_type, parent, false);
                return new TFTypeViewHolder(view);
            case QuestionListModel.CK_BOX_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_type, parent, false);
                return new CheckBoxTypeViewHolder(view);
            case QuestionListModel.DESC_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.descriptive_type, parent, false);
                return new DescriptiveTypeViewHolder(view);
        }
        return null;


    }


    @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).type) {
            case 0:
                return QuestionListModel.TEXT_TYPE;
            case 1:
                return QuestionListModel.IMAGE_TYPE;
            case 2:
                return QuestionListModel.AUDIO_TYPE;
            case 3:
                return QuestionListModel.RADIO_TYPE;
            case 4:
                return QuestionListModel.T_F_TYPE;
            case 5:
                return QuestionListModel.CK_BOX_TYPE;
            case 6:
                return QuestionListModel.DESC_TYPE;
            default:
                return -1;
        }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        QuestionListModel object = dataSet.get(listPosition);
        if (object != null) {
            switch (object.type) {
                case QuestionListModel.TEXT_TYPE:
                    ((TextTypeViewHolder) holder).txtType.setText(object.text);
                    break;
                case QuestionListModel.IMAGE_TYPE:
                    ((ImageTypeViewHolder) holder).txtType.setText(object.text);
                    ((ImageTypeViewHolder) holder).image.setImageResource(object.data);
                    break;
                case QuestionListModel.RADIO_TYPE:
                    ((RadioTypeViewHolder) holder).txtType.setText(object.text);
                    break;
                case QuestionListModel.T_F_TYPE:
                    ((TFTypeViewHolder) holder).txtType.setText(object.text);
                    break;
                case QuestionListModel.CK_BOX_TYPE:
                    ((CheckBoxTypeViewHolder) holder).txtType.setText(object.text);
                    break;
                case QuestionListModel.DESC_TYPE:
                    ((DescriptiveTypeViewHolder) holder).txtType.setText(object.text);
                    break;

            }
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}

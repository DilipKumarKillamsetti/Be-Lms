package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.PreviewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 11/14/2017.
 */

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<PreviewModel> previewModels;

    private final Context context;
    private Callback mCallback;


    public QuestionAdapter(Context context, List<PreviewModel> previewModels) {
        this.context = context;
        this.previewModels = previewModels;
    }

    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public QuestionAdapter.QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.answer_preview_list_item, parent, false);
        return new QuestionAdapter.QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestionAdapter.QuestionViewHolder holder, final int position) {

        holder.wvQuestionPreview.loadData(previewModels.get(position).getHtml(), "text/html", "UTF-8");

    }

    public void setQuestionList(List<PreviewModel> previewModels) {
        this.previewModels = previewModels;
    }

    @Override
    public int getItemCount() {
        return previewModels.size();
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.wvQuestionPreview)
        WebView wvQuestionPreview;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public QuestionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}




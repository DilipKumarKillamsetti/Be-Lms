package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.model.PreviewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 11/13/2017.
 */

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.PreviewAnswerViewHolder> {

    private List<PreviewModel> previewModels;

    private final Context context;
    private Callback mCallback;


    public PreviewAdapter(Context context, List<PreviewModel> previewModels) {
        this.context = context;
        this.previewModels = previewModels;
    }
    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public PreviewAdapter.PreviewAnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.answer_preview_list_item, parent, false);
        return new PreviewAdapter.PreviewAnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PreviewAdapter.PreviewAnswerViewHolder holder, final int position) {

        holder.wvQuestionPreview.loadData(previewModels.get(position).getHtml(),"text/html","UTF-8");
//        holder.btnStart.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                mCallback.myCallback(position, context.getString(R.string.quiz_tag));
//            }
//        });
        WebSettings ws = holder.wvQuestionPreview.getSettings();

    }
    public void setPreviewList(List<PreviewModel> previewModels) {
        this.previewModels = previewModels;
    }

    @Override
    public int getItemCount() {
        return previewModels.size();
    }

    public class PreviewAnswerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.wvQuestionPreview)
        WebView wvQuestionPreview;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public PreviewAnswerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


        }
    }


}

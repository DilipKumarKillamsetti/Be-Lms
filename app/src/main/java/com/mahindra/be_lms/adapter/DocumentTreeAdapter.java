package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.model.DocumentTreeMaster;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pravin on 11/3/16.
 */
public class DocumentTreeAdapter extends RecyclerView.Adapter<DocumentTreeAdapter.DocumentViewHolder> {
    private final Context context;
    private final List<DocumentTreeMaster> documentTreeList;
    private MyCallback myCallback;

    public DocumentTreeAdapter(Context context, List<DocumentTreeMaster> documentTreeList) {
        this.context = context;
        this.documentTreeList = documentTreeList;
    }

    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DocumentViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_document_tree, parent, false));
    }

    @Override
    public void onBindViewHolder(DocumentViewHolder holder, int position) {
        Drawable drawable = null;
        if (documentTreeList.get(position).getType().equalsIgnoreCase("doc")) {
            try {
                JSONObject jsonObject = new JSONObject(documentTreeList.get(position).getName().toString());
                Iterator<String> iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    L.l("Document Title: " + key);
                    try {
                        Object value = jsonObject.get(key);
                        L.l("Document Name: " + value);
                        String extension = CommonFunctions.getExtension(value.toString());
                        L.l("File Extension : " + extension);
                        if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
                            drawable = context.getResources().getDrawable(R.drawable.icon_xls);
                        } else if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")) {
                            drawable = context.getResources().getDrawable(R.drawable.icon_doc);
                        } else if (extension.equalsIgnoreCase("pdf")) {
                            drawable = context.getResources().getDrawable(R.drawable.icon_pdf);
                        } else {
                            drawable = context.getResources().getDrawable(R.drawable.unknown_icon);
                        }
                        holder.ivDocumentIcon.setImageDrawable(drawable);
                        if (extension.equalsIgnoreCase("png")
                                || extension.equalsIgnoreCase("jpg")
                                || extension.equalsIgnoreCase("JPE")
                                || extension.equalsIgnoreCase("IMG")
                                || extension.equalsIgnoreCase("GIF")
                                || extension.equalsIgnoreCase("PSD")
                                || extension.equalsIgnoreCase("jpeg")) {
                            Picasso.with(context).load(Constants.DOC_UPLOAD_DOWNLOAD_URL + value.toString()).into(holder.ivDocumentIcon);
                        }
                        holder.tvDocumentFolderName.setText(key);
                    } catch (JSONException e) {
                        // Something went wrong!
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            drawable = context.getResources().getDrawable(R.drawable.folder);
            holder.tvDocumentFolderName.setText(documentTreeList.get(position).getName());
        }

    }

    @Override
    public int getItemCount() {
        return documentTreeList.size();
    }

    public void setMyCallback(MyCallback myCallback) {
        this.myCallback = myCallback;
    }

    public class DocumentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvDocumentFolderName)
        TextView tvDocumentFolderName;
        @BindView(R.id.ivDocumentIcon)
        ImageView ivDocumentIcon;

        public DocumentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myCallback.myCallback(getAdapterPosition());
                }
            });
        }

    }
}

package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.db.Product;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.lib.L;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Pravin on 10/17/16.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final Context context;
    private final List<Product> productList;
    private Callback mCallback;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_product_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        String productTitle = productList.get(position).getProductName();

        holder.tvProductTitle.setText(productTitle.toUpperCase().charAt(0) + productTitle.substring(1));
        L.l("PRODUCT IMAGE LINK: " + productList.get(position).getProductImageUrl());
        L.l("Product Sequence ID: " + productList.get(position).getProductSequence());
        Picasso.with(context).load(productList.get(position).getProductImageUrl()).placeholder(R.drawable.powerol_logo).into(holder.ivProductImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvProductTitle)
        TextView tvProductTitle;
        @BindView(R.id.ivProductImage)
        ImageView ivProductImage;

        public ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.ivProductQuestion, R.id.rlProductMainLayout})
        public void itemClick(View view) {
            if (view.getId() == R.id.ivProductQuestion) {
                mCallback.myCallback(getAdapterPosition(), "Question");
            } else if (view.getId() == R.id.rlProductMainLayout) {
                mCallback.myCallback(getAdapterPosition(), "Item");
            }
        }
    }
}

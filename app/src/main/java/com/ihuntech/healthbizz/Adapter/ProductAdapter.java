package com.ihuntech.healthbizz.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ihuntech.healthbizz.Activity.DetailActivity;
import com.ihuntech.healthbizz.Common.Common;
import com.ihuntech.healthbizz.Interface.ItemClickListener;
import com.ihuntech.healthbizz.Model.Product;
import com.ihuntech.healthbizz.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_product,parent,false);

        return new ProductViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Picasso
                .get()
                .load(new StringBuilder(Common.IMAGE_URL)
                        .append(productList.get(position).photo).toString())
                .error(R.mipmap.ic_launcher)
                .into(holder.productImage);

        holder.productName.setText(productList.get(position).name);

        int previous = Common.calculatePrice(Float.parseFloat(productList.get(position).previous_price));
        int discount = Common.calculatePrice(Float.parseFloat(productList.get(position).discount_price));

        if (previous == 0) {
            holder.previousPrice.setVisibility(View.GONE);
            Common.CURRENT_PRICE = String.valueOf(discount);
        }
        else if (discount == 0)   {
            holder.discountPrice.setVisibility(View.GONE);
            Common.CURRENT_PRICE = String.valueOf(previous);
        }
        else if (previous > discount)  {
            holder.previousPrice.setVisibility(View.VISIBLE);
            holder.discountPrice.setVisibility(View.VISIBLE);
            holder.previousPrice.setTextColor(context.getColor(R.color.white));
            holder.previousPrice.setPaintFlags(holder.previousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            Common.CURRENT_PRICE = String.valueOf(discount);
        }
        else if (previous < discount)  {
            holder.previousPrice.setVisibility(View.VISIBLE);
            holder.discountPrice.setVisibility(View.GONE);
            Common.CURRENT_PRICE = String.valueOf(previous);
        }

        holder.previousPrice.setText("₹ "+previous);
        holder.discountPrice.setText("₹ "+discount);

        int calculate = Common.calculateDiscount(
                Float.parseFloat(productList.get(position).previous_price),
                Float.parseFloat(productList.get(position).discount_price)
        );

        if (calculate == 0) {
            holder.txtDiscount.setVisibility(View.GONE);
        }
        else {
            holder.txtDiscount.setVisibility(View.VISIBLE);
            holder.txtDiscount.setText("-"+calculate+"%");
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                Common.CURRENT_PRODUCT = productList.get(position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView productImage;
        TextView productName, previousPrice,discountPrice, txtDiscount;

        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            previousPrice = itemView.findViewById(R.id.previousPrice);
            discountPrice = itemView.findViewById(R.id.discountPrice);
            txtDiscount = itemView.findViewById(R.id.txtDiscount);

            productName.setSelected(true);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(view);
        }
    }
}

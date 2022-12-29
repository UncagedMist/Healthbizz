package com.ihuntech.healthbizz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ihuntech.healthbizz.CartDB.Model.Cart;
import com.ihuntech.healthbizz.Common.Common;
import com.ihuntech.healthbizz.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.CheckViewHolder> {

    Context context;
    List<Cart> cartList;

    public CheckAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_check,parent,false);

        return new CheckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckViewHolder holder, int position) {

        Picasso
                .get()
                .load(new StringBuilder(Common.IMAGE_URL)
                        .append(cartList.get(position).photo).toString())
                .into(holder.layoutImage);

        holder.layoutName.setText(cartList.get(position).name);

        holder.layoutDesc.setText(cartList.get(position).qty +"  x  "+ "₹ "+cartList.get(position).price);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class CheckViewHolder extends RecyclerView.ViewHolder {

        TextView layoutDesc, layoutName;
        ImageView layoutImage;

        public CheckViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutDesc = itemView.findViewById(R.id.layoutDesc);

            layoutName = itemView.findViewById(R.id.layoutName);

            layoutImage = itemView.findViewById(R.id.layoutImage);

            layoutName.setSelected(true);
        }
    }
}

package com.ihuntech.healthbizz.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.ihuntech.healthbizz.CartDB.Model.Cart;
import com.ihuntech.healthbizz.Common.Common;
import com.ihuntech.healthbizz.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    List<Cart> carts;

    public CartAdapter(Context context, List<Cart> carts) {
        this.context = context;
        this.carts = carts;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_cart,parent,false);

        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final float[] total = {0};

        Picasso
                .get()
                .load(new StringBuilder(Common.IMAGE_URL)
                        .append(carts.get(position).photo).toString())
                .into(holder.cartImage);

        holder.cartName.setText(carts.get(position).name);
        holder.cartCategory.setText(carts.get(position).category);
        holder.cartPrice.setText("₹ "+String.valueOf(carts.get(position).price));
        holder.cartQuantity.setNumber(String.valueOf(carts.get(position).qty));

        holder.cartQuantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Cart cart = carts.get(position);

                cart.qty = newValue;

                Common.cartRepository.updateCart(cart);
            }
        });

        for (int i = 0; i < carts.size(); i++) {

            String price= carts.get(i).price;
            float quantity = Float.parseFloat(String.valueOf(carts.get(i).qty));
            float pricePerUnit = Float.parseFloat(price);

            total[0] = total[0] + (pricePerUnit * quantity);

            carts.get(position).subTotal = String.valueOf(total[0]);
            carts.get(position).Total = String.valueOf(total[0]);
        }
        Common.SUB_TOTAL = String.valueOf(total[0]);
        Common.TOTAL = String.valueOf(total[0]);
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public void removeItem(int position)    {
        carts.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(Cart item,int position)   {
        carts.add(position,item);
        notifyItemInserted(position);
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView cartImage;
        TextView cartName, cartCategory, cartPrice;
        ElegantNumberButton cartQuantity;

        public RelativeLayout view_background;
        public LinearLayout view_foreground;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cartImage = itemView.findViewById(R.id.cartImage);

            cartName = itemView.findViewById(R.id.cartName);
            cartCategory = itemView.findViewById(R.id.cartCat);
            cartPrice = itemView.findViewById(R.id.cartPrice);

            cartQuantity = itemView.findViewById(R.id.cartQuantity);

            view_foreground = itemView.findViewById(R.id.view_foreground);
            view_background = itemView.findViewById(R.id.view_background);

            cartName.setSelected(true);
        }
    }
}

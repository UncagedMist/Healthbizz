package com.ihuntech.healthbizz.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.gson.Gson;
import com.ihuntech.healthbizz.CartDB.Model.Cart;
import com.ihuntech.healthbizz.Common.Common;
import com.ihuntech.healthbizz.R;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    ImageView detailImage;
    TextView txtName,txtPrevious, txtDiscount, txtStock, txtSort, txtDetail, txtPercent;

    RecyclerView recyclerView;

    int quantity = 1;

    ElegantNumberButton btnQuantity;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailImage = findViewById(R.id.detailImage);
        txtName = findViewById(R.id.detailName);
        txtPrevious = findViewById(R.id.previousPrice);
        txtDiscount = findViewById(R.id.discountPrice);
        txtStock = findViewById(R.id.txtStock);
        txtSort = findViewById(R.id.detailIngredient);
        txtDetail = findViewById(R.id.detailsDesc);
        btnQuantity = findViewById(R.id.cartQuantity);
        txtPercent = findViewById(R.id.txtDiscount);

        recyclerView = findViewById(R.id.recyclerView);

        txtName.setSelected(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadAllDetails();

        findViewById(R.id.btnCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(Common.CURRENT_PRODUCT.stock) >= 1)    {
                    addToCart();
                }
                else {
                    Toast.makeText(DetailActivity.this, "Product is out of stock!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btnBuy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(Common.CURRENT_PRODUCT.stock) >= 1)    {
                    buyNow();
                }
                else {
                    Toast.makeText(DetailActivity.this, "Product is out of stock!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnQuantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                quantity = newValue;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadAllDetails() {
        Picasso
                .get()
                .load(new StringBuilder(Common.IMAGE_URL)
                        .append(Common.CURRENT_PRODUCT.photo).toString())
                .into(detailImage);

        txtName.setText(Common.CURRENT_PRODUCT.name);

        if (Integer.parseInt(Common.CURRENT_PRODUCT.stock) >= 1)    {
            txtStock.setTextColor(getColor(android.R.color.holo_green_dark));
            txtStock.setText("In Stock");
        }
        else {
            txtStock.setTextColor(getColor(android.R.color.holo_red_dark));
            txtStock.setText("Out of Stock");
        }

        int previous = Common.calculatePrice(Float.parseFloat(Common.CURRENT_PRODUCT.previous_price));
        int discount = Common.calculatePrice(Float.parseFloat(Common.CURRENT_PRODUCT.discount_price));

        if (previous == 0) {
            txtPrevious.setVisibility(View.GONE);
            Common.CURRENT_PRICE = String.valueOf(discount);
        }
        else if (discount == 0)   {
            txtDiscount.setVisibility(View.GONE);
            Common.CURRENT_PRICE = String.valueOf(previous);
        }
        else if (previous > discount)  {
            txtPrevious.setVisibility(View.VISIBLE);
            txtDiscount.setVisibility(View.VISIBLE);
            txtPrevious.setTextColor(getColor(R.color.overlayBackground));
            txtPrevious.setPaintFlags(txtPrevious.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            Common.CURRENT_PRICE = String.valueOf(discount);
        }
        else if (previous < discount)  {
            txtPrevious.setVisibility(View.VISIBLE);
            txtDiscount.setVisibility(View.GONE);
            Common.CURRENT_PRICE = String.valueOf(previous);
        }

        txtPrevious.setText("₹ "+previous);
        txtDiscount.setText("₹ "+discount);

        int calculate = Common.calculateDiscount(
                Float.parseFloat(Common.CURRENT_PRODUCT.previous_price),
                Float.parseFloat(Common.CURRENT_PRODUCT.discount_price)
        );

        if (calculate == 0) {
            txtPercent.setVisibility(View.GONE);
        }
        else {
            txtPercent.setVisibility(View.VISIBLE);
            txtPercent.setText("-"+calculate+"%");
        }

        if (Common.CURRENT_PRODUCT.sort_details != null)    {
            txtSort.setText(Common.stripHtml(Common.CURRENT_PRODUCT.sort_details));
        }
        else {
            txtSort.setVisibility(View.GONE);
        }

        if (Common.CURRENT_PRODUCT.details != null)    {
            txtDetail.setText(Common.stripHtml(Common.CURRENT_PRODUCT.details));
        }
        else {
            txtDetail.setVisibility(View.GONE);
        }
    }

    private void addToCart() {
        try {
            Cart cartItem = new Cart();
            cartItem.name = Common.CURRENT_PRODUCT.name;
            cartItem.qty = quantity;
            cartItem.photo = Common.CURRENT_PRODUCT.photo;
            cartItem.price = Common.CURRENT_PRICE;
            cartItem.main_price = Common.CURRENT_PRICE;
            cartItem.productId = Common.CURRENT_PRODUCT.id;
            cartItem.slug = Common.CURRENT_PRODUCT.slug;

            Common.cartRepository.insertToCart(cartItem);

            Log.d("CartDebug", new Gson().toJson(cartItem));

            Toast.makeText(this, cartItem.name+" Added to Cart.", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void buyNow() {
        try {
            Cart cartItem = new Cart();
            cartItem.name = Common.CURRENT_PRODUCT.name;
            cartItem.qty = quantity;
            cartItem.photo = Common.CURRENT_PRODUCT.photo;
            cartItem.price = Common.CURRENT_PRICE;
            cartItem.main_price = Common.CURRENT_PRICE;
            cartItem.productId = Common.CURRENT_PRODUCT.id;
            cartItem.slug = Common.CURRENT_PRODUCT.slug;

            Common.cartRepository.insertToCart(cartItem);

            startActivity(new Intent(DetailActivity.this,CartActivity.class));
            finish();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
package com.ihuntech.healthbizz.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.ihuntech.healthbizz.Adapter.CartAdapter;
import com.ihuntech.healthbizz.CartDB.DataSource.CartRepository;
import com.ihuntech.healthbizz.CartDB.Local.CartDataSource;
import com.ihuntech.healthbizz.CartDB.Local.CartDatabase;
import com.ihuntech.healthbizz.CartDB.Model.Cart;
import com.ihuntech.healthbizz.Common.Common;
import com.ihuntech.healthbizz.Interface.RecyclerItemTouchHelper;
import com.ihuntech.healthbizz.Interface.RecyclerItemTouchHelperListener;
import com.ihuntech.healthbizz.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    List<Cart> cartList = new ArrayList<>();

    CartAdapter adapter;

    RelativeLayout rootLayout;

    AppCompatButton btnShopNow, btnCheckout;

    TextView txtNote,txtMsg;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView = findViewById(R.id.recyclerView);

        rootLayout = findViewById(R.id.rootLayout);

        txtMsg = findViewById(R.id.txtMsg);
        txtNote = findViewById(R.id.txtNote);

        btnShopNow = findViewById(R.id.btnShopNow);
        btnCheckout = findViewById(R.id.btnCheckout);

        initDB();


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        loadCartItems();

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this,CheckoutActivity.class));

//                if (Common.CURRENT_USER != null)    {
////                    displayJson();
//                    startActivity(new Intent(CartActivity.this,CheckoutActivity.class));
//                    finish();
//                }
//                else {
//                    Toast.makeText(CartActivity.this, "Please login first to use this feature!!!", Toast.LENGTH_SHORT).show();
//
//                }
            }
        });

        btnShopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void initDB() {
        Common.cartDatabase = CartDatabase.getInstance(this);
        Common.cartRepository = CartRepository.getInstance(CartDataSource.getInstance(Common.cartDatabase.cartDAO()));
    }

    private void loadCartItems() {
        compositeDisposable.add(
                Common.cartRepository.getCartItems()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<List<Cart>>() {
                            @Override
                            public void accept(List<Cart> carts) throws Exception {
                                displayCartItems(carts);
                            }
                        })
        );
    }


    private void displayCartItems(List<Cart> carts) {
        cartList = carts;
        adapter = new CartAdapter(this,carts);

        if (adapter.getItemCount() == 0)    {
            btnShopNow.setVisibility(View.VISIBLE);
            txtMsg.setVisibility(View.VISIBLE);
            txtNote.setVisibility(View.GONE);
            btnCheckout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            btnShopNow.setVisibility(View.GONE);
            txtMsg.setVisibility(View.GONE);
            txtNote.setVisibility(View.VISIBLE);
            btnCheckout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartAdapter.CartViewHolder)  {
            String name = cartList.get(viewHolder.getAdapterPosition()).name;

            final Cart deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deletedIndex);

            Common.cartRepository.deleteCartItem(deletedItem);

            Snackbar snackbar = Snackbar.make(rootLayout,new StringBuilder(name)
                            .append(" removed from Cart").toString(),
                    Snackbar.LENGTH_LONG);

            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deletedItem,deletedIndex);
                    Common.cartRepository.insertToCart(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
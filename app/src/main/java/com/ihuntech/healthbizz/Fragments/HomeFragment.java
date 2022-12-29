package com.ihuntech.healthbizz.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ihuntech.healthbizz.Adapter.BannerSliderAdapter;
import com.ihuntech.healthbizz.Adapter.CategoryAdapter;
import com.ihuntech.healthbizz.Adapter.ProductAdapter;
import com.ihuntech.healthbizz.Common.Common;
import com.ihuntech.healthbizz.Interface.CategoryClickListener;
import com.ihuntech.healthbizz.Model.Banner;
import com.ihuntech.healthbizz.Model.Category;
import com.ihuntech.healthbizz.Model.Product;
import com.ihuntech.healthbizz.R;
import com.ihuntech.healthbizz.Remote.IMyAPI;
import com.ihuntech.healthbizz.Service.PicassoImageLoadingService;

import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

public class HomeFragment extends Fragment implements CategoryClickListener {

    Slider slider;

    IMyAPI myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    NoInternetDialog noInternetDialog;

    List<Category> categoryList = new ArrayList<>();

    RecyclerView recyclerCategory, recyclerProduct;

    TextView txtProducts;

    ImageView imgComing;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        noInternetDialog = new NoInternetDialog.Builder(this).build();

        myAPI = Common.getAPI();

        slider = view.findViewById(R.id.banner_slider);
        recyclerCategory = view.findViewById(R.id.recyclerCategory);
        recyclerProduct = view.findViewById(R.id.recyclerProduct);
        txtProducts = view.findViewById(R.id.txtProducts);
        imgComing = view.findViewById(R.id.imgComing);

        Slider.init(new PicassoImageLoadingService());

        recyclerCategory.setHasFixedSize(true);
        recyclerCategory.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));

        recyclerProduct.setNestedScrollingEnabled(false);

        recyclerProduct.setHasFixedSize(true);
        recyclerProduct.setLayoutManager(new GridLayoutManager(getContext(),2));

        fetchBanner();

        loadCategory();

        loadAllProducts();

        return view;
    }


    private void fetchBanner() {
        compositeDisposable.add(
                myAPI.getBanner()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Banner>>() {
                            @Override
                            public void accept(List<Banner> banners) throws Exception {
                                slider.setAdapter(new BannerSliderAdapter(banners));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(getContext(), "Error in Getting Slider Images \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void loadCategory() {
        compositeDisposable.add(
                myAPI.getCategory()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Category>>() {
                                       @Override
                                       public void accept(List<Category> categories) throws Exception {

                                           categoryList.clear();

                                           categoryList = categories;

                                           CategoryAdapter adapter = new CategoryAdapter(
                                                   getContext(),
                                                   categories,
                                                   HomeFragment.this::onCategoryClick
                                           );

                                           recyclerCategory.setAdapter(adapter);
                                       }
                                   }
                        )
        );
    }

    @Override
    public void onCategoryClick(int pos) {
        String id = categoryList.get(pos).id.trim();
        String name = categoryList.get(pos).name.trim();

        if (name != null)   {
            txtProducts.setVisibility(View.VISIBLE);
            txtProducts.setPaintFlags(txtProducts.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            txtProducts.setText(name+" Products");
        }

        loadProducts(id);
    }

    private void loadProducts(String id) {
        compositeDisposable.add(
                myAPI.getItemByCategory(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Product>>() {
                                       @Override
                                       public void accept(List<Product> products) throws Exception {

                                           ProductAdapter adapter = new ProductAdapter(getContext(),products);

                                           if (adapter.getItemCount() == 0) {
                                               recyclerProduct.setVisibility(View.GONE);
                                               imgComing.setVisibility(View.VISIBLE);
                                           }
                                           else {
                                               imgComing.setVisibility(View.GONE);
                                               recyclerProduct.setVisibility(View.VISIBLE);
                                               recyclerProduct.setAdapter(adapter);
                                           }

                                       }
                                   }
                        )
        );
    }

    private void loadAllProducts() {
        compositeDisposable.add(
                myAPI.getAllItems()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Product>>() {
                                       @Override
                                       public void accept(List<Product> products) throws Exception {

                                           ProductAdapter adapter = new ProductAdapter(getContext(),products);

                                           if (adapter.getItemCount() == 0) {
                                               recyclerProduct.setVisibility(View.GONE);
                                           }
                                           else {
                                               recyclerProduct.setVisibility(View.VISIBLE);
                                               recyclerProduct.setAdapter(adapter);
                                           }

                                       }
                                   }
                        )
        );
    }


    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        compositeDisposable.clear();
        super.onDestroy();
    }
}
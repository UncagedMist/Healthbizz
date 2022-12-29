package com.ihuntech.healthbizz.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.ihuntech.healthbizz.Fragments.Checkout1Fragment;
import com.ihuntech.healthbizz.R;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Checkout1Fragment checkout1Fragment = new Checkout1Fragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.container, checkout1Fragment).commit();
    }
}
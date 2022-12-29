package com.ihuntech.healthbizz.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ihuntech.healthbizz.Common.Common;
import com.ihuntech.healthbizz.Model.Country;
import com.ihuntech.healthbizz.R;
import com.ihuntech.healthbizz.Remote.IMyAPI;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class Checkout2Fragment extends Fragment {

    Spinner spinnerCountry;

    EditText edtFName, edtLName, edtEmail, edtPhone, edtAdd1, edtAdd2, edtCity, edtZip;

    IMyAPI mService;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    static Checkout2Fragment instance;

    ArrayList<String> countryNames = new ArrayList<>();


    public static Checkout2Fragment getInstance() {
        if (instance == null)   {
            instance = new Checkout2Fragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkout2, container, false);

        mService = Common.getAPI();

        spinnerCountry = view.findViewById(R.id.spinnerCountry);

        edtFName = view.findViewById(R.id.edtFName);
        edtLName = view.findViewById(R.id.edtLName);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtAdd1 = view.findViewById(R.id.edtAdd1);
        edtAdd2 = view.findViewById(R.id.edtAdd2);
        edtCity = view.findViewById(R.id.edtCity);
        edtZip = view.findViewById(R.id.edtZip);

        loadCountry();
        loadData();

        view.findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checkout2Fragment checkout2Fragment = new Checkout2Fragment();
                FragmentTransaction transaction = ((AppCompatActivity) getContext())
                        .getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, checkout2Fragment).commit();
            }
        });

        view.findViewById(R.id.btnPrevious).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checkout1Fragment checkout1Fragment = new Checkout1Fragment();
                FragmentTransaction transaction = ((AppCompatActivity) getContext())
                        .getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, checkout1Fragment).commit();
            }
        });

        view.findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fName = edtFName.getText().toString().trim();
                String lName = edtLName.getText().toString().trim();

                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String add1 = edtAdd1.getText().toString().trim();
                String add2 = edtAdd2.getText().toString().trim();
                String city = edtCity.getText().toString().trim();
                String zip = edtZip.getText().toString().trim();

                String country = spinnerCountry.getSelectedItem().toString();

                if (TextUtils.isEmpty(fName))   {
                    Toast.makeText(getContext(), "Enter First Name...", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(lName))   {
                    Toast.makeText(getContext(), "Enter Last Name...", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(email))   {
                    Toast.makeText(getContext(), "Enter Email...", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(phone))   {
                    Toast.makeText(getContext(), "Enter Phone Number...", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(add1))   {
                    Toast.makeText(getContext(), "Enter Address...", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(add2))   {
                    Toast.makeText(getContext(), "Enter Address...", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(city))   {
                    Toast.makeText(getContext(), "Enter City...", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(zip))   {
                    Toast.makeText(getContext(), "Enter Pin Code...", Toast.LENGTH_SHORT).show();
                }
                else if (country.equals("Choose country..."))   {
                    Toast.makeText(getContext(), "Please choose country...", Toast.LENGTH_SHORT).show();
                }
                else {

                    Common.FIRST_NAME_1 = fName;
                    Common.LAST_NAME_1 = lName;
                    Common.EMAIL_1 = email;
                    Common.PHONE_1 = phone;
                    Common.ADDRESS_1_1 = add1;
                    Common.ADDRESS_2_1 = add2;
                    Common.CITY_1 = city;
                    Common.ZIP_1 = zip;
                    Common.COUNTRY_1 = country;

                    if (Common.COUNTRY_1.equals("India"))   {
                        Common.COUNTRY_ID = "1";

                    }

                    Checkout3Fragment checkout3Fragment = new Checkout3Fragment();
                    FragmentTransaction transaction = ((AppCompatActivity) getContext())
                            .getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, checkout3Fragment).commit();
                }


            }
        });

        return view;
    }

    private void loadCountry() {
        compositeDisposable.add(mService.getCountry()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Country>>() {
                               @Override
                               public void accept(List<Country> countries) throws Exception {
                                   displayCountry(countries);
                               }
                           }
                )
        );
    }

    private void displayCountry(List<Country> countries) {

        countryNames.add("Choose country...");

        for (int i = 0; i < countries.size(); i++) {
            countryNames.add(countries.get(i).name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_list_item_1,
                countryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapter);
    }

    private void loadData() {

        if (Common.CHECK_STATUS == 1)   {
            edtFName.setText(Common.FIRST_NAME);
            edtLName.setText(Common.LAST_NAME);
            edtEmail.setText(Common.EMAIL);
            edtPhone.setText(Common.PHONE);
            edtAdd1.setText(Common.ADDRESS_1);
            edtAdd2.setText(Common.ADDRESS_2);
            edtCity.setText(Common.CITY);
            edtZip.setText(Common.ZIP);

            String compareValue = Common.COUNTRY;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    countryNames
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCountry.setAdapter(adapter);
            if (compareValue != null) {
                int spinnerPosition = adapter.getPosition(compareValue);
                spinnerCountry.setSelection(spinnerPosition);
            }
        }
        else if (Common.CHECK_STATUS == 0){
            edtFName.setText(Common.CURRENT_USER.first_name);
            edtLName.setText(Common.CURRENT_USER.last_name);
            edtEmail.setText(Common.CURRENT_USER.email);
            edtPhone.setText(Common.CURRENT_USER.phone);
            edtAdd1.setText(Common.CURRENT_USER.ship_address1);
            edtAdd2.setText(Common.CURRENT_USER.ship_address2);
            edtCity.setText(Common.CURRENT_USER.ship_city);
            edtZip.setText(Common.CURRENT_USER.ship_zip);

            String compareValue = Common.CURRENT_USER.ship_country;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    countryNames
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCountry.setAdapter(adapter);
            if (compareValue != null) {
                int spinnerPosition = adapter.getPosition(compareValue);
                spinnerCountry.setSelection(spinnerPosition);
            }
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        compositeDisposable.dispose();
        super.onDestroy();
    }
}
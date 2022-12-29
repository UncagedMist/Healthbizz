package com.ihuntech.healthbizz.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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

public class Checkout1Fragment extends Fragment {

    Spinner spinnerCountry;

    EditText edtFName, edtLName, edtEmail, edtPhone, edtAdd1, edtAdd2, edtCity, edtZip;
    CheckBox ckbAddress;

    IMyAPI mService;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    static Checkout1Fragment instance;

    ArrayList<String> countryNames = new ArrayList<>();

    public static Checkout1Fragment getInstance() {
        if (instance == null)   {
            instance = new Checkout1Fragment();
        }
        return instance;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkout1, container, false);

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

        ckbAddress = view.findViewById(R.id.ckbAddress);

        loadData();
        loadCountry();

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

                Common.COUNTRY = spinnerCountry.getSelectedItem().toString();

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
                else if (Common.COUNTRY.equals("Choose country..."))   {
                    Toast.makeText(getContext(), "Please choose country...", Toast.LENGTH_SHORT).show();
                }
                else {

                    Common.FIRST_NAME = fName;
                    Common.LAST_NAME = lName;
                    Common.EMAIL = email;
                    Common.PHONE = phone;
                    Common.ADDRESS_1 = add1;
                    Common.ADDRESS_2 = add2;
                    Common.CITY = city;
                    Common.ZIP = zip;

                    if (ckbAddress.isChecked()) {
                        Common.CHECK_STATUS = 1;
                    }
                    else {
                        Common.CHECK_STATUS = 0;
                    }

                    Checkout2Fragment checkout2Fragment = new Checkout2Fragment();
                    FragmentTransaction transaction = ((AppCompatActivity) getContext())
                            .getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, checkout2Fragment).commit();
                }
            }
        });

        return view;
    }

    private void loadData() {
        if (Common.CURRENT_USER != null)    {
            edtFName.setText(Common.CURRENT_USER.first_name);
            edtLName.setText(Common.CURRENT_USER.last_name);
            edtEmail.setText(Common.CURRENT_USER.email);
            edtPhone.setText(Common.CURRENT_USER.phone);
            edtAdd1.setText(Common.CURRENT_USER.bill_address1);
            edtAdd2.setText(Common.CURRENT_USER.bill_address2);
            edtCity.setText(Common.CURRENT_USER.bill_city);
            edtZip.setText(Common.CURRENT_USER.bill_zip);

            String compareValue = Common.CURRENT_USER.bill_country;
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

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        compositeDisposable.dispose();
        super.onDestroy();
    }
}
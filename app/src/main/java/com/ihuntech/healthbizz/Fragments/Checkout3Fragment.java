package com.ihuntech.healthbizz.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.gson.Gson;
import com.ihuntech.healthbizz.Activity.HomeActivity;
import com.ihuntech.healthbizz.Activity.PayRazorActivity;
import com.ihuntech.healthbizz.Adapter.CheckAdapter;
import com.ihuntech.healthbizz.CartDB.Model.Cart;
import com.ihuntech.healthbizz.Common.Common;
import com.ihuntech.healthbizz.Model.State;
import com.ihuntech.healthbizz.R;
import com.ihuntech.healthbizz.Remote.IMyAPI;
import com.ihuntech.healthbizz.Remote.JavaMailAPI;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Checkout3Fragment extends Fragment {

    TextView txtName, txtAdd, txtPhone;
    TextView txtName1, txtAdd1, txtPhone1;

    Spinner spinnerState;

    RecyclerView recyclerView;

    TextView txtSubTotal, txtShip, txtTotal;

    RadioButton rbOnline, rbOffline;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    IMyAPI mService;

    String json;

    String orderId;

    JSONObject object;

    static Checkout3Fragment instance;

    public static Checkout3Fragment getInstance() {
        if (instance == null)   {
            instance = new Checkout3Fragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkout3, container, false);

        mService = Common.getAPI();

        txtName = view.findViewById(R.id.txtName);
        txtAdd = view.findViewById(R.id.txtAddress);
        txtPhone = view.findViewById(R.id.txtPhone);

        txtName1 = view.findViewById(R.id.txtName1);
        txtAdd1 = view.findViewById(R.id.txtAddress1);
        txtPhone1 = view.findViewById(R.id.txtPhone1);

        spinnerState = view.findViewById(R.id.spinnerState);

        recyclerView = view.findViewById(R.id.recyclerView);

        txtSubTotal = view.findViewById(R.id.txtSubTotal);
        txtShip = view.findViewById(R.id.txtShipping);
        txtTotal = view.findViewById(R.id.txtTotal);

        rbOnline = view.findViewById(R.id.rbOnline);
        rbOffline = view.findViewById(R.id.rbOffline);

        txtName.setSelected(true);
        txtName1.setSelected(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadData();
        loadStates();

        new GenerateOrderId().execute();

        rbOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)  {
                    Common.PAYMENT_METHOD = 1;
                }
            }
        });

        rbOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)  {
                    Common.PAYMENT_METHOD = 2;
                }
            }
        });

        txtSubTotal.setText("₹ "+Common.SUB_TOTAL);
        txtTotal.setText("₹ " +Common.TOTAL);

        view.findViewById(R.id.btnPrevious).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checkout2Fragment checkout2Fragment = new Checkout2Fragment();
                FragmentTransaction transaction = ((AppCompatActivity) getContext())
                        .getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, checkout2Fragment).commit();
            }
        });

        view.findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Common.STATE = spinnerState.getSelectedItem().toString();

                if (Common.STATE.equals("Choose state...")) {
                    Toast.makeText(getContext(), "Please choose state...", Toast.LENGTH_SHORT).show();
                }
                else if (Common.PAYMENT_METHOD == 0) {
                    Toast.makeText(getContext(), "Please payment method...", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (Common.PAYMENT_METHOD == 1) {
                        startActivity(new Intent(getContext(), PayRazorActivity.class));
                    }
                    else if (Common.PAYMENT_METHOD == 2)    {
                        showCodDialog();
                    }

                }
            }
        });

        return view;
    }

    private void showCodDialog() {
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getContext());

        dialogBuilder
                .withTitle("Cash on Delivery Confirmation!")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessage("Pay Full Amount at the time of Delivery...")                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor("#003A79")                               //def  | withDialogColor(int resid)
                .withDuration(700)                                          //def
                .withEffect(Effectstype.Newspager)                                         //def Effectstype.Slidetop
                .withButton1Text("Cancel")                                      //def gone
                .withButton2Text("Place Order")                             //def gone
                .isCancelableOnTouchOutside(false)                           //def    | isCancelable(true)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendOrderToDB();
                        dialogBuilder.dismiss();                    }
                })
                .show();
    }

    private void loadData() {
        if (Common.CHECK_STATUS == 1)   {
            txtName.setText(Common.FIRST_NAME +" "+Common.LAST_NAME);
            txtAdd.setText(Common.ADDRESS_1 + " "+ Common.ADDRESS_2 + " "+Common.CITY + " "+Common.ZIP + " " +Common.COUNTRY);
            txtPhone.setText("+91 "+Common.PHONE);

            txtName1.setText(Common.FIRST_NAME_1 +" "+Common.LAST_NAME_1);
            txtAdd1.setText(Common.ADDRESS_1_1 + " "+ Common.ADDRESS_2_1 + " "+Common.CITY_1 + " "+Common.ZIP_1 + " " +Common.COUNTRY_1);
            txtPhone1.setText("+91 "+Common.PHONE_1);
        }
        else {
            txtName.setText(Common.FIRST_NAME +" "+Common.LAST_NAME);
            txtAdd.setText(Common.ADDRESS_1 + " "+ Common.ADDRESS_2 + " "+Common.CITY + " "+Common.ZIP + " " +Common.COUNTRY);
            txtPhone.setText("+91 "+Common.PHONE);

            txtName1.setText(Common.FIRST_NAME_1 +" "+Common.LAST_NAME_1);
            txtAdd1.setText(Common.ADDRESS_1_1 + " "+ Common.ADDRESS_2_1 + " "+Common.CITY_1 + " "+Common.ZIP_1 + " " +Common.COUNTRY_1);
            txtPhone1.setText("+91 "+Common.PHONE_1);
        }

        compositeDisposable.add(
                Common.cartRepository.getCartItems()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<List<Cart>>() {
                            @Override
                            public void accept(List<Cart> carts) throws Exception {
                                displayCartItems(carts);
                                Common.CARTS = carts;

                                try {
                                    displayJson(carts);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        })
        );
    }

    private void displayCartItems(List<Cart> carts) {
        CheckAdapter adapter = new CheckAdapter(getContext(),carts);
        recyclerView.setAdapter(adapter);
    }

    private void loadStates() {

        compositeDisposable.add(mService.getStates(
                        Common.COUNTRY_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<State>>() {
                               @Override
                               public void accept(List<State> states) throws Exception {
                                   displayStates(states);
                               }
                           }
                ));
    }

    private void displayStates(List<State> states) {

        ArrayList<String> stateName = new ArrayList<>();

        stateName.add("Choose state...");

        for (int i = 0; i < states.size(); i++) {
            stateName.add(states.get(i).name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_list_item_1,
                stateName
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(adapter);
    }

    private void sendOrderToDB() {

        JSONObject shipping = new JSONObject();

        Log.d("Price", Common.CURRENT_PRICE);

        float a = (Float.parseFloat(Common.TOTAL));
        int b = Math.round(a);


        if (b > 1000)  {
            try {
                shipping.put("id", "1");
                shipping.put("title", "Free Delivery");
                shipping.put("price", "0");
                shipping.put("minimum_price", "1000");
                shipping.put("is_condition", "1");
                shipping.put("status", "1");

                Log.d("Ship Response", String.valueOf(shipping));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {

            try {
                shipping.put("id", "2");
                shipping.put("title", "Delivery");
                shipping.put("price", "20");
                shipping.put("minimum_price", "0");
                shipping.put("is_condition", "0");
                shipping.put("status", "1");

                Log.d("Ship Response", String.valueOf(shipping));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        String cartDetails = new Gson().toJson(Common.CARTS);

        JSONObject jsonShipBody = new JSONObject();
        try {
            jsonShipBody.put("ship_first_name", Common.FIRST_NAME_1);
            jsonShipBody.put("ship_last_name", Common.LAST_NAME_1);
            jsonShipBody.put("ship_email", Common.EMAIL_1);
            jsonShipBody.put("ship_phone", Common.PHONE_1);
            jsonShipBody.put("ship_company", "null");
            jsonShipBody.put("ship_address1", Common.ADDRESS_1_1);
            jsonShipBody.put("ship_address2", Common.ADDRESS_2_1);
            jsonShipBody.put("ship_zip", Common.ZIP_1);
            jsonShipBody.put("ship_city", Common.CITY_1);
            jsonShipBody.put("ship_country", Common.COUNTRY_1);

            Log.d("Ship Response", String.valueOf(jsonShipBody));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        String txnId = "Cash On Delivery";

        JSONObject jsonBillBody = new JSONObject();
        try {
            jsonBillBody.put("_token", txnId);
            jsonBillBody.put("bill_first_name", Common.FIRST_NAME);
            jsonBillBody.put("bill_last_name", Common.LAST_NAME);
            jsonBillBody.put("bill_email", Common.EMAIL);
            jsonBillBody.put("bill_phone", Common.PHONE);
            jsonBillBody.put("bill_company", "null");
            jsonBillBody.put("bill_address1", Common.ADDRESS_1);
            jsonBillBody.put("bill_address2", Common.ADDRESS_2);
            jsonBillBody.put("bill_zip", Common.ZIP);
            jsonBillBody.put("bill_city", Common.CITY);
            jsonBillBody.put("bill_country", Common.COUNTRY);

            Log.d("Bill Response", String.valueOf(jsonBillBody));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        String dateTime = currentDate + " "+currentTime;


        mService.storeOrders(
                Common.CURRENT_USER.id,
                object,
                String.valueOf(shipping),
                "Cash on Delivery",
                txnId,
                orderId,
                "Pending",
                String.valueOf(jsonShipBody),
                String.valueOf(jsonBillBody),
                "UnPaid",
                dateTime,
                dateTime,
                Common.STATE
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(getContext(), "Thank you for placing your order...", Toast.LENGTH_SHORT).show();

                Common.cartRepository.emptyCart();

                startActivity(new Intent(getContext(), HomeActivity.class));
                getActivity().finish();

                sendToEmail1();
                sendToEmail2();
                sendToEmail3();

                Log.d("ResRazor", response.body() + " " + response.message());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("FailRazor", t.getMessage());
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendToEmail1() {
        String message = new StringBuilder("Hello "+Common.CURRENT_USER.first_name+" "+Common.CURRENT_USER.last_name)
                .append(Common.CURRENT_USER.email+"\n")
                .append(Common.CURRENT_USER.phone+"\n")
                .append("Your Order Has Been Placed Successfully.\n")
                .append("Order Details:\n")
                .append("Shipping Address : " +Common.ADDRESS_1_1 +" "+Common.ADDRESS_2_1 + " "+Common.CITY_1 + " "+Common.ZIP_1 +" "+Common.COUNTRY_1+"\n")
                .append("Billing Address : "+Common.ADDRESS_1 +" "+Common.ADDRESS_2 + " "+Common.CITY + " "+Common.ZIP +" "+Common.COUNTRY+"\n")
                .append("Payment Method : Cash on Delivery \n")
                .append("Total Amount : "+Common.TOTAL)
                .toString();

        JavaMailAPI mailAPI = new JavaMailAPI(
                getContext(),
                "gluonelectrical@gmail.com",
                "New Order From "+Common.CURRENT_USER.first_name,
                message);

        mailAPI.execute();
    }

    private void sendToEmail2() {
        String message = new StringBuilder("Hello "+Common.CURRENT_USER.first_name+" "+Common.CURRENT_USER.last_name)
                .append(Common.CURRENT_USER.email+"\n")
                .append(Common.CURRENT_USER.phone+"\n")
                .append("Your Order Has Been Placed Successfully.\n")
                .append("Order Details:\n")
                .append("Shipping Address : " +Common.ADDRESS_1_1 +" "+Common.ADDRESS_2_1 + " "+Common.CITY_1 + " "+Common.ZIP_1 +" "+Common.COUNTRY_1+"\n")
                .append("Billing Address : "+Common.ADDRESS_1 +" "+Common.ADDRESS_2 + " "+Common.CITY + " "+Common.ZIP +" "+Common.COUNTRY+"\n")
                .append("Payment Method : Cash on Delivery \n")
                .append("Total Amount : "+Common.TOTAL)
                .toString();

        JavaMailAPI mailAPI = new JavaMailAPI(
                getContext(),
                "sales@gluonelectrical.com",
                "New Order From "+Common.CURRENT_USER.first_name,
                message);

        mailAPI.execute();
    }

    private void sendToEmail3() {
        String message = new StringBuilder("Hello "+Common.CURRENT_USER.first_name+" "+Common.CURRENT_USER.last_name)
                .append(Common.CURRENT_USER.email+"\n")
                .append(Common.CURRENT_USER.phone+"\n")
                .append("Your Order Has Been Placed Successfully.\n")
                .append("Order Details:\n")
                .append("Shipping Address : " +Common.ADDRESS_1_1 +" "+Common.ADDRESS_2_1 + " "+Common.CITY_1 + " "+Common.ZIP_1 +" "+Common.COUNTRY_1+"\n")
                .append("Billing Address : "+Common.ADDRESS_1 +" "+Common.ADDRESS_2 + " "+Common.CITY + " "+Common.ZIP +" "+Common.COUNTRY+"\n")
                .append("Payment Method : Cash on Delivery \n")
                .append("Total Amount : "+Common.TOTAL)
                .toString();

        JavaMailAPI mailAPI = new JavaMailAPI(
                getContext(),
                Common.CURRENT_USER.email,
                "Your Order Has Been Placed Successfully.",
                message);

        mailAPI.execute();
    }

    private void displayJson(List<Cart> carts) throws JSONException {
        int size = Common.cartRepository.countCartItems();

        Log.d("CartSize", String.valueOf(size));

        JSONObject mainBody = new JSONObject();
        JSONObject body1 = new JSONObject();
        JSONObject body2 = new JSONObject();
        JSONObject body3 = new JSONObject();

        for (int i = 0; i < size; i++) {

            JSONArray array = new JSONArray();
//            body1.put("id", null);
            mainBody.put("options_id",array);

            body2.put("names", array);
            body2.put("option_name", array);
            body2.put("option_price", array);
            mainBody.put("attribute",body2);

            mainBody.put("attribute_price", "0");
            mainBody.put("name", carts.get(i).name);
            mainBody.put("slug", carts.get(i).slug);
            mainBody.put("qty", carts.get(i).qty);
            mainBody.put("price", carts.get(i).price);
            mainBody.put("main_price",carts.get(i).price);
            mainBody.put("photo", carts.get(i).photo);
            mainBody.put("item_type", carts.get(i).category);
            mainBody.put("item_l_n", JSONObject.NULL);
            mainBody.put("item_l_k", JSONObject.NULL);


//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(String.valueOf(mainBody).trim(),array);


            JSONObject jsonObject = new JSONObject(String.valueOf(mainBody));

            body3.put(carts.get(i).productId+"-", jsonObject);
        }

        String cartJson = body3.toString().trim();

//        json = cartJson.replaceAll("\"","");
//
        object = new JSONObject(cartJson);
//
        Log.d("CartJson",object.toString());
    }


    private class GenerateOrderId extends AsyncTask<Void,Void,Void> {

        float a = (Float.parseFloat(Common.TOTAL));
        int b = Math.round(a);

        int amount = b * 100;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                RazorpayClient razorpay = new RazorpayClient(
                        getString(R.string.razor_key_id),
                        getString(R.string.razor_key_secret)
                );

                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", String.valueOf(amount)); // amount in the smallest currency unit
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "order_rcptid_11");

                Order order = razorpay.orders.create(orderRequest);

                orderId = order.get("id");

                Log.d("OrderID", order.get("id"));
            }
            catch (Exception e) {
                // Handle Exception
                System.out.println(e.getMessage());
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
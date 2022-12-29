package com.ihuntech.healthbizz.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ihuntech.healthbizz.Common.Common;
import com.ihuntech.healthbizz.Model.UserResponse;
import com.ihuntech.healthbizz.R;
import com.ihuntech.healthbizz.Remote.IMyAPI;

import am.appwise.components.ni.NoInternetDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText edtFName, edtLName, edtEmail,edtPhone, edtPassword, edtCnfPassword;

    IMyAPI mService;

    NoInternetDialog noInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        noInternetDialog = new NoInternetDialog.Builder(this).build();


        mService = Common.getAPI();

        edtFName = findViewById(R.id.edtFirst);
        edtLName = findViewById(R.id.edtLast);
        edtEmail = findViewById(R.id.edtEmail);

        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtCnfPassword = findViewById(R.id.edtCnfPassword);


        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = edtFName.getText().toString().trim();
                String lastName = edtLName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String confirm = edtCnfPassword.getText().toString().trim();

                if (TextUtils.isEmpty(firstName))   {
                    Toast.makeText(RegisterActivity.this, "Please Enter First Name", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(lastName))   {
                    Toast.makeText(RegisterActivity.this, "Please Enter Last Name", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(email))   {
                    Toast.makeText(RegisterActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(phone))   {
                    Toast.makeText(RegisterActivity.this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password))   {
                    Toast.makeText(RegisterActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(confirm))   {
                    Toast.makeText(RegisterActivity.this, "Please Enter Confirm password.", Toast.LENGTH_SHORT).show();
                }
                else if (!password.equals(confirm))  {
                    Toast.makeText(RegisterActivity.this, "Both Password doesn't match..", Toast.LENGTH_SHORT).show();
                }
                else    {
                    registerUser(firstName,lastName,email,phone,password);
                }

            }
        });

//        findViewById(R.id.txtLogin).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//                finish();
//            }
//        });
    }

    private void registerUser(String firstName, String lastName, String email, String phone, String password) {
        mService.registerUser(firstName, lastName, phone, email, password)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        UserResponse result = response.body();

                        if (result.isError())   {
                            SweetAlertDialog alertDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE);
                            alertDialog.setTitleText("");
                            alertDialog.setContentText(result.getError_msg());
                            alertDialog.setCancelable(false);

                            alertDialog.setConfirmText("Continue...");
                            alertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            });
                            alertDialog.show();

                        }
                        else    {
                            SweetAlertDialog alertDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                            alertDialog.setTitleText("");
                            alertDialog.setContentText("User Registered Successfully");
                            alertDialog.setCancelable(false);

                            alertDialog.setConfirmText("Continue...");
                            alertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
                            alertDialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}
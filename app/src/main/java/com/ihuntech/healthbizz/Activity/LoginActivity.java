package com.ihuntech.healthbizz.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.ihuntech.healthbizz.Common.Common;
import com.ihuntech.healthbizz.MainActivity;
import com.ihuntech.healthbizz.Model.UserResponse;
import com.ihuntech.healthbizz.R;
import com.ihuntech.healthbizz.Remote.IMyAPI;

import am.appwise.components.ni.NoInternetDialog;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;

    CheckBox ckbRemember;

    IMyAPI mService;

    NoInternetDialog noInternetDialog;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        noInternetDialog = new NoInternetDialog.Builder(this).build();

        mService = Common.getAPI();

        Paper.init(this);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword  = findViewById(R.id.edtPassword);

        ckbRemember = findViewById(R.id.ckbRemember);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email))   {
                    Toast.makeText(LoginActivity.this, "Enter Email...", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password))   {
                    Toast.makeText(LoginActivity.this, "Enter Password...", Toast.LENGTH_SHORT).show();
                }
                else    {
                    login(email,password);
                }
            }
        });

        findViewById(R.id.txtForgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this,ForgotActivity.class));
            }
        });

        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    private void login(String email, String password) {
        mService.loginUser(email,password)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        UserResponse result = response.body();

                        if (result.isError())   {
                            Toast.makeText(LoginActivity.this, ""+result.getError_msg(), Toast.LENGTH_SHORT).show();
                        }
                        else    {
                            if (ckbRemember.isChecked())    {
                                Paper.book().write(Common.EMAIL_KEY, email);
                                Paper.book().write(Common.PWD_KEY, password);
                            }

                            Toast.makeText(LoginActivity.this, "Login Success!!!", Toast.LENGTH_SHORT).show();
                            Common.CURRENT_USER = result.getUser();

                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}
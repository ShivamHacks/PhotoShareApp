package com.example.shivamagrawal.photoshareapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shivamagrawal.photoshareapp.Objects.ContactsHelper;
import com.example.shivamagrawal.photoshareapp.Objects.ResponseHandler;
import com.example.shivamagrawal.photoshareapp.Objects.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText phoneNumber;
    EditText password;
    EditText verificationCode;
    Button loginSubmit;
    Button loginVerify;
    Context context;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        phoneNumber = (EditText) findViewById(R.id.login_phone_number);
        password = (EditText) findViewById(R.id.login_password);
        verificationCode = (EditText) findViewById(R.id.login_verification_code);

        loginSubmit = (Button) findViewById(R.id.login_submit);
        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()) {
                    submit();
                }
            }
        });

        loginVerify = (Button) findViewById(R.id.login_verify);
        loginVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });
    }

    private boolean check() {
        boolean allGood = true;
        // check if any fields are empty
        if (TextUtils.isEmpty(phoneNumber.getText().toString().trim())
                || TextUtils.isEmpty(password.getText().toString().trim())) {
            allGood = false;
        }
        return allGood;
    }

    private void submit() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("phoneNumber", ContactsHelper
                .internationalize(phoneNumber.getText().toString()));
        params.put("password", password.getText().toString());

        StringRequest sr = Server.POST(params, Server.loginURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        JSONObject body = new ResponseHandler(context, res).parseRes();
                        if (body != null) {
                            try {
                                userID = body.getString("userID");
                            } catch (JSONException e) {
                                ResponseHandler.errorToast(context, "An error occured");
                                e.printStackTrace();
                            }
                        } else { ResponseHandler.errorToast(context, "An error occured"); }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ResponseHandler.errorToast(context, "An error occured");
                    }
                }
        );
        Server.makeRequest(context, sr);
    }

    private void verify() {
        if (TextUtils.isEmpty(verificationCode.getText().toString().trim()) || userID.equals("")) {
            ResponseHandler.errorToast(context, "An error occured");
        } else {

            Map<String, String> params = new HashMap<String, String>();
            params.put("phoneNumber", ContactsHelper
                    .internationalize(phoneNumber.getText().toString()));
            params.put("verificationCode", verificationCode.getText().toString());
            params.put("userID", userID);
            params.put("intent", "login");

            StringRequest sr = Server.POST(params, Server.verifyURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String res) {
                            JSONObject body = new ResponseHandler(context, res).parseRes();
                            if (body != null) {
                                try {
                                    Log.d("TOKEN", body.getString("token"));
                                    saveToken(body.getString("token"));
                                } catch (JSONException e) {
                                    ResponseHandler.errorToast(context, "An error occured");
                                    e.printStackTrace();
                                }
                            } else { ResponseHandler.errorToast(context, "An error occured"); }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            ResponseHandler.errorToast(context, "An error occured");
                        }
                    }
            );
            Server.makeRequest(context, sr);
        }
    }

    private void saveToken(String token) {
        SharedPreferences sharedPref = this.getSharedPreferences("main", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.putBoolean("loggedIn", true);
        editor.commit();
        finish();
    }
}

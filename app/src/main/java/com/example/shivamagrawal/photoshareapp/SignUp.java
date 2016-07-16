package com.example.shivamagrawal.photoshareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.text.TextUtils;
import android.util.Log;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import java.util.Map;
import java.util.HashMap;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;

// TODO: server stuff in async

public class SignUp extends AppCompatActivity {

    EditText phoneNumber;
    EditText password1;
    EditText password2;
    EditText vertificationCode;
    Button signUpSubmit;
    Button signUpVerify;

    String userID = "";
    String countryISO = "";
    String internationalNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        phoneNumber = (EditText) findViewById(R.id.signup_phone_number);
        password1 = (EditText) findViewById(R.id.signup_password_1);
        password2 = (EditText) findViewById(R.id.signup_password_2);
        vertificationCode = (EditText) findViewById(R.id.signup_verification_code);

        signUpSubmit = (Button) findViewById(R.id.signup_submit);
        signUpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()) {
                    submit();
                }
            }
        });

        signUpVerify = (Button) findViewById(R.id.signup_verify);
        signUpVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });

        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        countryISO = tm.getSimCountryIso();

    }

    private boolean check() {
        boolean allGood = true;
        // check if passwords match
        if (!(password1.getText().toString().equals(password2.getText().toString()))) {
            allGood = false;
        }
        // check if any fields are empty
        if (TextUtils.isEmpty(phoneNumber.getText().toString().trim())
                || TextUtils.isEmpty(password1.getText().toString().trim())
                || TextUtils.isEmpty(password2.getText().toString().trim())) {
            allGood = false;
        }
        Log.d("CHECK", Boolean.toString(allGood));
        return allGood;
    }

    private void submit() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("phoneNumber", phoneNumber.getText().toString());
        params.put("password", password1.getText().toString());
        params.put("countryISO", countryISO);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = Server.POST(params, Server.signupURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject result = new JSONObject(s);
                            if (result.has("_id")) {
                                userID = result.getString("_id");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("SIGNUP", volleyError.getMessage());
                    }
                }
        );

        queue.add(sr);
    }

    private void verify() {
        if (TextUtils.isEmpty(vertificationCode.getText().toString().trim()) || userID.equals("")) {
            Log.d("ERROR", "LOL WAT");
        } else {

            Map<String, String> params = new HashMap<String, String>();
            params.put("phoneNumber", phoneNumber.getText().toString());
            params.put("verficationCode", vertificationCode.getText().toString());
            params.put("userID", userID);
            params.put("countryISO", countryISO);

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest sr = Server.POST(params, Server.verifyURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject result = new JSONObject(s);
                                saveToken(result.getString("token"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.d("VERIFY", volleyError.getMessage());
                        }
                    }
            );

            queue.add(sr);

        }
    }

    private void saveToken(String token) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("token", token);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

}

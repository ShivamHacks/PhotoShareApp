package com.example.shivamagrawal.photoshareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.util.Log;

public class LoginOrSignUp extends AppCompatActivity {

    Button login;
    Button signup;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_sign_up);
        context = this.getApplicationContext();

        login = (Button) findViewById(R.id.login);
        signup = (Button) findViewById(R.id.signup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logIn = new Intent(context, Login.class);
                startActivity(logIn);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUp = new Intent(context, SignUp.class);
                startActivityForResult(signUp, Statics.tokenResultCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == Statics.tokenResultCode) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                SharedPreferences sharedPref = this.getSharedPreferences("main", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Log.d("TOKEN 1", data.getStringExtra("token"));
                editor.putString("token", data.getStringExtra("token"));
                editor.commit();
                finish();
            }
        }
    }
}

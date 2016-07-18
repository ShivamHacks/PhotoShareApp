package com.example.shivamagrawal.photoshareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity extends AppCompatActivity {

    LinearLayout membersListLayout;
    LayoutInflater inflater;
    Button addButton;
    Button submitButton;

    List<String> members = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        membersListLayout = (LinearLayout) findViewById(R.id.members_list);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        addButton = (Button) findViewById(R.id.add_member_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText memberET = (EditText) inflater.inflate(R.layout.add_member_edittext, null);
                membersListLayout.addView(memberET);
            }
        });

        submitButton = (Button) findViewById(R.id.submit_new_group_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    private void submit() {
        for (int i = 0; i < membersListLayout.getChildCount(); i++) {
            EditText member = (EditText) membersListLayout.getChildAt(i);
            members.add(member.getText().toString());
            Log.d("MEMBER", member.getText().toString());
        }
    }
}

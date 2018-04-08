package com.hbln.smsintercept.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hbln.smsintercept.R;

public class ErrorActivity extends AppCompatActivity {
    public static void start(Context context) {
        Intent starter = new Intent(context, ErrorActivity.class);
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
    }
}

package com.gcit.smssend.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.gcit.smssend.R;
import com.gcit.smssend.base.BaseActivity;

public class MobileActivity extends BaseActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);
        setupActionBar();
    }
    
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}

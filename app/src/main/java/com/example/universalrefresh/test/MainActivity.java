package com.example.universalrefresh.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.universalrefresh.R;

public class MainActivity extends Activity implements View.OnClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_test_list_view).setOnClickListener(this);
        findViewById(R.id.btn_test_scroll_view).setOnClickListener(this);
        findViewById(R.id.btn_test_recycler_view).setOnClickListener(this);
	}

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_test_list_view) {
            startActivity(new Intent(this, TestListViewActivity.class));
        } else if (v.getId() == R.id.btn_test_scroll_view){
            startActivity(new Intent(this, TestScrollViewActivity.class));
        }
    }
}
package com.sudrives.sudrives.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.utils.GlobalUtil;


public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        back = findViewById(R.id.back_about);
        back.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_about:


                  finish();

            break;
        }
    }
}
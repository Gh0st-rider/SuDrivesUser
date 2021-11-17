package com.sudrives.sudrives.activity;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.utils.GlobalUtil;

public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener {


    ImageButton back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us_activity);

        back = findViewById(R.id.iv_contact_us);
        back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_contact_us:


                finish();

                break;
        }
    }
}


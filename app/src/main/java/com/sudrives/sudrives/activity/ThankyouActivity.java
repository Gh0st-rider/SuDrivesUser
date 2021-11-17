package com.sudrives.sudrives.activity;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ActivityThankyouBinding;
import com.sudrives.sudrives.utils.GlobalUtil;

public class ThankyouActivity extends AppCompatActivity {
    ActivityThankyouBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_thankyou);


        GlobalUtil.setStatusBarColor(ThankyouActivity.this, getResources().getColor(R.color.colorPrimaryDark));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(ThankyouActivity.this, Splash.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }


}

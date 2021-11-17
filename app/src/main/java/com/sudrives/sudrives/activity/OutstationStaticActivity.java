package com.sudrives.sudrives.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sudrives.sudrives.R;

public class OutstationStaticActivity extends AppCompatActivity {
    private TextView tv_Phone, tv_bookrental;
    private String outstationString = "Are you trying to book an Outstation Ride?\n" +
            "Yes we are accepting Outstation rides offline.";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outstation_static);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_Phone = findViewById(R.id.tv_Phone);
        tv_bookrental = findViewById(R.id.tv_bookrental);

        if (getIntent().getExtras() != null){
            outstationString = getIntent().getStringExtra("stringout");
            tv_bookrental.setText(outstationString);

        }else {
            tv_bookrental.setText(outstationString);

        }


        tv_Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+917683055008"));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
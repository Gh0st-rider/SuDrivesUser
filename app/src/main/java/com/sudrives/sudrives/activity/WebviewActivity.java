package com.sudrives.sudrives.activity;

import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sudrives.sudrives.R;

public class WebviewActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton back;
    TextView tvTitle;
    String tool_title,values = "";

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);



        if (getIntent().getExtras() != null){
            tool_title = getIntent().getExtras().getString("tool_title");
            values = getIntent().getExtras().getString("value");
        }

        Log.e("dataaa",values);

        back = findViewById(R.id.back_webview);
        back.setOnClickListener(this);


        tvTitle = findViewById(R.id.tv_title_webview);
        tvTitle.setText(tool_title);

        webView = findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new SSLTolerentWebViewClient());
        webView.loadUrl(values);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_webview:


                finish();

                break;
        }
    }

    private class SSLTolerentWebViewClient extends WebViewClient {
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

            AlertDialog.Builder builder = new AlertDialog.Builder(WebviewActivity.this);
            AlertDialog alertDialog = builder.create();
            String message = "SSL Certificate error.";
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = "The certificate authority is not trusted.";
                    break;
                case SslError.SSL_EXPIRED:
                    message = "The certificate has expired.";
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = "The certificate Hostname mismatch.";
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = "The certificate is not yet valid.";
                    break;
            }

            message += " Do you want to continue anyway?";
            alertDialog.setTitle("SSL Certificate Error");
            alertDialog.setMessage(message);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Ignore SSL certificate errors
                    handler.proceed();
                }
            });

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    handler.cancel();
                }
            });
            alertDialog.show();
        }
    }
}




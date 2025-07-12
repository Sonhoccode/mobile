package com.example.fastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fastfood.R;

public class VnPayActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay);

        WebView webView = findViewById(R.id.webview);
        String paymentUrl = getIntent().getStringExtra("paymentUrl");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO: Xử lý khi VNPay trả về Return URL
                if (url.contains("vnpay_return")) {
                    // Thanh toán hoàn tất, đóng activity và có thể gửi kết quả về
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("isSuccess", true); // Gửi cờ thành công
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        if (paymentUrl != null) {
            webView.loadUrl(paymentUrl);
        }
    }
}
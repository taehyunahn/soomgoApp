package com.example.soomgodev.Fragment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.soomgodev.R;

public class ImageSliderWebView extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider_web_view);
        getSupportActionBar().hide();

        // 웹뷰 셋팅
        mWebView = (WebView) findViewById(R.id.webView); // xml 자바코드 연결
        mWebView.getSettings().setJavaScriptEnabled(true); // 자바스크립트 허용

        Intent intent = getIntent();
        String imageLink = intent.getStringExtra("imageDetail");
        mWebView.loadUrl(imageLink); // 웹뷰 실행
        mWebView.setWebChromeClient(new WebChromeClient()); // 웹뷰에 크롬 사용 허용 // 이 부분이 없으면 크롬에서 alert가 뜨지 않음
        mWebView.setWebViewClient(new WebViewClientClass()); //새창열기 없이 웹뷰 내에서 다시 열기 // 페이지 이동 원활히 하기 위해 사용

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("check URL", url);
            view.loadUrl(url);
            return true;
        }
    }
}
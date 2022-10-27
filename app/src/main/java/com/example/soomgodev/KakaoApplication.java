package com.example.soomgodev;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this, "d129c1e4eee464f7d896f3f48115fb58");
    }
}

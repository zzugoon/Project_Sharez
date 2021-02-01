package com.ex.project_sharez;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this,"6d25aa609651cad179609c5397794636");

    }
}

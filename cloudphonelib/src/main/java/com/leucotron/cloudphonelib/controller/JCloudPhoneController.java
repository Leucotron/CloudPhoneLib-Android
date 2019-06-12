package com.leucotron.cloudphonelib.controller;

import android.content.Context;
import android.content.Intent;

import com.leucotron.cloudphonelib.view.JCloudPhoneWebActivity;

public class JCloudPhoneController {

    private Context context = null;
    private String url = null;

    public JCloudPhoneController(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    public void openCloudPhoneActivity() {
        Intent intent = new Intent(context, JCloudPhoneWebActivity.class);
        intent.putExtra("url",url);
        context.startActivity(intent);
    }
}

package com.leucotron.cloudphonelib.controller;

import android.content.Context;
import android.content.Intent;

import com.leucotron.cloudphonelib.view.JCloudPhoneWebActivity;

public class JCloudPhoneController {

    private Context context;
    private String url = null;

    public JCloudPhoneController(Context context) {
        this.context = context;
    }

    public boolean openCloudPhoneActivity(String url) {
        boolean returnValue = false;
        this.url = url;
        if (isValidUrl()) {
            Intent intent = new Intent(context, JCloudPhoneWebActivity.class);
            intent.putExtra("url", url);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        return returnValue;
    }

    private boolean isValidUrl() {
        boolean returnValue = true;
        if (!url.contains("bit.ly") && !url.contains("cloud-phone") && !url.contains("cloudphone")) {
            returnValue = false;
        }
        return returnValue;
    }


}

package com.leucotron.cloudphonelib.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.leucotron.cloudphonelib.R;

public class JCloudPhoneWebActivity extends AppCompatActivity implements SensorEventListener {

    private String url = null;

    private WebView webView = null;
    private static final int TIME_INTERVAL = 3000;
    private long backPressedTime;

    private SensorManager sensorManager;
    private Sensor proximity;
    private static final int SENSOR_SENSITIVITY = 4;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloudphoneweb);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        initUi();
        initWakeLock();
        initSensor();
        initPermissions();

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
            return;
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.backpressed_message), Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Conceder a permissão para uso do Microfone é obrigatória para o funcionamento da chamada.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            startConnection();
        }
    }

    private void initUi() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        webView = findViewById(R.id.webView);
    }

    private void initPermissions() {
        int requestPermissionCode = 1001;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, requestPermissionCode);
        } else {
            startConnection();
        }
    }

    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(getApplicationContext().SENSOR_SERVICE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    private void initWakeLock() {
        try {
            // Yeah, this is hidden field.
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getLocalClassName());
    }

    private void startConnection() {

        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(webView, true);
        webView.getSettings().setUserAgentString("cloudphone-android");
        webView.setWebChromeClient(new MyWebChromeViewClient());
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(url);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float distance = event.values[0];
        if (distance >= -SENSOR_SENSITIVITY && distance <= SENSOR_SENSITIVITY) {
            if(!wakeLock.isHeld()) {
                wakeLock.acquire();
            }
        } else {
            if(wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class MyWebChromeViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {
            String[] resources = request.getResources();
            request.grant(resources);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.contains("bit.ly") && !url.contains("cloud-phone") && !url.contains("cloudphone")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse(url);
                intent.setData(data);
                startActivity(intent);
                return true;
            } else {
                return super.shouldOverrideUrlLoading(view, url);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!view.getTitle().contains("Cloud Phone") && !view.getTitle().contains("Atendimento")) {
                finish();
                Toast.makeText(getApplicationContext(), getString(R.string.service_unavailable), Toast.LENGTH_LONG).show();
            }
        }
    }

}

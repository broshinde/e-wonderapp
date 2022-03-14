package com.ewondercourse.ewonderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.ewondercourse.ewonderapp.R;
import com.ewondercourse.ewonderapp.config.UiConfig;
import com.ewondercourse.ewonderapp.utils.ThemePref;
import com.ewondercourse.ewonderapp.utils.Tools;

public class ActivitySplash extends AppCompatActivity {

    Boolean isCancelled = false;
    private ProgressBar progressBar;
    long nid = 0;
    String url = "";
    ThemePref themePref;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_splash);


        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if (getIntent().hasExtra("nid")) {
            nid = getIntent().getLongExtra("nid", 0);
            url = getIntent().getStringExtra("external_link");
        }

        new Handler().postDelayed(() -> {
            if (!isCancelled) {
                if (nid == 0) {
                    if (url.equals("") || url.equals("no_url")) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Intent intent = new Intent(getApplicationContext(), ActivityWebView.class);
                        intent.putExtra("url", url);
                        startActivity(intent);

                        finish();
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    Intent intent = new Intent(getApplicationContext(), ActivityNotificationDetail.class);
                    intent.putExtra("id", nid);
                    startActivity(intent);
                    finish();
                }
            }
        }, UiConfig.SPLASH_TIME);

    }
}

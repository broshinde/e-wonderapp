package com.ewondercourse.ewonderapp.activities;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ewondercourse.ewonderapp.R;
import com.ewondercourse.ewonderapp.callbacks.CallbackSettings;
import com.ewondercourse.ewonderapp.config.UiConfig;
import com.ewondercourse.ewonderapp.models.Setting;
import com.ewondercourse.ewonderapp.rests.ApiInterface;
import com.ewondercourse.ewonderapp.rests.RestAdapter;
import com.ewondercourse.ewonderapp.utils.Constant;
import com.ewondercourse.ewonderapp.utils.NetworkCheck;
import com.ewondercourse.ewonderapp.utils.ThemePref;
import com.ewondercourse.ewonderapp.utils.Tools;
import com.facebook.shimmer.ShimmerFrameLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPrivacyPolicy extends AppCompatActivity {

    private SwipeRefreshLayout swipe_refresh;
    Call<CallbackSettings> callbackCall = null;
    private ShimmerFrameLayout lyt_shimmer;
    private String bg_paragraph;
    ThemePref themePref;
    Setting post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_privacy_policy);

        themePref = new ThemePref(this);

        swipe_refresh = findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary);
        lyt_shimmer = findViewById(R.id.shimmer_view_container);

        requestAction();

        swipe_refresh.setOnRefreshListener(this::requestAction);

        setupToolbar();

    }

    public void setupToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (themePref.getIsDarkTheme()) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorToolbarDark));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.title_about_privacy));
        }
    }

    private void requestAction() {
        showFailedView(false, "");
        swipeProgress(true);
        new Handler().postDelayed(this::requestDetailsPostApi, Constant.DELAY_TIME);
    }

    private void requestDetailsPostApi() {
        ApiInterface api = RestAdapter.createAPI();
        callbackCall = api.getSettings();
        callbackCall.enqueue(new Callback<CallbackSettings>() {
            @Override
            public void onResponse(Call<CallbackSettings> call, Response<CallbackSettings> response) {
                CallbackSettings resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    post = resp.post;
                    displayPostData();
                    swipeProgress(false);
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackSettings> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest();
            }

        });
    }

    private void onFailRequest() {
        swipeProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.msg_no_network));
        } else {
            showFailedView(true, getString(R.string.msg_offline));
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = findViewById(R.id.lyt_failed);
        RelativeLayout lyt_main_content = findViewById(R.id.lyt_main_content);

        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_main_content.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_main_content.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        (findViewById(R.id.failed_retry)).setOnClickListener(view -> requestAction());
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(show);
            lyt_shimmer.setVisibility(View.GONE);
            lyt_shimmer.stopShimmer();
            return;
        }
        swipe_refresh.post(() -> {
            swipe_refresh.setRefreshing(show);
            lyt_shimmer.setVisibility(View.VISIBLE);
            lyt_shimmer.startShimmer();
        });
    }

    public void displayPostData() {

        WebView webView = findViewById(R.id.webview_privacy_policy);
        webView.setBackgroundColor(Color.parseColor("#ffffff"));
        webView.setFocusableInTouchMode(false);
        webView.setFocusable(false);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");

        WebSettings webSettings = webView.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = post.privacy_policy;

        if (themePref.getIsDarkTheme()) {
            webView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBackgroundDark));
            bg_paragraph = "<style type=\"text/css\">body{color: #eeeeee;}";
        } else {
            bg_paragraph = "<style type=\"text/css\">body{color: #000000;}";
        }

        String text_default = "<html><head>"
                + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                + bg_paragraph
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        String text_rtl = "<html dir='rtl'><head>"
                + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                + bg_paragraph
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        if (UiConfig.ENABLE_RTL_MODE) {
            webView.loadDataWithBaseURL(null, text_rtl, mimeType, encoding, null);
        } else {
            webView.loadDataWithBaseURL(null, text_default, mimeType, encoding, null);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

}

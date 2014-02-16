package sg.vista;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androauth.oauth.OAuth10Service;
import com.androauth.oauth.OAuth10Token;
import com.twotoasters.android.hoot.HootResult;

public class TwitterAuth extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_auth);
        // Show the Up button in the action bar.
        setupActionBar();
        getOAuthToken();
    }

    public void getOAuthToken() {
    	Log.i("", "Getting token");
        final WebView webview = (WebView) findViewById(R.id.webView);
        final Twitter t = Twitter.getInstance();
        final OAuth10Service service = t.service;
        service.setoAuthCallback(new OAuth10Service.OAuth10ServiceCallback() {
            @Override
            public void onOAuthRequestTokenReceived() {
                webview.loadUrl(service.getAuthorizeUrl());
            }

            @Override
            public void onOAuthRequestTokenFailed(HootResult hootResult) {
            	Log.i("",hootResult.getResponseString());
            }

            @Override
            public void onOAuthAccessTokenReceived(OAuth10Token oAuth10Token) {
                t.setToken(oAuth10Token);
                Intent result = new Intent();
                oAuth10Token.getAccessToken();
                result.putExtra("access_token", oAuth10Token.getAccessToken());
                result.putExtra("user_secret", oAuth10Token.getUserSecret());
                setResult(RESULT_OK, result);
                finish();
            }

            @Override
            public void onOAuthAccessTokenFailed(HootResult hootResult) {
            	Log.i("","Failed");

            }
        });
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                // Checking for our successful callback
                if(url.startsWith("tipple://")) {
                    webview.setVisibility(View.GONE);
                    service.getOAuthAccessToken(url);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

        });
        service.start();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
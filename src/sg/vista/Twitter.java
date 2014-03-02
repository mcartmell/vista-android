package sg.vista;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import android.webkit.WebView;

import com.androauth.api.TwitterApi;
import com.androauth.oauth.OAuth10Request;
import com.androauth.oauth.OAuth10Service;
import com.androauth.oauth.OAuth10Token;
import com.androauth.oauth.OAuth20Token;
import com.androauth.oauth.OAuthRequest;
import com.androauth.oauth.OAuthRequest.OnRequestCompleteListener;
import com.androauth.oauth.OAuthService;
import com.twotoasters.android.hoot.HootResult;


public class Twitter {

    OAuth10Service service;
    OAuth10Token token;
    WebView webview;

    private static final Twitter instance = new Twitter();

    public void setToken(OAuth10Token token) {
        this.token = token;
    }

    public boolean hasToken() {
        return (token != null);
    }

    public void setWebView(WebView wv) {
        webview = wv;
    }
    
    // Send a tweet to Twitter with exact latitude & longitude
    public void postTweet(String tweet) {
    	if (!hasToken()) return;
        String baseUrl = "https://api.twitter.com/1.1/statuses/update.json";
        Twitter t = Twitter.getInstance();
        OAuth10Request request = OAuthRequest.newInstance(baseUrl, t.token, t.service, new OnRequestCompleteListener() {

            @Override
            public void onSuccess(HootResult result) {
            }

            @Override
            public void onNewAccessTokenReceived(OAuth20Token token) {
            }

            @Override
            public void onFailure(HootResult result) {
            }
        });
        
        Map<String,String> queryParameters = new HashMap<String,String>();
        queryParameters.put("status", tweet);
        queryParameters.put("lat", Vista.latitude());
        queryParameters.put("long", Vista.longitude());
        queryParameters.put("display_coordinates", "true");

        request.setRequestParams(queryParameters);
        request.post();
    }
    
    private Twitter() {
        service = OAuthService.newInstance(new TwitterApi(), VistaApiKey.API_CONSUMER_KEY, VistaApiKey.API_CONSUMER_SECRET, null);
        service.setApiCallback("tipple://auth");
    }

    public static Twitter getInstance() {
        return instance;
    }
}
package sg.vista;

import com.androauth.oauth.OAuth10Token;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class VistaFragmentActivity extends FragmentActivity {
	public static final String PREFS_NAME = "VistaPrefs";

	public SharedPreferences config() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		return settings;
	}
    public SharedPreferences prefs() {
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    	return sp;
    }
	public Context c() {
		return getApplicationContext();
	}

	public SharedPreferences.Editor getEditor() {
		return config().edit();
	}

	public void saveTwitterToken(Intent data) {
		Log.v("TwitterAuth", "Saved Twitter access token in preferences");
		String token = data.getStringExtra("access_token");
		String key = data.getStringExtra("user_secret");
		// save key and things
		SharedPreferences.Editor editor = getEditor();
		editor.putString("twitter_access_token", token);
		editor.putString("twitter_user_secret", key);
		editor.commit();
		setTwitterToken();
	}
	
	public void setTwitterToken() {
		if (config().contains("twitter_access_token")) {
            Twitter.getInstance().setToken(new OAuth10Token(config().getString("twitter_access_token", ""), config().getString("twitter_user_secret", "")));
            Log.v("TwitterAuth", "Found Twitter access token in preferences");
		}
	}
}

package sg.vista;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class VistaActivity extends ActionBarActivity {
    public static final String PREFS_NAME = "VistaPrefs";
    public SharedPreferences config() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        return settings;
    }
    
    public Context c() {
    	return getApplicationContext();
    }
    
    public SharedPreferences prefs() {
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    	return sp;
    }
    public SharedPreferences.Editor getEditor() {
    	return config().edit();
    }
    
    public Boolean readToken() {
		SharedPreferences cfg = config();
		String token = cfg.getString("user_token", null);
		String email = cfg.getString("user_email", null);
		if (token == null || email == null) {
			return false;
		}
		else {
			// Set the token in our REST client
			Log.i("Auth", "Found token in saved state");
			Vista.setToken(email, token);
			return true;
		}   	
    }
    
    public void saveToken(String email, String token) {
		SharedPreferences conf = config();
		SharedPreferences.Editor ed = conf.edit();
		ed.putString("user_email", email);
		ed.putString("user_token", token);
		ed.commit();
		Log.i("Auth", "Saved token " + token);
    }
}

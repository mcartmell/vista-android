package sg.vista;

import org.json.JSONException;
import org.json.JSONObject;

import sg.vista.Vista.VistaResponse;

import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends VistaActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (readToken()) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            return;
        }
        
        // setting default screen to login.xml
        setContentView(R.layout.login_form);
 
        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
 
        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }
    
    public void login(View v) {
    	EditText email = (EditText) findViewById(R.id.login_email);
    	final String em = email.getText().toString();
    	EditText password = (EditText) findViewById(R.id.login_pass);
    	String pass = password.getText().toString();
    	RequestParams rp = new RequestParams();
    	rp.put("email", em);
    	rp.put("password", pass);
    	final VistaActivity a = this;
    	Vista.post("/api/v1/login.json", rp, new VistaResponse() {

			@Override
			public void onResponse(JSONObject json) throws JSONException {
				// Save the user's token
				String token = json.getString("auth_token");
				a.saveToken(em, token);
				
				// Start main activity
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
			}
    	}
    	);
    }
}

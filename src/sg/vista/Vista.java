package sg.vista;
import java.lang.reflect.InvocationTargetException;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.Header;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.*;

public class Vista {
	public static String userEmail = null;
	public static String userToken = null;
	public static final int DEFAULT_TIMEOUT = 20 * 1000;

	private static AsyncHttpClient getClient() {
		AsyncHttpClient ah = new AsyncHttpClient();
		ah.setTimeout(DEFAULT_TIMEOUT);
		ah.addHeader("X-User-Email", userEmail);
		ah.addHeader("X-User-Token", userToken);
		return ah;
	}
	
	public static void setToken(String email, String token) {
		userEmail = email;
		userToken = token;
	}
	
	public static void request(final Context ctx, String method, String path, RequestParams rp, final VistaResponse cb) {
		AsyncHttpClient client = getClient();

		//String host = "http://192.168.1.2:3000";
		String host = "http://vista.herokuapp.com";
		Class[] partypes = new Class[]{String.class, RequestParams.class, ResponseHandlerInterface.class};
		try {
			client.getClass().getMethod(method, partypes).invoke(client, host + path, rp, new AsyncHttpResponseHandler() {
			    @Override
			    public void onSuccess(String response) {
			    	JSONObject json;
					Log.d("Got response", response);
			    	try {
						json = new JSONObject(response);
				        cb.onResponse(json);
					} catch (JSONException e) {
						Log.d("JSON", e.getMessage());
				    	Toast toast = Toast.makeText(ctx, "Error talking to server.", Toast.LENGTH_SHORT);
				    	toast.show();
					}
			    }
			    
			    @Override
			    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
			    	Toast toast = Toast.makeText(ctx, "Failed to connect. Try again later.", Toast.LENGTH_SHORT);
			    	toast.show();
			    }
			});
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void get(Context ctx, String path, RequestParams params, final VistaResponse cb) {
		request(ctx, "get", path, params, cb);
	}
	
	public static void post(Context ctx, String path, RequestParams params, final VistaResponse cb) {
		request(ctx, "post", path, params, cb);
	}
	
	public interface VistaResponse {
		public void onResponse(JSONObject json) throws JSONException;
	}
}



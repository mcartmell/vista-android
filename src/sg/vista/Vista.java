package sg.vista;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.Header;
import com.loopj.android.http.*;

public class Vista {
	public static String userEmail = null;
	public static String userToken = null;
	
	private static AsyncHttpClient getClient() {
		AsyncHttpClient ah = new AsyncHttpClient();
		ah.addHeader("X-User-Email", userEmail);
		ah.addHeader("X-User-Token", userToken);
		return ah;
	}
	
	public static void setToken(String email, String token) {
		userEmail = email;
		userToken = token;
	}
	
	public static void get(String path, RequestParams params, final VistaResponse cb) {
	AsyncHttpClient client = getClient();
	String host = "http://192.168.1.6:3000";
	// String host = "http://vista.herokuapp.com";
	client.get(host + path, params, new AsyncHttpResponseHandler() {
	    @Override
	    public void onSuccess(String response) {
	    	JSONObject json;
			try {
				json = new JSONObject(response);
		        cb.onResponse(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    @Override
	    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
	    	System.out.println("Oh no!" + Integer.toString(statusCode));
	    }
	});
	}
	
	public static void post(String path, RequestParams params, final VistaResponse cb) {
	AsyncHttpClient client = getClient();
	String host = "http://192.168.1.6:3000";
	//String host = "http://vista.herokuapp.com";
	client.post(host + path, params, new AsyncHttpResponseHandler() {
	    @Override
	    public void onSuccess(String response) {
	    	JSONObject json;
			try {
				json = new JSONObject(response);
		        cb.onResponse(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    @Override
	    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
	    	System.out.println("Oh no!" + Integer.toString(statusCode));
	    }
	});
	}
	public interface VistaResponse {
		public void onResponse(JSONObject json) throws JSONException;
	}
}



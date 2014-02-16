package sg.vista;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.RequestParams;

import sg.vista.Vista.VistaResponse;
import sg.vista.TwitterAuth;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
	public View mTwitterButton;
	
	public ProfileFragment() {
	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mTwitterButton = rootView.findViewById(R.id.btn_twitter_connect);
        // rootView.setTag(getArguments().getInt("section_number"));
        return rootView;
    }
    
    public void refresh() {
    	RequestParams rp = new RequestParams();
    	// Hide Twitter button if already connected to Twitter
    	if (false && Twitter.getInstance().hasToken()) {
    		mTwitterButton.setVisibility(View.GONE);
    	}
    	else {
    		mTwitterButton.setVisibility(View.VISIBLE);
    	}
    	Vista.get(getActivity().getApplicationContext(), "/user/profile", rp, new VistaResponse() {
			@Override
			public void onResponse(JSONObject json) throws JSONException {
				// TODO Auto-generated method stub
				showInfo(json);
			}
    	});
    }
    
    public void showInfo(JSONObject profile) throws JSONException {
    	TextView tName = (TextView) getActivity().findViewById(R.id.user_name);
    	tName.setText(profile.getString("email"));
    	TextView tvistas = (TextView) getActivity().findViewById(R.id.user_stats_vistas);
    	TextView tareas = (TextView) getActivity().findViewById(R.id.user_stats_areas);

    	String vistas_completed = Integer.toString(profile.getInt("total_visits"));
    	String total_vistas = Integer.toString(profile.getInt("total_vistas"));
    	String total_areas = Integer.toString(profile.getInt("total_areas"));
    	String total_areas_completed = Integer.toString(profile.getInt("total_areas_completed"));
    	tvistas.setText(vistas_completed);
    	tareas.setText(total_areas_completed);
    }

}

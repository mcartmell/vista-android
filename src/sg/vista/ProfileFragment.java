package sg.vista;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.RequestParams;

import sg.vista.Vista.VistaResponse;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
	public ProfileFragment() {
	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        // rootView.setTag(getArguments().getInt("section_number"));
        return rootView;
    }
    
    public void refresh() {
    	RequestParams rp = new RequestParams();
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
    	TextView tv = (TextView) getActivity().findViewById(R.id.user_stats_text);
    	String vistas_completed = Integer.toString(profile.getInt("total_visits"));
    	String total_vistas = Integer.toString(profile.getInt("total_vistas"));
    	String total_areas = Integer.toString(profile.getInt("total_areas"));
    	String total_areas_completed = Integer.toString(profile.getInt("total_areas_completed"));

    	tv.setText(Html.fromHtml("Vistas completed: " + vistas_completed + " / " + total_vistas + "<br>"
    			+ "Completed areas: " + total_areas_completed + " / " + total_areas));
    }
}

package sg.vista;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.vista.Vista.VistaResponse;

import com.loopj.android.http.RequestParams;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class ExploreFragment extends Fragment {
	public ExploreFragment() {
	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        return rootView;
    }
    
    public void refresh() {
    	RequestParams rp = new RequestParams();
    	Vista.get(getActivity().getApplicationContext(), "/user/area_stats", rp, new VistaResponse() {
			@Override
			public void onResponse(JSONObject json) throws JSONException {
				populateList(json);
			}
    	});
    }
    
    public void populateList(JSONObject j) throws JSONException {
        ArrayList<Area> areas = new ArrayList<Area>();
		JSONArray areas_j = j.getJSONArray("areas");
        for (int i = 0; i < areas_j.length(); i++) {
            JSONObject jo = areas_j.getJSONObject(i);
            Area a = Area.fromJSON(jo);
            areas.add(a);
        }
        ListView lv = (ListView) getActivity().findViewById(R.id.list_areas);
        final AreaAdapter adapter = new AreaAdapter(areas, getActivity().getApplicationContext());
        if (lv != null) {
        lv.setAdapter(adapter);
        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Area area = (Area) adapter.getItem(position);
	            Intent i = new Intent(getActivity().getApplicationContext(), AreaActivity.class);
	            Bundle b = new Bundle();
	            b.putString("area_name", area.name);
	            i.putExtras(b);
	            startActivity(i);
			}
		});
        }
    }

}

package sg.vista;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ShowAreaFragment extends Fragment {
	JSONObject mVistas;
	View mRootView;

	public ShowAreaFragment() {
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_show_area, container, false);
		return mRootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle b = getActivity().getIntent().getExtras();
		if (b != null && b.containsKey("area_name")) {
			try {
				getVistas(b.getString("area_name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			((TextView) mRootView.findViewById(R.id.current_area)).setText(b.getString("area_name"));
		}
	}
	public void getVistas(String area_name) throws JSONException {
		RequestParams rp = new RequestParams();
		rp.put("area_name", area_name);
		Vista.get(getActivity().getApplicationContext(), "/geo/vistas", rp, new Vista.VistaResponse() {
			Context ctx = getActivity().getApplicationContext();	
			@Override
			public void onResponse(JSONObject json) throws JSONException {
				ArrayList<VistaItem> vistas = new ArrayList<VistaItem>();
				JSONArray vistas_j = json.getJSONArray("vistas");
				mVistas = json;
				for (int i = 0; i < vistas_j.length(); i++) {
					JSONObject jo = vistas_j.getJSONObject(i);
					VistaItem vista = VistaItem.fromJSON(jo);
					vistas.add(vista);
				}
				ListView lv = (ListView) mRootView.findViewById(R.id.area_vistas_list);
				final VistaAdapter adapter = new VistaAdapter(vistas, ctx);
				if (lv != null) {
					lv.setAdapter(adapter);
					lv.setClickable(true);
					lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {
							VistaItem vi = (VistaItem) adapter.getItem(position);
							Intent i = new Intent(getActivity().getApplicationContext(), VistaItemActivity.class);
							Bundle b = new Bundle();
							b.putString("vista_id", vi.vista_id);
							i.putExtras(b);
							startActivity(i);
						}
					});
				}

				// Set stats
				setStats();
				
				// Set static map
				String mapURL = json.getString("static_map");
				DownloadImageTask.dl(mapURL, (ImageView) mRootView.findViewById(R.id.area_static_map));
			}
		});
	}

	public void setStats() throws JSONException {
		int visited = mVistas.getInt("visited");
		int total = mVistas.getInt("total");
		((TextView) mRootView.findViewById(R.id.area_short_stats)).setText(Integer.toString(visited) + "/" + Integer.toString(total));
	}

}

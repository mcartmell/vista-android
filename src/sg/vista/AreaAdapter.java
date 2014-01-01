package sg.vista;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AreaAdapter extends BaseAdapter {
    private ArrayList<Area> areas;
    Context _c;
    
    AreaAdapter (ArrayList<Area> data, Context c) {
        areas = data;
        _c = c;
    }
	@Override
	public int getCount() {
		return areas.size();
	}
	@Override
	public Object getItem(int i) {
		return areas.get(i);
	}
	@Override
	public long getItemId(int i) {
		return i;
	}
	@Override
	public View getView(int i, View v, ViewGroup parent) {
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_area_item, null);
        }
        assert v != null;
        TextView vname = (TextView) v.findViewById(R.id.area_name);
        TextView vdesc = (TextView) v.findViewById(R.id.area_vistas_desc);
        Area a = areas.get(i);
        String vistas = Integer.toString(a.num_vistas);
        String visits = Integer.toString(a.num_visits);
        vname.setText(a.name);
        vdesc.setText(visits + " of " + vistas + " visited");
        return v;
	}
}

package sg.vista;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VistaAdapter extends BaseAdapter {
    private ArrayList<VistaItem> vistas;
    Context _c;

    VistaAdapter (ArrayList<VistaItem> data, Context c) {
        vistas = data;
        _c = c;
    }
	@Override
	public int getCount() {
		return vistas.size();
	}

	@Override
	public Object getItem(int i) {
		return vistas.get(i);
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
            v = vi.inflate(R.layout.list_vista_item, null);
        }
        assert v != null;
        TextView vname = (TextView) v.findViewById(R.id.vista_name);
        TextView vdesc = (TextView)v.findViewById(R.id.vista_description);
        TextView vdist = (TextView) v.findViewById(R.id.vista_dis);

        VistaItem vista = vistas.get(i);
        vname.setText(vista.name);
        vdesc.setText(vista.description);
        vdist.setText(vista.distance + " km");
        return v;
	}

}

package sg.vista;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class Area {
	String name;
    int num_vistas;
	int num_visits;

	public Area(String name, int vistas, int visits) {
		this.name = name;
		this.num_vistas = vistas;
		this.num_visits = visits;
	}
	
	public static Area fromJSON(JSONObject j) throws JSONException {
		String name = j.getString("area_name");
		int vistas = j.getInt("total");
		int visits = j.getInt("visited");
		return new Area(name, vistas, visits);
	}
}

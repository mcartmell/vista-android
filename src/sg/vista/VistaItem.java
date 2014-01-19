package sg.vista;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class VistaItem {
		String name, vista_id, description, directions, distance;
		boolean visited = false;
		double lat;
		double lon;
		
		public VistaItem(String vista_id, String name, String description, String directions, boolean visited, String distance, double lat, double lon) {
			this.name = name;
			this.description = description;
			this.directions = directions;
			this.vista_id = vista_id;
			this.visited = visited;
			this.distance = distance;
			this.lat = lat;
			this.lon = lon;
		}
		
		public static VistaItem fromJSON(JSONObject jo) throws JSONException {
            String name = jo.getString("name");
            String desc = jo.getString("description");
            String vista_id = jo.getJSONObject("_id").getString("$oid");
            String directions = jo.getString("directions");
            boolean visited = false;
            double lat = 0, lon = 0;
            String distance = "";
            if (jo.has("visited")) {
            	visited = jo.getBoolean("visited");
            }
            if (jo.has("dis")) {
            	distance = jo.getString("dis");
            }
            if (jo.has("geometry")) {
            	lat = jo.getJSONObject("geometry").getJSONArray("coordinates").getDouble(1);
            	lon = jo.getJSONObject("geometry").getJSONArray("coordinates").getDouble(0);
            }
            VistaItem vista = new VistaItem(vista_id, name, desc, directions, visited, distance, lat, lon);
			return vista;
		}
}

package sg.vista;

import org.json.JSONException;
import org.json.JSONObject;

public class VistaItem {
		String name, vista_id, description, directions;
		boolean visited = false;
		
		public VistaItem(String vista_id, String name, String description, String directions, boolean visited) {
			this.name = name;
			this.description = description;
			this.directions = directions;
			this.vista_id = vista_id;
			this.visited = visited;
		}
		
		public static VistaItem fromJSON(JSONObject jo) throws JSONException {
            String name = jo.getString("name");
            String desc = jo.getString("description");
            String vista_id = jo.getJSONObject("_id").getString("$oid");
            String directions = jo.getString("directions");
            boolean visited = false;
            if (jo.has("visited")) {
            	visited = jo.getBoolean("visited");
            }
            VistaItem vista = new VistaItem(vista_id, name, desc, directions, visited);
			return vista;
		}
}

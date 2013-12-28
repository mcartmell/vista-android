package sg.vista;

import org.json.JSONException;
import org.json.JSONObject;

public class VistaItem {
		String name, vista_id, description, directions;
		
		public VistaItem(String vista_id, String name, String description, String directions) {
			this.name = name;
			this.description = description;
			this.directions = directions;
			this.vista_id = vista_id;
		}
		
		public static VistaItem fromJSON(JSONObject jo) throws JSONException {
            String name = jo.getString("name");
            String desc = jo.getString("description");
            String vista_id = jo.getJSONObject("_id").getString("$oid");
            VistaItem vista = new VistaItem(vista_id, name, desc,"");
			return vista;
		}
}

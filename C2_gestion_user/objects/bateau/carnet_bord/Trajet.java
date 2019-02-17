package C2_gestion_user.objects.bateau.carnet_bord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class d'objet Trajet
 * - représente le trajet d'un bateau
 * - doit contenir 2 ports(départ, arrivée)
 */

class Trajet {

    public static void main(String args[]) {
        /*Trajet t = new Trajet("port1", 0.0, 0.0);
        try {
            System.out.println(t.toJson().toString(1));
        }
        catch(JSONException je) {
            System.out.println("error json "+je);
        }*/
    }

    private ArrayList<String> list_port;
    private ArrayList<JSONObject> list_coord;

    public Trajet(ArrayList<String> list_port, ArrayList<JSONObject> list_coord) {
        this.list_port = list_port;
        this.list_coord = list_coord;
    }
    public Trajet(String port_pasbouger_encore, Double longitude, Double latitude) {
        this.list_port = new ArrayList<>(1);
        this.list_coord = new ArrayList<>(1);
        JSONObject jc = new JSONObject();
        try {
            jc.put(longitude.toString(), latitude.toString());
        }
        catch(JSONException je) {
            System.out.println("error json "+je);
        }
        for(int i =0;i<2;i++) {
            this.list_coord.add(jc);
            this.list_port.add(port_pasbouger_encore);
        }
    }

    public ArrayList<String> getList_port() {
        return list_port;
    }
    public void setList_port(ArrayList<String> list_port) {
        this.list_port = list_port;
    }
    public ArrayList<JSONObject> getList_coord() {
        return list_coord;
    }
    public void setList_coord(ArrayList<JSONObject> list_coord) {
        this.list_coord = list_coord;
    }

    @Override
    public String toString() {
        return "Trajet{" +
                "list_port=" + list_port +
                '}';
    }

    public JSONObject toJson() {
        JSONArray ja_port = new JSONArray();
        JSONArray ja_coord = new JSONArray();
        JSONObject jo = new JSONObject();
        for(int i =0;i<2;i++) {
            ja_port.put(this.list_port.get(i));
            ja_coord.put(this.list_coord.get(i));
        }
        try {
            jo.put("list_port", ja_port);
            jo.put("list_coord", ja_coord);
        }
        catch(JSONException je) {
            System.out.println("error json "+je);
        }

        return jo;
    }
}
package C2_gestion_user.objects.bateau.carnet_bord;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


/**
 * Caractérise un objet Carnet de Bord
 */

public class Carnet_Bord {

    public static void main(String args[]) {
        /*Carnet_Bord c = new Carnet_Bord("port1");
        try {
            System.out.println(c.toJson().toString(1));
        }
        catch(JSONException je) {
            System.out.println("error json "+je);
        }*/
    }

    private ArrayList<Trajet> list_trajet;

    public Carnet_Bord() {
        this.list_trajet = null;
    }
    public Carnet_Bord(ArrayList<Trajet> list_trajet) {
        this.list_trajet = list_trajet;
    }
    public Carnet_Bord(String port_pasBouger_encore) {
        Trajet t = new Trajet(port_pasBouger_encore, 0.0, 0.0);
        this.list_trajet = new ArrayList<>();
        this.list_trajet.add(t);
    }

    public ArrayList<Trajet> getList_trajet() {
        return list_trajet;
    }
    public void setList_trajet(ArrayList<Trajet> list_trajet) {
        this.list_trajet = list_trajet;
    }

    @Override
    public String toString() {
        return "Trajet{" +
                "list_trajet=" + list_trajet +
                '}';
    }
    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
            for(int i=0;i<list_trajet.size();i++) {
                jo.put("trajet"+i, this.list_trajet.get(i).toJson());
            }
        }
        catch (JSONException e) {
            System.out.println("erreur "+e);
        }
        return jo;
    }
}

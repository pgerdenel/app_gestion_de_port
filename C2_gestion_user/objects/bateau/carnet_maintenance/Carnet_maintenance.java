package C2_gestion_user.objects.bateau.carnet_maintenance;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class d'objet Carnet_maintenance
 * - contient un objet entretien
 * - contient un boolean déterminant si l'entretien a été fait
 */

public class Carnet_maintenance {

    public static void main(String[] args) {
    }

    private boolean entretienfait;
    private Entretien entretien;

    public Carnet_maintenance(boolean entretienfait, String modele) {
        this.entretien = new Entretien(modele);
        this.entretienfait = entretienfait;
    }

    public boolean isEntretienfait() {
        return entretienfait;
    }
    public void setEntretienfait(boolean entretienfait) {
        this.entretienfait = entretienfait;
    }
    public Entretien getEntretien() {
        return entretien;
    }
    public void setEntretien(Entretien entretien) {
        this.entretien = entretien;
    }

    @Override
    public String toString() {
        return "Carnet_maintenance{" +
                "entretienfait=" + entretienfait +
                ", entretien=" + entretien +
                '}';
    }
    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("entretienfait", this.entretienfait);
            jo.put("entretien", this.entretien.toJson());
        }
        catch (JSONException e) {
            System.out.println("erreur "+e);
        }
        return jo;
    }
}
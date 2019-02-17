package C2_gestion_user.objects.bateau.carnet_maintenance;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Class d'objet Point_Entretien
 * représente un point d'entretien parmi d'autre au sein d'une catégorie
 */

public class Point_Entretien {

    public static void main(String[] args) {
        /*Point_Entretien p = new Point_Entretien("etancheite_des_gaines", 3);
        System.out.println(p.toJson());*/
    }

    private String nom_point_entretien;
    private int periodicite;
    private String date_derniere_verif;
    private String date_prochaine_verif;

    /**
     * Point_entretien constructor.
     */
    public Point_Entretien(String nom_point_entretien, int periodicite) {
        this.nom_point_entretien = nom_point_entretien;
        this.periodicite = periodicite;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/LLLL/yyyy");
        this.date_derniere_verif = LocalDate.now().format(formatter);
        this.date_prochaine_verif = LocalDate.now().plusDays(periodicite *30).format(formatter);  // 31 parfois ^^
    }
    public Point_Entretien(Point_Entretien p) {
        this.nom_point_entretien = p.getNom_point_entretien();
        this.periodicite = p.getPeriodicite();
        this.date_derniere_verif = p.getDate_derniere_verif();
        this.date_prochaine_verif = p.getDate_derniere_verif();
    }

    public String getNom_point_entretien() {
        return nom_point_entretien;
    }
    public void setNom_point_entretien(String nom_point_entretien) {
        this.nom_point_entretien = nom_point_entretien;
    }
    private int getPeriodicite() {
        return periodicite;
    }
    public void setPeriodicite(int periodicite) {
        this.periodicite = periodicite;
    }
    private String getDate_derniere_verif() {
        return date_derniere_verif;
    }
    public void setDate_derniere_verif(String date_derniere_verif) {
        this.date_derniere_verif = date_derniere_verif;
    }
    public String getDate_prochaine_verif() {
        return date_prochaine_verif;
    }
    public void setDate_prochaine_verif(String date_prochaine_verif) {
        this.date_prochaine_verif = date_prochaine_verif;
    }

    @Override
    public String toString() {
        return "Point_entretien{" +
                "nom_point_entretien='" + nom_point_entretien + '\'' +
                ", periodicite=" + periodicite +
                ", date_derniere_verif='" + date_derniere_verif + '\'' +
                ", date_prochaine_verif='" + date_prochaine_verif + '\'' +
                '}';
    }
    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("periodicite", this.periodicite);
            jo.put("date_derniere_verif", this.date_derniere_verif);
            jo.put("date_prochaine_verif", this.date_prochaine_verif);
        }
        catch (JSONException e) {
            System.out.println("erreur "+e);
        }
        return jo;
    }
}
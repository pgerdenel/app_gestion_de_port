package C2_gestion_user.objects.bateau;

import C2_gestion_user.objects.bateau.carnet_bord.Carnet_Bord;
import C2_gestion_user.objects.bateau.carnet_maintenance.Carnet_maintenance;
import org.json.JSONException;
import org.json.JSONObject;

public class Bateau {

    public static void main(String[] args) {
        /*Carnet_maintenance cm = new Carnet_maintenance(false, "voilier");
        Carnet_Bord cb = new Carnet_Bord();
        Bateau b = new Bateau("lemeilleur", "voilier", Integer.toString(E_Longueur.Longueur_6_699.getLongueur()), "lemaitre", cm, cb);
        //Bateau b = new Bateau("lemeilleur", "voilier", Integer.toString(-6), "lemaitre", cm, cb);
        System.out.println(b.toJson());*/
    }

    private String nom;
    private String modele;
    private String longueur;
    private String proprio;
    private Carnet_maintenance cm;
    private Carnet_Bord cb;

    public Bateau(String nom, String modele, String longueur, String proprio, Carnet_maintenance cm, Carnet_Bord cb) {
        this.nom = nom;
        this.modele = modele;
        this.longueur = longueur;
        this.proprio = proprio;
        this.cm = cm;
        this.cb = cb;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getModele() {
        return modele;
    }
    public void setModele(String modele) {
        this.modele = modele;
    }
    public String getLongueur() {
        return longueur;
    }
    public void setLongueur(String longueur) {
        this.longueur = longueur;
    }
    public String getProprio() {
        return proprio;
    }
    public void setProprio(String proprio) {
        this.proprio = proprio;
    }
    public Carnet_maintenance getCm() {
        return cm;
    }
    public void setCm(Carnet_maintenance cm) {
        this.cm = cm;
    }
    public Carnet_Bord getCb() {
        return cb;
    }
    public void setCb(Carnet_Bord cb) {
        this.cb = cb;
    }

    @Override
    public String toString() {
        return "Bateau{" +
                "nom='" + nom + '\'' +
                ", modele='" + modele + '\'' +
                ", longueur='" + longueur + '\'' +
                ", proprio='" + proprio + '\'' +
                ", cm=" + cm +
                ", cb=" + cb +
                '}';
    }
    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("nom", this.nom);
            jo.put("modele", this.modele);
            jo.put("longueur", this.longueur);
            jo.put("proprio", this.proprio);
            jo.put("carnetm", this.cm.toJson());
            jo.put("carnetb", this.cb.toJson());
        }
        catch (JSONException e) {
            System.out.println("erreur "+e);
        }
        return jo;
    }
}

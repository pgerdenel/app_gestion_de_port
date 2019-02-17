package C3_gestion_de_port.objects.port;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Place identifiée par :
 * - une longueur
 * - un prix
 */

public class Place {

    public static void main(String[] args) {
    }

    // Attributes
    private int longueur;
    private int prix;
    private int num_place;
    private boolean isEmpty;
    private boolean option_gardiennage;
    private String nom_bateau; // nom_du_fichier.json // plusieurs dossier de longueur qui contiennent tous les bateaux(nom_bateau.json) garés correspondant à cette longueur

    // Constructors
    public Place() {

    }
    public Place(int longueur, int prix, int num_place) {
        this.longueur = longueur;
        this.prix = prix;
        this.num_place = num_place;
        this.isEmpty = true;
        this.option_gardiennage = false;
        this.nom_bateau = null;
        //this.emplacement_bateau = emplacement_bateau;

    }
    public Place(int longueur, int prix, int num_place, boolean isEmpty, boolean option_gardiennage, String nom_bateau/*JSONObject emplacement_bateau*/) {
        this.longueur = longueur;
        this.prix = prix;
        this.num_place = num_place;
        this.isEmpty = isEmpty;
        this.option_gardiennage = option_gardiennage;
        this.nom_bateau = nom_bateau;
        //this.emplacement_bateau = emplacement_bateau;

    }
    public Place(JSONObject place_json) {
        System.out.println("place_json parameter "+place_json);
        try {
            // On récupère le bateau json
            this.longueur = place_json.getInt("longueur");
            this.prix = place_json.getInt("prix");
            this.num_place = place_json.getInt("num_place");
            this.isEmpty = place_json.getBoolean("isEmpty");
            this.option_gardiennage = place_json.getBoolean("option_gardiennage");
            this.nom_bateau = place_json.getString("nom_bateau");
        }
        catch(JSONException jo) {
            System.err.println("Erreur lors de la construction de l'objet Place à partir d'un JSONObject place_json");
        }
    }
    public Place(Place place) {
            this.longueur = place.getLongueur();
            this.prix = place.getPrix();
            this.num_place = place.getNum_place();
            this.isEmpty = place.isEmpty;
            this.option_gardiennage = place.isOption_gardiennage();
            this.nom_bateau = place.getNom_bateau();
    }

    // Getters & Setters
    public int getLongueur() {
        return longueur;
    }
    public void setLongueur(int longueur) {
        this.longueur = longueur;
    }
    public int getPrix() {
        return prix;
    }
    public void setPrix(int prix) {
        this.prix = prix;
    }
    public boolean isEmpty() {
        return isEmpty;
    }
    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
    public boolean isOption_gardiennage() {
        return option_gardiennage;
    }
    public void setOption_gardiennage(boolean option_gardiennage) {
        this.option_gardiennage = option_gardiennage;
    }
    public String getNom_bateau() {
        return nom_bateau;
    }
    public void setNom_bateau(String nom_bateau) {
        this.nom_bateau = nom_bateau;
    }
    public void setNum_place(int num_place) {
        this.num_place = num_place;
    }
    public int getNum_place() {
        return num_place;
    }

    // Others Methods
    @Override
    public String toString() {
        return "\n> Place Object = " +
                " num place = "+ num_place+
                ", longueur = " + longueur +
                ", prix = " + prix + " euros" +
                ", isEmpty = " + isEmpty +
                ", option_gardiennage = " + option_gardiennage +
                ", nom_bateau = " + nom_bateau;
                /*"bateau = "+emplacement_bateau;*/
    }
    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
            /*jo.put("longueur", longueur);
            jo.put("num_place", num_place);*/
            jo.put("prix", prix);
            jo.put("isEmpty", isEmpty);
            jo.put("option_gardiennage", option_gardiennage);
            jo.put("nom_bateau", nom_bateau);
        }
        catch (JSONException e) {
            System.out.println("erreur "+e);
        }
        return jo;
    }
    public JSONObject toJsonMini() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("prix", prix);
            jo.put("isEmpty", isEmpty);
            jo.put("option_gardiennage", option_gardiennage);
            jo.put("nom_bateau", nom_bateau);
        }
        catch (JSONException e) {
            System.out.println("erreur "+e);
        }
        return jo;
    }
}

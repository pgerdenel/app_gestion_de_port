package C2_gestion_user.objects.bateau.carnet_maintenance;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class d'objet Entretien
 * contient une map de toutes les catégories d'entretien
 */

public class Entretien {

    public static void main(String[] args) {
        /*Entretien e2 = new Entretien("voilier");
        System.out.println("voilier "+e2.toString()+"\n\n");
        Entretien e3 = new Entretien("troismat");
        System.out.println("troismat "+e3.toString()+"\n\n");
        Entretien e4 = new Entretien("moteur");
        System.out.println("moteur "+e4.toString()+"\n\n");
        Entretien e5 = new Entretien("sportif");
        System.out.println("sportif "+e5.toString()+"\n\n");
        Entretien e6 = new Entretien("zodiac");
        System.out.println("zodiac "+e6.toString()+"\n\n");
        Entretien e7 = new Entretien("croisiere");
        System.out.println("croisiere "+e7.toString()+"\n\n");*/
    }

    private HashMap<String, Categorie_Entretien> map_cat_entretien; // Map<nom_categorie, Categorie_Entretien>
    private String modele;
    
    public Entretien(String modele) {
        
        this.modele = modele;
        HashMap<String, Categorie_Entretien> map = new HashMap<>();
        try {
            if (CEE_Model_to_Nom.VOILIER.equals(modele)) {
                System.out.println("VOILIER");
                CEE_Model_to_Categorie cee_model_to_categorie = new CEE_Model_to_Categorie();
                Iterator it_1 = cee_model_to_categorie.getVOILIER().entrySet().iterator();
                while (it_1.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) it_1.next();
                    map.put((String) pair.getKey(), new Categorie_Entretien(((String) pair.getKey()).toUpperCase(), modele));
                    it_1.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
                }
                this.map_cat_entretien = new HashMap<>(map);
            } else if (CEE_Model_to_Nom.TROISMAT.equals(modele)) {
                System.out.println("TROISMAT");
                CEE_Model_to_Categorie cee_model_to_categorie = new CEE_Model_to_Categorie();
                Iterator it_1 = cee_model_to_categorie.getTROISMAT().entrySet().iterator();
                while (it_1.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) it_1.next();
                    map.put((String) pair.getKey(), new Categorie_Entretien(((String) pair.getKey()).toUpperCase(), modele));
                    it_1.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
                }
                this.map_cat_entretien = new HashMap<>(map);
            } else if (CEE_Model_to_Nom.MOTEUR.equals(modele)) {
                System.out.println("MOTEUR");
                CEE_Model_to_Categorie cee_model_to_categorie = new CEE_Model_to_Categorie();
                Iterator it_1 = cee_model_to_categorie.getMOTEUR().entrySet().iterator();
                while (it_1.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) it_1.next();
                    map.put((String) pair.getKey(), new Categorie_Entretien(((String) pair.getKey()).toUpperCase(), modele));
                    it_1.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
                }
                this.map_cat_entretien = new HashMap<>(map);
            } else if (CEE_Model_to_Nom.SPORTIF.equals(modele)) {
                System.out.println("SPORTIF");
                CEE_Model_to_Categorie cee_model_to_categorie = new CEE_Model_to_Categorie();
                Iterator it_1 = cee_model_to_categorie.getSPORTIF().entrySet().iterator();
                while (it_1.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) it_1.next();
                    map.put((String) pair.getKey(), new Categorie_Entretien(((String) pair.getKey()).toUpperCase(), modele));
                    it_1.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
                }
                this.map_cat_entretien = new HashMap<>(map);
            } else if (CEE_Model_to_Nom.ZODIAC.equals(modele)) {
                System.out.println("ZODIAC");
                CEE_Model_to_Categorie cee_model_to_categorie = new CEE_Model_to_Categorie();
                Iterator it_1 = cee_model_to_categorie.getZODIAC().entrySet().iterator();
                while (it_1.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) it_1.next();
                    map.put((String) pair.getKey(), new Categorie_Entretien(((String) pair.getKey()).toUpperCase(), modele));
                    it_1.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
                }
                this.map_cat_entretien = new HashMap<>(map);
            } else if (CEE_Model_to_Nom.CROISIERE.equals(modele)) {
                System.out.println("CROISIERE");
                CEE_Model_to_Categorie cee_model_to_categorie = new CEE_Model_to_Categorie();
                Iterator it_1 = cee_model_to_categorie.getCROISIERE().entrySet().iterator();
                while (it_1.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) it_1.next();
                    map.put((String) pair.getKey(), new Categorie_Entretien(((String) pair.getKey()).toUpperCase(), modele));
                    it_1.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
                }
                this.map_cat_entretien = new HashMap<>(map);
            } else {
                System.err.println("modele incorrecte");
                this.map_cat_entretien = null;
            }
        }
        catch(ConcurrentModificationException cme) {
            System.out.println("Error Constructor Entretien "+ cme);
            /*System.out.println(E_Categorie_to_Point_Entretien.ACASTILLAGE.toString());
            System.out.println(CEE_Model_to_Nom.VOILIER.getValue());*/
            //map.put(E_Categorie_to_Point_Entretien.ACASTILLAGE.toString(), new Categorie_Entretien(E_Categorie_to_Point_Entretien.ACASTILLAGE.toString(), CEE_Model_to_Nom.VOILIER.getValue()));
            /*map.put(E_Categorie_to_Point_Entretien.COQUE.toString(), new Categorie_Entretien(E_Categorie_to_Point_Entretien.COQUE.toString(), CEE_Model_to_Nom.VOILIER.getValue()));
            map.put(E_Categorie_to_Point_Entretien.CIRCUIT.toString(), new Categorie_Entretien(E_Categorie_to_Point_Entretien.CIRCUIT.toString(), CEE_Model_to_Nom.VOILIER.getValue()));*/
            this.map_cat_entretien = new HashMap<>(map);
        }
    }

    public HashMap<String, Categorie_Entretien> getMap_cat_entretien() {
        return map_cat_entretien;
    }
    public void setMap_cat_entretien(HashMap<String, Categorie_Entretien> map_cat_entretien) {
        this.map_cat_entretien = map_cat_entretien;
    }
    public String getModele() {
        return modele;
    }
    public void setModele(String modele) {
        this.modele = modele;
    }

    @Override
    public String toString() {
        return "Entretien{" +
                "carnet_entretien=" + map_cat_entretien +
                ", modele='" + modele + '\'' +
                '}';
    }
    public JSONObject toJson() {
        //JSONObject jo_cat =new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            //jo.put("carnet_entretien", this.carnet_entretien);
            for (Map.Entry<String, Categorie_Entretien> entry : this.map_cat_entretien.entrySet()) {
                jo.put(entry.getKey(), entry.getValue().toJson());
                //jo_cat.put(entry.getKey(), jo);
                //System.out.println("key= "+entry.getKey()+" value= "+entry.getValue());
            }
        }
        catch (JSONException e) {
            System.out.println("erreur "+e);
        }
        //return jo_cat;
        return jo;
    }
}
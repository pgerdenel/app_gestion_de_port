package C2_gestion_user.objects.bateau.carnet_maintenance;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class d'objet Categorie_Entretien
 * contient l'ensemble des points d'entretien de la catégorie
 * peut être associé à un type de bateau particulier
 */

public class Categorie_Entretien {

    public static void main(String[] args) {
        /*Categorie_Entretien ce1 = new Categorie_Entretien(CE_Categorie_to_Point_Entretien.ACASTILLAGE.toString(), "voilier");
        System.out.println("voilier "+ce1.toString()+"\n\n");*/
    }

    private String nom_categorie; // nom de la catégorie d'entretien (moteur, etc ....)
    private HashMap<String, Point_Entretien> map_point_entretien; // map<nom_point_entretien, point_entretien>
    private String modele;  // modele du bateau lié à cette catégorie
    
    public Categorie_Entretien(String nom_categorie, String modele) {
        this.nom_categorie = nom_categorie;
        this.modele = modele;

        HashMap<String, Point_Entretien> map = new HashMap<>();
        //CEE_Model_to_Categorie ceemtc = new CEE_Model_to_Categorie();

        if (CEE_Model_to_Nom.VOILIER.equals(modele)) {

            // parcours des catégories d'entretien(pair.getKey()) pour chaque modele
            CEE_Model_to_Categorie ceemtc = new CEE_Model_to_Categorie();
            Iterator it = ceemtc.getVOILIER().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                //System.out.print("NomDeCategorie: "+pair.getKey()+"\n"); // nom de catégorie

                // parcours des points d'entretien de chaque catégorie(Pair2.getKey())
                Iterator it2 = CEE_Categorie_to_Point_Entretien.valueOf(nom_categorie).entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry pair2 = (Map.Entry)it2.next();
                    //System.out.print("\tNomPointEntretien: "+pair2.getKey()+"\n"); // nom de catégorie
                    //System.out.print("key= "+pair2.getKey()+" value= "+pair2.getValue()+"\t");
                    map.put((String)pair2.getKey(), new Point_Entretien((String)pair2.getKey(), (Integer)pair2.getValue())); // erreur ne peut pas cast E_Categorie_to_Point_Entretien en Point_Entretien
                    it2.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
                }
                //System.out.println("");

                //it.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
            }
            this.map_point_entretien = new HashMap<>(map);

        }
        else if (CEE_Model_to_Nom.TROISMAT.equals(modele)) {
            // parcours des catégories d'entretien(pair.getKey()) pour chaque modele
            CEE_Model_to_Categorie ceemtc = new CEE_Model_to_Categorie();
            // parcours des catégories d'entretien(pair.getKey()) pour chaque modele
            Iterator it = ceemtc.getTROISMAT().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                //System.out.print(pair.getKey()+" ");
                // System.out.println(pair.getValue().getClass()); // E_Categorie_to_Point_Entretien

                Iterator it2 = CEE_Categorie_to_Point_Entretien.valueOf(nom_categorie).entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry pair2 = (Map.Entry)it2.next();
                    //System.out.print("key= "+pair2.getKey()+" value= "+pair2.getValue()+"\t");
                    map.put((String)pair2.getKey(), new Point_Entretien((String)pair2.getKey(), (Integer)pair2.getValue())); // erreur ne peut pas cast E_Categorie_to_Point_Entretien en Point_Entretien
                    it2.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
                }
                //System.out.println("");

                //it.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
            }
            this.map_point_entretien = new HashMap<>(map);

        }
        else if (CEE_Model_to_Nom.MOTEUR.equals(modele)) {
            // parcours des catégories d'entretien(pair.getKey()) pour chaque modele
            CEE_Model_to_Categorie ceemtc = new CEE_Model_to_Categorie();
            Iterator it = ceemtc.getMOTEUR().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                //System.out.print(pair.getKey()+" ");
                // System.out.println(pair.getValue().getClass()); // E_Categorie_to_Point_Entretien
                Iterator it2 = CEE_Categorie_to_Point_Entretien.valueOf(nom_categorie).entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry pair2 = (Map.Entry)it2.next();
                    //System.out.print("key= "+pair2.getKey()+" value= "+pair2.getValue()+"\t");
                    map.put((String)pair2.getKey(), new Point_Entretien((String)pair2.getKey(), (Integer)pair2.getValue())); // erreur ne peut pas cast E_Categorie_to_Point_Entretien en Point_Entretien
                    it2.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
                }
                //System.out.println("");

                //it.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
            }
            this.map_point_entretien = new HashMap<>(map);

        }
        else if (CEE_Model_to_Nom.SPORTIF.equals(modele)) {
            // parcours des catégories d'entretien(pair.getKey()) pour chaque modele
            CEE_Model_to_Categorie ceemtc = new CEE_Model_to_Categorie();
            Iterator it = ceemtc.getSPORTIF().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                //System.out.print(pair.getKey()+" ");
                // System.out.println(pair.getValue().getClass()); // E_Categorie_to_Point_Entretien
                Iterator it2 = CEE_Categorie_to_Point_Entretien.valueOf(nom_categorie).entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry pair2 = (Map.Entry)it2.next();
                    //System.out.print("key= "+pair2.getKey()+" value= "+pair2.getValue()+"\t");
                    map.put((String)pair2.getKey(), new Point_Entretien((String)pair2.getKey(), (Integer)pair2.getValue())); // erreur ne peut pas cast E_Categorie_to_Point_Entretien en Point_Entretien
                    it2.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
                }
                //System.out.println("");

                //it.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
            }
            this.map_point_entretien = new HashMap<>(map);

        }
        else if (CEE_Model_to_Nom.ZODIAC.equals(modele)) {
            // parcours des catégories d'entretien(pair.getKey()) pour chaque modele
            CEE_Model_to_Categorie ceemtc = new CEE_Model_to_Categorie();
            Iterator it = ceemtc.getZODIAC().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                //System.out.print(pair.getKey()+" ");
                // System.out.println(pair.getValue().getClass()); // E_Categorie_to_Point_Entretien
                Iterator it2 = CEE_Categorie_to_Point_Entretien.valueOf(nom_categorie).entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry pair2 = (Map.Entry)it2.next();
                    //System.out.print("key= "+pair2.getKey()+" value= "+pair2.getValue()+"\t");
                    map.put((String)pair2.getKey(), new Point_Entretien((String)pair2.getKey(), (Integer)pair2.getValue())); // erreur ne peut pas cast E_Categorie_to_Point_Entretien en Point_Entretien
                    it2.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
                }
                //System.out.println("");

                //it.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
            }
            this.map_point_entretien = new HashMap<>(map);

        }
        else if (CEE_Model_to_Nom.CROISIERE.equals(modele)) {
            // parcours des catégories d'entretien(pair.getKey()) pour chaque modele
            CEE_Model_to_Categorie ceemtc = new CEE_Model_to_Categorie();
            Iterator it = ceemtc.getCROISIERE().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                //System.out.print(pair.getKey()+" ");
                // System.out.println(pair.getValue().getClass()); // E_Categorie_to_Point_Entretien
                Iterator it2 = CEE_Categorie_to_Point_Entretien.valueOf(nom_categorie).entrySet().iterator(); // Iterator it2 = E_Categorie_to_Point_Entretien.valueOf(pair.getKey().toString().toUpperCase()).getMapCatToPoint().entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry pair2 = (Map.Entry)it2.next();
                    //System.out.print("key= "+pair2.getKey()+" value= "+pair2.getValue()+"\t");
                    map.put((String)pair2.getKey(), new Point_Entretien((String)pair2.getKey(), (Integer)pair2.getValue())); // erreur ne peut pas cast E_Categorie_to_Point_Entretien en Point_Entretien
                    it2.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
                }
                //System.out.println("");

                //it.remove(); // evite une modification concurrente (ex: plusieurs threads accèdent)
            }
            this.map_point_entretien = new HashMap<>(map);

        }
        else {
            System.err.println("modele incorrecte");
            this.map_point_entretien = null;
        }
    }

    public String getNom_categorie() {
        return nom_categorie;
    }
    public void setNom_categorie(String nom_categorie) {
        this.nom_categorie = nom_categorie;
    }
    public HashMap<String, Point_Entretien> getMap_point_entretien() {
        return map_point_entretien;
    }
    public void setMap_point_entretien(HashMap<String, Point_Entretien> map_point_entretien) {
        this.map_point_entretien = map_point_entretien;
    }
    public String getModele() {
        return modele;
    }
    public void setModele(String modele) {
        this.modele = modele;
    }
    public void addPointToCat(Point_Entretien point_entretien_to_add) {
        this.map_point_entretien.replace(point_entretien_to_add.getNom_point_entretien(), point_entretien_to_add);
    }

    @Override
    public String toString() {
        return "Categorie_Entretien{" +
                "nom_categorie='" + nom_categorie + '\'' +
                ", map_point_entretien=" + map_point_entretien +
                ", modele='" + modele + '\'' +
                '}';
    }
    public JSONObject toJson() {
        //JSONObject jo_cat = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            for (Map.Entry<String, Point_Entretien> entry : this.map_point_entretien.entrySet()) {
                jo.put(entry.getKey(), entry.getValue().toJson());
                //jo_cat.put(this.nom_categorie, jo);
                /*System.out.println("key= "+entry.getKey().toString());
                System.out.println("value= "+entry.getValue().toString());*/
            }
        }
        catch (JSONException e) {
            System.out.println("erreur "+e);
        }
        //return jo_cat;
        return jo;
    }
}
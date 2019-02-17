package C2_gestion_user.objects.bateau.carnet_maintenance;

import java.lang.reflect.Field;
import java.util.HashMap;

class CEE_Categorie_to_Point_Entretien {

    public static void main(String[] args) {
        //System.out.println(CEE_Categorie_to_Point_Entretien.valueOf("ACASTILLAGE"));
    }

    // Attributes
    private HashMap<String,Integer> ACASTILLAGE;
    private HashMap<String,Integer> COQUE;
    private HashMap<String,Integer> CIRCUIT;
    private HashMap<String,Integer> ELECTRICITE;
    private HashMap<String,Integer> ELECTRONIC;
    private HashMap<String,Integer> MOTEUR;
    private HashMap<String,Integer> PROPULSION;
    private HashMap<String,Integer> SUPERVITESSE;
    private HashMap<String,Integer> RESTAURANT;
    private HashMap<String,Integer> GREEMENT;

    // Internal Classes
    private final class Categorie_to_Point_Entretien {

        private HashMap<String,Integer> ACASTILLAGE;
        private HashMap<String,Integer> COQUE;
        private HashMap<String,Integer> CIRCUIT;
        private HashMap<String,Integer> ELECTRICITE;
        private HashMap<String,Integer> ELECTRONIC;
        private HashMap<String,Integer> MOTEUR;
        private HashMap<String,Integer> PROPULSION;
        private HashMap<String,Integer> SUPERVITESSE;
        private HashMap<String,Integer> RESTAURANT;
        private HashMap<String,Integer> GREEMENT;

        private Categorie_to_Point_Entretien(){
            ACASTILLAGE  = createMapa();
            COQUE = createMapc();
            CIRCUIT  = createMapci();
            ELECTRICITE  = createMape();
            ELECTRONIC  = createMapel();
            MOTEUR  = createMapmo();
            PROPULSION  = createMapp();
            SUPERVITESSE  = createMaps();
            RESTAURANT  = createMapr();
            GREEMENT  = createMapg();
        }

        private HashMap<String,Integer> createMapa() {
            HashMap<String,Integer> myMap = new HashMap<>();
            myMap.put("verif_boulons_haubans", 2);
            myMap.put("torsion_des_gaines", 3);
            return myMap;
        }
        private HashMap<String,Integer> createMapc() {
            HashMap<String,Integer> myMap = new HashMap<>();
            myMap.put("rafistolage_paroi", 6);
            return myMap;
        }
        private HashMap<String,Integer> createMapci() {
            HashMap<String,Integer> myMap = new HashMap<>();
            myMap.put("torsion_des_gaines", 3);
            return myMap;
        }
        private HashMap<String,Integer> createMape() {
            HashMap<String,Integer> myMap = new HashMap<>();
            myMap.put("etancheite_des_gaines", 3);
            return myMap;
        }
        private HashMap<String,Integer> createMapel() {
            HashMap<String,Integer> myMap = new HashMap<>();
            myMap.put("charge_batterie", 1);
            myMap.put("ecran", 12);
            return myMap;
        }
        private HashMap<String,Integer> createMapmo() {
            HashMap<String,Integer> myMap = new HashMap<>();
            myMap.put("niveau_huile", 1);
            myMap.put("charge_batterie", 1);
            myMap.put("clean_filtre_carburant", 1);
            return myMap;
        }
        private HashMap<String,Integer> createMapp() {
            HashMap<String,Integer> myMap = new HashMap<>();
            myMap.put("taux_pollution", 2);
            myMap.put("brulure_paroi", 3);
            return myMap;
        }
        private HashMap<String,Integer> createMaps() {
            HashMap<String,Integer> myMap = new HashMap<>();
            myMap.put("niveau_sulfurite", 2);
            myMap.put("niveau_nos", 3);
            return myMap;
        }
        private HashMap<String,Integer> createMapr() {
            HashMap<String,Integer> myMap = new HashMap<>();
            myMap.put("usure_table", 2);
            myMap.put("reserve_nourriture", 1);
            return myMap;
        }
        private HashMap<String,Integer> createMapg() {
            HashMap<String,Integer> myMap = new HashMap<>();
            myMap.put("verif_boulons_haubans", 2);
            return myMap;
        }


    }

    // Constructors
    CEE_Categorie_to_Point_Entretien() {
        Categorie_to_Point_Entretien ca = new Categorie_to_Point_Entretien();
        ACASTILLAGE = ca.ACASTILLAGE;
        COQUE = ca.COQUE;
        CIRCUIT = ca.CIRCUIT;
        ELECTRICITE = ca.ELECTRICITE;
        ELECTRONIC = ca.ELECTRONIC;
        MOTEUR = ca.MOTEUR;
        PROPULSION = ca.PROPULSION;
        SUPERVITESSE = ca.SUPERVITESSE;
        RESTAURANT = ca.RESTAURANT;
        GREEMENT = ca.GREEMENT;
    }

    // Getters & Setters
    HashMap<String, Integer> getACASTILLAGE() {
        return ACASTILLAGE;
    }
    HashMap<String, Integer> getCOQUE() {
        return COQUE;
    }
    HashMap<String, Integer> getCIRCUIT() {
        return CIRCUIT;
    }
    HashMap<String, Integer> getELECTRICITE() {
        return ELECTRICITE;
    }
    HashMap<String, Integer> getELECTRONIC() {
        return ELECTRONIC;
    }
    HashMap<String, Integer> getMOTEUR() {
        return MOTEUR;
    }
    HashMap<String, Integer> getPROPULSION() {
        return PROPULSION;
    }
    HashMap<String, Integer> getSUPERVITESSE() {
        return SUPERVITESSE;
    }
    HashMap<String, Integer> getRESTAURANT() {
        return RESTAURANT;
    }
    HashMap<String, Integer> getGREEMENT() {
        return GREEMENT;
    }

    // Autres Methodes
    /**
     * CEE_Model_to_Nom.valueOf("voilier");
     * permet de récupérer une enum en particulier en lui passant son nom
     * @param fieldname : nom de la variable du champ
     * @return HashMap<String,Integer>
     */
    @SuppressWarnings("unchecked")
    public static HashMap<String,Integer> valueOf(String fieldname) {
        Field[] fields= CEE_Categorie_to_Point_Entretien.class.getDeclaredFields();
        HashMap<String,Integer> value= new HashMap<>();
        try {
            CEE_Categorie_to_Point_Entretien ce = new CEE_Categorie_to_Point_Entretien();
            for (Field f : fields) {
                //System.out.println("est ce que |"+f.getName()+"| est = a "+fieldname);
                if(f.getName().equals(fieldname.toUpperCase())) {
                    //System.out.print("oui");
                        value = (HashMap<String, Integer>) f.get(ce);
                }
            }
        }
        catch(IllegalAccessException iae) {
            System.out.println("iae error "+iae);
        }
        return value;
    }

}

package C2_gestion_user.objects.bateau.carnet_maintenance;

import java.lang.reflect.Field;
import java.util.HashMap;

public class CEE_Model_to_Categorie implements Cloneable {

    public static void main(String[] args) {
        //System.out.println("la valeur du champ VOILIER est "+ CEE_Model_to_Categorie.getValue("voilier"));
        //CEE_Model_to_Categorie ce = new CEE_Model_to_Categorie();
        //System.out.println("la valeur du champ VOILIER est "+ ce.getVOILIER());
        //System.out.println(MyJSONApi.jsonToString(CEE_Model_to_Nom.toJsonArray(), 1));
        //System.out.println(CEE_Model_to_Nom.VOILIER);
    }

    // Attributes
    private HashMap VOILIER;
    private HashMap TROISMAT;
    private HashMap MOTEUR;
    private HashMap SPORTIF;
    private HashMap ZODIAC;
    private HashMap CROISIERE;

    // Internal Classes
    private final class Model_To_Categorie {

        private final HashMap<String, HashMap<String,Integer>> VOILIER  = createMapv();
        private HashMap<String, HashMap<String,Integer>> createMapv() {
            HashMap<String,HashMap<String,Integer>> myMap = new HashMap<>();
            CEE_Categorie_to_Point_Entretien ceectpe = new CEE_Categorie_to_Point_Entretien();
            myMap.put("acastillage", ceectpe.getACASTILLAGE());
            myMap.put("coque", ceectpe.getCOQUE());
            myMap.put("electricite", ceectpe.getELECTRICITE());
            myMap.put("greement", ceectpe.getGREEMENT());
            return myMap;
        }
        private final HashMap<String, HashMap<String,Integer>> TROISMAT = createMapt();
        private HashMap<String, HashMap<String,Integer>> createMapt() {
            HashMap<String,HashMap<String,Integer>> myMap = new HashMap<>();
            CEE_Categorie_to_Point_Entretien ceectpe = new CEE_Categorie_to_Point_Entretien();
            myMap.put("acastillage", ceectpe.getACASTILLAGE());
            myMap.put("coque", ceectpe.getCOQUE());
            myMap.put("electricite", ceectpe.getELECTRICITE());
            myMap.put("electronic", ceectpe.getELECTRONIC());
            return myMap;
        }
        private final HashMap<String, HashMap<String,Integer>> MOTEUR  = createMapcm();
        private HashMap<String, HashMap<String,Integer>> createMapcm() {
            HashMap<String,HashMap<String,Integer>> myMap = new HashMap<>();
            CEE_Categorie_to_Point_Entretien ceectpe = new CEE_Categorie_to_Point_Entretien();
            myMap.put("acastillage", ceectpe.getACASTILLAGE());
            myMap.put("coque", ceectpe.getCOQUE());
            myMap.put("circuit", ceectpe.getCIRCUIT());
            myMap.put("electricite", ceectpe.getELECTRICITE());
            myMap.put("electronic", ceectpe.getELECTRONIC());
            myMap.put("moteur", ceectpe.getMOTEUR());
            myMap.put("propulsion", ceectpe.getPROPULSION());
            return myMap;
        }
        private final HashMap<String, HashMap<String,Integer>> SPORTIF  = createMaps();
        private HashMap<String, HashMap<String,Integer>> createMaps() {
            HashMap<String,HashMap<String,Integer>> myMap = new HashMap<>();
            CEE_Categorie_to_Point_Entretien ceectpe = new CEE_Categorie_to_Point_Entretien();
            myMap.put("acastillage", ceectpe.getACASTILLAGE());
            myMap.put("coque", ceectpe.getCOQUE());
            myMap.put("circuit", ceectpe.getCIRCUIT());
            myMap.put("electricite", ceectpe.getELECTRICITE());
            myMap.put("electronic", ceectpe.getELECTRONIC());
            myMap.put("moteur", ceectpe.getMOTEUR());
            myMap.put("supervitesse", ceectpe.getSUPERVITESSE());
            return myMap;
        }
        private final HashMap<String, HashMap<String,Integer>> ZODIAC  = createMapez();
        private HashMap<String, HashMap<String,Integer>> createMapez() {
            HashMap<String,HashMap<String,Integer>> myMap = new HashMap<>();
            CEE_Categorie_to_Point_Entretien ceectpe = new CEE_Categorie_to_Point_Entretien();
            myMap.put("acastillage", ceectpe.getACASTILLAGE());
            myMap.put("coque", ceectpe.getCOQUE());
            myMap.put("electronic", ceectpe.getELECTRONIC());
            myMap.put("moteur", ceectpe.getMOTEUR());
            return myMap;
        }
        private final HashMap<String, HashMap<String,Integer>> CROISIERE  = createMapc();
        private HashMap<String, HashMap<String,Integer>> createMapc() {
            HashMap<String,HashMap<String,Integer>> myMap = new HashMap<>();
            CEE_Categorie_to_Point_Entretien ceectpe = new CEE_Categorie_to_Point_Entretien();
            myMap.put("acastillage", ceectpe.getACASTILLAGE());
            myMap.put("coque", ceectpe.getCOQUE());
            myMap.put("circuit", ceectpe.getCIRCUIT());
            myMap.put("electricite", ceectpe.getELECTRICITE());
            myMap.put("electronic", ceectpe.getELECTRONIC());
            myMap.put("moteur", ceectpe.getMOTEUR());
            myMap.put("restaurant", ceectpe.getRESTAURANT());
            return myMap;
        }

        private Model_To_Categorie(){
        }
    }

    // Constructors
    public CEE_Model_to_Categorie() {

        Model_To_Categorie mtc = new Model_To_Categorie();
        VOILIER = mtc.VOILIER;
        TROISMAT = mtc.TROISMAT;
        MOTEUR = mtc.MOTEUR;
        SPORTIF = mtc.SPORTIF;
        ZODIAC = mtc.ZODIAC;
        CROISIERE = mtc.CROISIERE;
    }

    // Getters & Setters
    public HashMap getVOILIER() {
        return VOILIER;
    }
    public void setVOILIER(HashMap VOILIER) {
        this.VOILIER = VOILIER;
    }
    public HashMap getTROISMAT() {
        return TROISMAT;
    }
    public void setTROISMAT(HashMap TROISMAT) {
        this.TROISMAT = TROISMAT;
    }
    public HashMap getMOTEUR() {
        return MOTEUR;
    }
    public void setMOTEUR(HashMap MOTEUR) {
        this.MOTEUR = MOTEUR;
    }
    public HashMap getSPORTIF() {
        return SPORTIF;
    }
    public void setSPORTIF(HashMap SPORTIF) {
        this.SPORTIF = SPORTIF;
    }
    public HashMap getZODIAC() {
        return ZODIAC;
    }
    public void setZODIAC(HashMap ZODIAC) {
        this.ZODIAC = ZODIAC;
    }
    public HashMap getCROISIERE() {
        return CROISIERE;
    }
    public void setCROISIERE(HashMap CROISIERE) {
        this.CROISIERE = CROISIERE;
    }

    // Autres Methodes
    /**
     * CEE_Model_to_Nom.valueOf("voilier");
     * permet de récupérer une enum en particulier en lui passant son nom
     * @param fieldname : nom de la variable du champ
     * @return HashMap<String, HashMap<String,Integer>>
     */
    @SuppressWarnings("unchecked")
    public static HashMap<String, HashMap<String,Integer>> getValue(String fieldname) {
        Field[] fields= CEE_Model_to_Categorie.class.getDeclaredFields();
        HashMap<String, HashMap<String,Integer>> value = new HashMap<>();
        try {
            CEE_Model_to_Categorie ce = new CEE_Model_to_Categorie();
            for (Field f : fields) {
                //System.out.println("est ce que |"+f.getName()+"| est = a "+fieldname);
                if(f.getName().equals(fieldname.toUpperCase())) {
                    //System.out.print("oui");
                    value = (HashMap<String, HashMap<String,Integer>>)f.get(ce);
                }
            }
        }
        catch(IllegalAccessException iae) {
            System.out.println("iae error "+iae);
        }
        return value;
    }
}

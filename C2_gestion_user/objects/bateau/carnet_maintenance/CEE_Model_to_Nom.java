package C2_gestion_user.objects.bateau.carnet_maintenance;

import org.json.JSONArray;

import java.lang.reflect.Field;

public class CEE_Model_to_Nom {

    public static void main( String [] args ) {
        /*System.out.println("le nom de champ de voilier est "+CEE_Model_to_Nom.getFieldName("voilier"));
        System.out.println("la valeur du champ VOILIER est "+CEE_Model_to_Nom.valueOf("voilier"));*/
        //System.out.println(MyJSONApi.jsonToString(CEE_Model_to_Nom.toJsonArray(), 1));
        //System.out.println(CEE_Model_to_Nom.VOILIER);
    }

    // Attributes
    public final static String VOILIER = "voilier";
    public final static String TROISMAT = "troismat";
    public final static String MOTEUR = "moteur";
    public final static String SPORTIF = "sportif";
    public final static String ZODIAC = "zodiac";
    public final static String CROISIERE = "croisiere";

    // Constructors
    private CEE_Model_to_Nom() {
    }

    // Autres Methodes
    /**
     * CEE_Model_to_Nom.valueOf("voilier");
     * permet de récupérer une enum en particulier en lui passant son nom
     * @param fieldname : nom de la variable de l'attribut
     * @return String
     */
    public static String valueOf(String fieldname) {
        Field[] fields= CEE_Model_to_Nom.class.getDeclaredFields();
        String value="-1";
        try {
            CEE_Model_to_Nom ce = new CEE_Model_to_Nom();
            for (Field f : fields) {
                //System.out.println("est ce que |"+f.getName()+"| est = a "+fieldname);
                if(f.getName().equals(fieldname.toUpperCase())) {
                    //System.out.print("oui");
                    value = f.get(ce).toString();
                }
            }
        }
        catch(IllegalAccessException iae) {
            System.out.println("iae error "+iae);
        }
        return value;
    }
    /**
     * System.out.println(ce.getValue(VOILIER));
     * permet de récupérer une enum en particulier en lui passant son nom
     * @param fieldname : nom de la variable de l'attribut
     * @return String
     */
    public String getValue(String fieldname) {
        Field[] fields= CEE_Model_to_Nom.class.getDeclaredFields();
        String value="-1";
        try {
            CEE_Model_to_Nom ce = new CEE_Model_to_Nom();
            for (Field f : fields) {
                //System.out.println("est ce que |"+f.getName()+"| est = a "+fieldname);
                if(f.getName().equals(fieldname.toUpperCase())) {
                    //System.out.print("oui");
                    value = f.get(ce).toString();
                }
            }
        }
        catch(IllegalAccessException iae) {
            System.out.println("iae error "+iae);
        }
        return value;
    }
    /**
     * Convertit les valeurs de l'enum en un JSONArray
     * @return JSONArray
     */
    public static JSONArray toJsonArray() {
        JSONArray ja = new JSONArray();
        CEE_Model_to_Nom ce = new CEE_Model_to_Nom();
        Field[] fields= CEE_Model_to_Nom.class.getDeclaredFields();
        try {
            for (Field f : fields) {
                ja.put(f.get(ce).toString());
            }
        }
        catch(IllegalAccessException iae) {
            System.out.println("iae error "+iae);
        }
        return ja;
    }
}

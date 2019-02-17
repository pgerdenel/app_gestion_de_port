package C3_gestion_de_port.objects.port;

import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public enum E_Prix implements Serializable {

    P_Longueur_M6(new Pair<>(E_Longueur.Longueur_M6, 510)),
    P_Longueur_6_699(new Pair<>(E_Longueur.Longueur_6_699, 510)),
    P_Longueur_7_799(new Pair<>(E_Longueur.Longueur_7_799, 590)),
    P_Longueur_8_899(new Pair<>(E_Longueur.Longueur_8_899, 590)),
    P_Longueur_9_999(new Pair<>(E_Longueur.Longueur_9_999, 750)),
    P_Longueur_10_1099(new Pair<>(E_Longueur.Longueur_10_1099, 950)),
    P_Longueur_11_1199(new Pair<>(E_Longueur.Longueur_11_1199, 1150)),
    P_Longueur_12_1299(new Pair<>(E_Longueur.Longueur_12_1299, 1300)),
    P_Longueur_13_1399(new Pair<>(E_Longueur.Longueur_13_1399, 1450)),
    P_Longueur_14_1499(new Pair<>(E_Longueur.Longueur_14_1499, 1600)),
    P_Longueur_15_1599(new Pair<>(E_Longueur.Longueur_15_1599, 1855)),
    P_Longueur_16_1699(new Pair<>(E_Longueur.Longueur_16_1699, 1980)),
    P_Longueur_17_1799(new Pair<>(E_Longueur.Longueur_17_1799, 2180)),
    P_Longueur_18_1899(new Pair<>(E_Longueur.Longueur_18_1899, 2380)),
    P_Longueur_19_1999(new Pair<>(E_Longueur.Longueur_19_1999, 2480)),
    P_Longueur_P20(new Pair<>(E_Longueur.Longueur_P20, 2680));

    private final Pair<E_Longueur, Integer> prix;

    E_Prix(Pair<E_Longueur, Integer> p) {
        prix = p;
    }

    // retourne un objet E_Longueur d'un objet E_Prix
    public E_Longueur getE_Longueur() {
        return prix.getKey();
    }
    // retourne un entier longueur d'un objet E_Prix
    public int getLongueur() {
        return prix.getKey().getLongueur();
    }
    // retourne un entier prix correspondant à un objet E_Prix
    public int getE_Prix() {
        return prix.getValue();
    }

    public static void main(String args[]) {
        /*for (E_Prix pair : E_Prix.values()) {
            // System.out.println("key: "+pair); // affiche les noms de variables accessibles à travers l'enum E_Prix
            // System.out.println("value: "+pair.prix); // affiche les valeurs des variables accessibles à travers l'enum E_Prix
            System.out.println("key: "+pair.prix.getKey()+ " value: "+pair.prix.getValue()); // affiche les clés et valeurs de pairs de chaque variables accessibles à travers l'enum E_Prix
        }*/
        //System.out.println("E_PRIX json "+E_Prix.toJson());
    }

    public static JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
            for (E_Prix pair : E_Prix.values()) {
                jo.put(pair.prix.getKey().toString(),pair.prix.getValue());
            }
        }
        catch (JSONException e) {
            System.out.println("erreur "+e);
        }
        return jo;
    }
    public static JSONArray toJsonArray() {
        JSONArray ja = new JSONArray();
        try {
            for (E_Prix pair : E_Prix.values()) {
                JSONObject jo = new JSONObject();
                jo.put("longueur", pair.prix.getKey().toString());
                jo.put("prix", pair.prix.getValue().toString());
                ja.put(jo);
            }
        }
        catch (JSONException e) {
            System.out.println("erreur "+e);
        }
        return ja;
    }


}

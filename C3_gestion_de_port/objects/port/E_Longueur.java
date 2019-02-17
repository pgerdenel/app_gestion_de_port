package C3_gestion_de_port.objects.port;

import org.json.JSONArray;

public enum E_Longueur {

    Longueur_M6(-6),
    Longueur_6_699(6_699),
    Longueur_7_799(7_799),
    Longueur_8_899(8_899),
    Longueur_9_999(9_999),
    Longueur_10_1099(10_1099),
    Longueur_11_1199(11_1199),
    Longueur_12_1299(12_1299),
    Longueur_13_1399(13_1399),
    Longueur_14_1499(14_1499),
    Longueur_15_1599(15_1599),
    Longueur_16_1699(16_1699),
    Longueur_17_1799(17_1799),
    Longueur_18_1899(18_1899),
    Longueur_19_1999(19_1999),
    Longueur_P20(20);

    private final int longueur;

    E_Longueur(int l) {
        longueur = l;
    }

    public int getLongueur() {
        return longueur;
    }
    public static JSONArray toJsonArray() {
        JSONArray ja = new JSONArray();
        for (E_Longueur pair : E_Longueur.values()) {
            ja.put(pair.getLongueur());
        }
        return ja;
    }

    public static void main(String[] args) {
        /*try {
            System.out.println(toJsonArray().toString(1));
        }
        catch(JSONException je) {
            System.out.println("eerr json "+je);
        }*/
    }
}

package B_back_office.objects;

import org.json.JSONArray;

public enum E_Model_to_Nom {

    VOILIER("voilier"),
    TROISMAT("troismat"),
    MOTEUR("moteur"),
    SPORTIF("sportif"),
    ZODIAC("zodiac"),
    CROISIERE("croisiere");

    private final String value;

    E_Model_to_Nom(final String value) {
        this.value = value;
    }

    public static JSONArray toJsonArray() {
        JSONArray ja = new JSONArray();
        for (E_Model_to_Nom pair : E_Model_to_Nom.values()) {
            ja.put(pair.value);
        }
        return ja;
    }

    public static void main(String[] args) {
        System.out.println(toJsonArray());
    }
}
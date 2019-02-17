package C3_gestion_de_port.objects.port;

import org.json.JSONArray;

public enum E_Model_to_Nom {

    DEFAULT("default"),
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

    public final String getValue() {
        return value;
    }
    public static JSONArray toJsonArray() {
        JSONArray ja = new JSONArray();
        for (E_Model_to_Nom pair : E_Model_to_Nom.values()) {
            ja.put(pair.value);
        }
        return ja;
    }

    public static void main(String[] args) {

        /*// Convert enum to set and apply forEach()
        EnumSet.allOf(E_Model_to_Nom.class)
                .forEach(season -> System.out.println(season));
        // Convert enum to set and apply forEach()
        Arrays.asList(E_Model_to_Nom.values()).forEach(season ->
                System.out.println(season));*/

       /* try {
            System.out.println(toJsonArray().toString(1));
        }
        catch(JSONException je) {
            System.out.println("eerr json "+je);
        }*/


    }
}
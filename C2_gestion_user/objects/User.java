package C2_gestion_user.objects;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Class utilisateur permettant de creer un objet avec les données d'un utilisateur
 */
public class User {

    public static void main(String args[]) {
    }

    // Attributes
    private String login;
    private String pass;
    private String type; // si type admin ou propriétaire

    // Constructors
    public User(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }
    public User(JSONObject user_json) {
        try {
            this.login = (String) user_json.get("login");
            this.pass = (String) user_json.get("pass");
        }
        catch(JSONException je) {
            System.out.println("Erreur de construction de l'objet USER avec un objet json "+je);
        }
    }
    public User(String login, String pass, String type) {
        this.login = login;
        this.pass = pass;
        this.type = type;
    }

    // Getters & Setters
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    // Autres méthodes
    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("login", this.login);
            jo.put("pass", this.pass);
            jo.put("type", this.type);
        }
        catch (JSONException je) {
            System.out.println("Error users.toJson() "+je);
        }
        return jo;
    }
    @Override
    public String toString() {
        return "> Object User" +
                "\n\tlogin= " + login +
                "\n\tpass= " + pass +
                "\n\ttype= " + type;
    }
}

package A_auth_certif.objects.cert;

import org.json.JSONObject;

/**
 * Objet destiné à représenter un mess
 */

public class Message_AskCert {

    private String code_message;
    private String public_key;

    public Message_AskCert() {
        code_message ="ask_cert";
        public_key = "01";
    }
    public Message_AskCert(JSONObject ma) {
        try {
            this.code_message = ma.getString("code_message");
            this.public_key = ma.getString("public_key");
        }
        catch(Exception e) {
            System.out.println("erreur "+e);
        }
    }
    public Message_AskCert(String message, String public_key) {
        this.code_message = message;
        this.public_key = public_key;
    }

    public String getCodeMessage() {
        return code_message;
    }
    public void setCodeMessage(String code_message) {
        this.code_message = code_message;
    }
    public String getPublic_key() {
        return public_key;
    }
    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("code_message",this.code_message);
            jo.put("getString",this.public_key);
        }
        catch(Exception e) {
            System.out.println("erreur "+e);
        }
        return jo;
    }
}

package A_auth_certif.objects.cert;

import A_auth_certif.objects.MyAPI;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MyCertificat implements MyAPI {

    public static void main(String[] args){
        /*PublicKey publicKey_serveur = GestionClesRSA.lectureClePublique("src\\serveur\\" + "public_key.bin");
        MyCertificat m = new MyCertificat("Auth_srv_verisian", "email@admin.com", "Client1", "v1", "58:57:59:58:57:59:58:57:59:58:57:59", "RSA", "localhost", "01/01/01", "01/01/01", "O", publicKey_serveur,  MyAPI.getMac());
        //System.out.println("certif json base\n"+m.toJson()+"\n\n");
        MyCertificat m2 = new MyCertificat(m.toJson());
        System.out.println("certif json rebuild\n"+m.toJson()+"\n\n");*/
    }

    // Attributs
    private String autorityName;        // nom de l'authorité qui a validé le certificat
    private String email;               // email du propriétaire
    private String proprio;             // propritaire du certificat
    private String version;             // La version de X.509 à laquelle le certificat correspond ;
    private String serial;              // Le numéro de série du certificat ;
    private String algoChiffrement;     // L'algorithme de chiffrement utilisé pour signer le certificat ;
    private String DN;                  // Le nom (DN, pour Distinguished Name) de l'autorité de certification émettrice ;
    private String dateStart; // La date de début de validité du certificat ;
    private String dateEnd;   // La date de fin de validité du certificat ;
    private String subjectKey;          // L'objet de l'utilisation de la clé publique ;
    private PublicKey publicKey;           // La clé publique du propriétaire du certificat ;
    private String fingerPrint;         // La signature de l'émetteur du certificat (thumbprint).

    // Constructors
    public MyCertificat(){
        DateFormat formatter = new SimpleDateFormat("dd/MM/yy");

        this.autorityName = "AN authority";
        this.email = "email@admin.com";
        this.proprio = "Mr. proprio";
        this.version = "v3";
        this.serial = "58:57:59:58:57:59:58:57:59:58:57:59:";
        this.algoChiffrement = "security"; //DES
        this.DN = "AN authority corp";
        try {
            this.dateStart = formatter.parse("29/01/18").toString();
            this.dateEnd = formatter.parse("29/01/19").toString();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        this.subjectKey = "subjectKey";
        //this.publicKey =
        this.fingerPrint = "fingerPrint";
    }
    public MyCertificat(String autorityName, String email, String proprio, String version, String serial, String algoChiffrement, String DN, String dateStart, String dateEnd, String subjectKey, PublicKey publicKey, String fingerPrint) {
        this.autorityName = autorityName;
        this.email = email;
        this.proprio = proprio;
        this.version = version;
        this.serial = serial;
        this.algoChiffrement = algoChiffrement;
        this.DN = DN;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.subjectKey = subjectKey;
        this.publicKey = publicKey;
        this.fingerPrint = fingerPrint;
    }
    public MyCertificat(JSONObject cert_json) {
        try {
        this.autorityName = cert_json.getString("autorityName");
        this.email= cert_json.getString("email");
        this.proprio = cert_json.getString("proprio");
        this.version = cert_json.getString("version");
        this.serial = cert_json.getString("serial");
        this.algoChiffrement = cert_json.getString("algoChiffrement");
        this.DN = cert_json.getString("DN");
        this.dateStart = cert_json.getString("dateStart");
        this.dateEnd = cert_json.getString("dateEnd");
        this.subjectKey = cert_json.getString("subjectKey");
        //converting string to Bytes
        byte[] byte_pubkey  = java.util.Base64.getDecoder().decode(cert_json.getString("publicKey"));
        //converting it back to public key
        KeyFactory usine = KeyFactory.getInstance("RSA");
        this.publicKey = usine.generatePublic(new X509EncodedKeySpec(byte_pubkey));
        this.fingerPrint = cert_json.getString("fingerPrint");
        }
        catch(Exception e) {
            System.out.println("erreur "+e);
        }
    }


    // Getters & Setters
    public String getAutorityName() {
        return autorityName;
    }
    public void setAutorityName(String autorityName) {
        this.autorityName = autorityName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getProprio() {
        return proprio;
    }
    public void setProprio(String proprio) {
        this.proprio = proprio;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getSerial() {
        return serial;
    }
    public void setSerial(String serial) {
        this.serial = serial;
    }
    public String getAlgoChiffrement() {
        return algoChiffrement;
    }
    public void setAlgoChiffrement(String algoChiffrement) {
        this.algoChiffrement = algoChiffrement;
    }
    public String getDN() {
        return DN;
    }
    public void setDN(String DN) {
        this.DN = DN;
    }
    public String getDateStart() {
        return dateStart;
    }
    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }
    public String getDateEnd() {
        return dateEnd;
    }
    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }
    public String getSubjectKey() {
        return subjectKey;
    }
    public void setSubjectKey(String subjectKey) {
        this.subjectKey = subjectKey;
    }
    public PublicKey getPublicKey() {
        return publicKey;
    }
    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
    public String getFingerPrint() {
        return fingerPrint;
    }
    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }


    // Others Methods

    @Override
    public String toString() {
        return "MyCertificat Object" +
                "autorityName='" + autorityName +
                ", email='" + email +
                ", proprio='" + proprio +
                ", version='" + version +
                ", serial='" + serial +
                ", algoChiffrement='" + algoChiffrement +
                ", DN='" + DN +
                ", dateStart='" + dateStart +
                ", dateEnd='" + dateEnd +
                ", subjectKey='" + subjectKey +
                ", publicKey=" + publicKey +
                ", fingerPrint='" + fingerPrint;
    }

    // Convertit l'objet en un objet JSON
    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
        jo.put("autorityName",this.autorityName);
        jo.put("email",this.email);
        jo.put("proprio",this.proprio);
        jo.put("version",this.version);
        jo.put("serial",this.serial);
        jo.put("algoChiffrement",this.algoChiffrement);
        jo.put("DN",this.DN);
        jo.put("dateStart",this.dateStart);
        jo.put("dateEnd",this.dateEnd);
        jo.put("subjectKey",this.subjectKey);
        //converting public key to byte
        byte[] byte_pubkey = this.publicKey.getEncoded();
        //converting byte to String
        String str_key = java.util.Base64.getEncoder().encodeToString(byte_pubkey);
        //String str_key2 = new String(byte_pubkey, StandardCharsets.UTF_8);
        //String str_key2 = new String(byte_pubkey, "UTF-8");
        jo.put("publicKey", str_key);
        jo.put("fingerPrint",this.fingerPrint);
        }
        catch(Exception e) {
            System.out.println("erreur "+e);
        }
        return jo;
    }
    // Enregistre le certificat en json dans un fichier json
    public void writeJSONCert(String path, String filename) { // 0 serveur, 1 client
        JSONObject cert_json = this.toJson();

        // Création du fichier de sortie
        FileWriter fs = null;
        try {
            fs = new FileWriter(path+filename);
        } catch(IOException e) {
            System.err.println("Erreur lors de l'ouverture du fichier '" + path + "'."+e);
            System.exit(-1);
        }

        // Sauvegarde dans le fichier
        try {
            cert_json.write(fs);
            fs.flush();
        }
        catch(IOException | JSONException e) {
            System.err.println("Erreur lors de l'écriture dans le fichier."+e);
            System.exit(-1);
        }

        // Fermeture du fichier
        try {
            fs.close();
        } catch(IOException e) {
            System.err.println("Erreur lors de la fermeture du fichier."+e);
            System.exit(-1);
        }

        System.out.println(path + "certificat.json generated");
    }
}

package C2_gestion_user.objects;

import C2_gestion_user.GestionUser_Srv;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

/**
 * permet de gérer les utilisateurs et le fichier 'liste utilisateur' qui est associé.
 * le constructeur prend en paramètre un nom de fichier dans lequel sera lu (et sauvegardé) les login/mot de passe
 */

public class GestionnaireUtilisateurs {

    public static void main(String[] args) {

       /* GestionnaireUtilisateurs gest = new GestionnaireUtilisateurs("src\\C2_gestion_user\\resources\\all_users.json");
        System.out.println(MyJSONApi.jsonToString(gest.all_users, 1));*/
    }

    private String nomFichier;
    private JSONObject all_users;

    /**
     * initialise le gestionnaire utilisateur avec les utilisateurs de base
     */
    public void init() {
        charger();
        MyJSONApi.writeToJSONFile(all_users, nomFichier);
    }
    /**
     * Ouverture d'un fichier de configuration.
     * @param nomFichier le nom du fichier de configuration
     */
    public GestionnaireUtilisateurs(String nomFichier) {
        this.nomFichier = nomFichier;
        charger();
    }
    /**
     * Indique si un fichier existe.
     * @param nomFichier le nom du fichier
     * @return 'true' s'il existe
     */
    public static boolean fichierExiste(String nomFichier) {
        File f = new File(nomFichier);

        return f.exists();
    }

    /**
     * Retourne la valeur associée à une clef.
     * @param clef le nom de la clef
     * @return la valeur de la clef
     */
    public String getString(String clef) {
        // System.out.println("this config "+ this.config);
        String str="";
        try {
                str = all_users.get(clef).toString();
        }
        catch(JSONException jes) {
            System.out.println("JSONException in getString() "+jes);
        }

        return str;

    }
    public void setGard(String nom_user, String val) {
        JSONObject tmp = MyJSONApi.castinJSONObject(MyJSONApi.getString(all_users, nom_user));
        MyJSONApi.ajouterValeur(tmp, "pass", MyJSONApi.getString(MyJSONApi.castinJSONObject(MyJSONApi.getString(all_users, nom_user)), "pass"));
        MyJSONApi.ajouterValeur(tmp, "type", MyJSONApi.getString(MyJSONApi.castinJSONObject(MyJSONApi.getString(all_users, nom_user)), "type") );
        MyJSONApi.ajouterValeur(tmp, "gard", val);
        MyJSONApi.ajouterValeur(all_users, nom_user, tmp);
        MyJSONApi.writeToJSONFile(all_users, GestionUser_Srv.getPath_all_users());
    }
    public JSONObject getAll_users() {
        return all_users;
    }

    /**
     * Charge un fichier de configuration en mémoire.
     */
    private void charger() {
        if (fichierExiste(nomFichier)){
            // Ouverture du fichier
            FileInputStream fs = null;
            try {
                fs = new FileInputStream(nomFichier);
            } catch(FileNotFoundException e) {
                System.err.println("Fichier '" + nomFichier + "' introuvable");
                System.exit(-1);
            }

            // Récupération de la chaîne JSON depuis le fichier
            StringBuilder jsonc = new StringBuilder();
            Scanner scanner = new Scanner(fs);
            while(scanner.hasNext())
                jsonc.append(scanner.nextLine());
            scanner.close();
            String json = jsonc.toString().replaceAll("[\t ]", "");

            // Fermeture du fichier
            try {
                fs.close();
            } catch(IOException e) {
                System.err.println("Erreur lors de la fermeture du fichier "+e);
                System.exit(-1);
            }

            try {
                // Création d'un objet JSON
                this.all_users = new JSONObject(json);
                // System.out.println("Contenu JSON : "+json);
            }
            catch(JSONException jes) {
                System.out.println("erreur chargement JSONException "+jes);
            }
        }
        else {
            System.out.println("le fichier n'existe pas");
        }
    }
    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> keys = all_users.keys();
        try {
            while (keys.hasNext()) {
                String key = keys.next();
                if (all_users.get(key) instanceof JSONObject) {
                    sb.append(key);
                    sb.append(": ");
                    sb.append(all_users.getString(key));
                    sb.append("\n");
                }
            }
        }
        catch(JSONException je) {
            System.out.println("toString() error json"+je);
        }

        return "> GestionnaireUtilisateurs :\n" +
                sb.toString();
    }

    // Renvoie un json Object de tous les utilisateurs propriétaire et enregistre cet objet dans une fichier all_users_proprio.json
    @SuppressWarnings("unchecked")
    public JSONArray getAllProprio() {

        JSONArray objet_final = new JSONArray();

        if (fichierExiste(nomFichier)){
            // Ouverture du fichier
            FileInputStream fs = null;
            try {
                fs = new FileInputStream(nomFichier);
            } catch(FileNotFoundException e) {
                System.err.println("Fichier '" + nomFichier + "' introuvable");
                System.exit(-1);
            }

            // Récupération de la chaîne JSON depuis le fichier
            StringBuilder jsonc = new StringBuilder();
            Scanner scanner = new Scanner(fs);
            while(scanner.hasNext())
                jsonc.append(scanner.nextLine());
            scanner.close();
            String json = jsonc.toString().replaceAll("[\t ]", "");

            // Fermeture du fichier
            try {
                fs.close();
            } catch(IOException e) {
                System.err.println("Erreur lors de la fermeture du fichier "+e);
                System.exit(-1);
            }

            try {
                // Création d'un objet JSON
                JSONObject objet = new JSONObject(json);
                this.all_users = objet;
                // On parcours l'objet JSON contenant tous les users
                // si le type est p alors on l'ajoute
                Iterator<String> keys = objet.keys();

                while(keys.hasNext()) {
                    String key = keys.next();
                    if (objet.get(key) instanceof JSONObject) {
                        if(((JSONObject) objet.get(key)).getString("type").equals("p")) {
                            //objet_final.put(key, ((JSONObject) objet.get(key)).getString("pass"));
                            objet_final.put(key);
                        }
                    }
                }
                //System.out.println("objet_final "+objet_final.toString(1));
            }
            catch(JSONException jes) {
                System.out.println("erreur chargement JSONException "+jes);
            }
        }
        else {
            System.out.println("le fichier n'existe pas");
        }
        return objet_final;
    }

}

package A_auth_certif.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.Scanner;

public interface MyJSONApi {

    /**
     * Renvoie un JSONObject du json_string passé en paramètre
     * Pour éviter les catch(JSONException partout)
     */
    static JSONObject castinJSONObject(String jo) {
        JSONObject j =null;
        try {
            j = new JSONObject(jo);
        }
        catch(JSONException je) {
            System.out.println("error json castinJSONObbject "+je);
        }
        return j;
    }
    /**
     * Sauvegarder le JSONObject en paramètre dans le fichier dont le nom
     * correspond à l'attribut 'nomFichier'.
     */
    static void writeToJSONFile(JSONObject jo, String path_filename) {

        // Création du fichier de sortie
        FileWriter fs = null;
        try {
            fs = new FileWriter(path_filename);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du fichier '" + path_filename + "'."+e);
            System.exit(-1);
        }

        // Sauvegarde dans le fichier
        try {
            jo.write(fs);
            System.out.println("\t> file \""+path_filename+"\"");
            fs.flush();
            fs.close();   // Fermeture du fichier
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture dans le fichier."+e);
            System.exit(-1);
        } catch (JSONException ej) {
            System.err.println("Erreur lors de l'écriture du json dans le fichier."+ej);
            System.exit(-1);
        }

        //System.out.println("Le fichier '" + nomFichier + "' a été généré.");
    }
    static JSONObject retrieveJSONObjectFromFile(String path_filename) {
        /*
          Charger le fichier JSON dont le nom correspond à l'attribut
          'nomFichier' dans l'attribut 'config' (un objet JSONObject).
         */
        JSONObject objet = null;
        if (fichierExiste(path_filename)) {
            // Ouverture du fichier
            FileInputStream fs = null;
            try {
                fs = new FileInputStream(path_filename);
            } catch (FileNotFoundException e) {
                System.err.println("Fichier '" + path_filename + "' introuvable");
                System.exit(-1);
            }

            // Récupération de la chaîne JSON depuis le fichier

            Scanner scanner = new Scanner(fs);
            StringBuilder jsonc = new StringBuilder();
            while (scanner.hasNext())
                jsonc.append(scanner.nextLine());
            scanner.close();
            String json = jsonc.toString().replaceAll("[\t ]", "");

            // Fermeture du fichier
            try {
                fs.close();
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture du fichier."+e);
                System.exit(-1);
            }

            try {
                // Création d'un objet JSON
                objet = new JSONObject(json);
                // System.out.println("Contenu JSON : "+json);
                // System.out.println("key adresse "+objet.get("adresse"));
                // System.out.println("key port "+objet.get("port"));
            } catch (JSONException jes) {
                System.out.println("erreur chargement JSONException 0+jes");
            }
        } else {
            System.out.println("le fichier n'existe pas");
        }
        return objet;
    }
    /**
     * Indique si un fichier existe.
     *
     * @param nomFichier le nom du fichier
     * @return 'true' s'il existe
     */
    static boolean fichierExiste(String nomFichier) {
        File f = new File(nomFichier);

        return f.exists();

    }
    /**
     * Retourne la valeur associée à une clef.
     * @param clef le nom de la clef
     * @return la valeur de la clef
     */
    static String getString(JSONObject jo, String clef) {
        /*
          Récupère la donnée dont la clef est spécifiée dans l'objet
          JSON (attribut 'config').
         */
        // System.out.println("this config "+ this.config);
        String str="";
        try {
            str = jo.get(clef).toString();
        }
        catch(JSONException jes) {
            System.out.println("JSONException in getString() "+jes);
        }

        return str;

    }
    /**
     * Retourne la valeur associée à une clef.
     * @param clef le nom de la clef
     * @return la valeur de la clef
     */
    static int getInt(JSONObject jo, String clef) {
        /*
          Récupère la donnée dont la clef est spécifiée dans l'objet
          JSON (attribut 'config').
         */
        int i=-1;
        try {
            i = Integer.parseInt(jo.get(clef).toString());
        }
        catch(JSONException jes) {
            System.out.println("JSONException in getInt() "+jes);
        }

        return i;
    }
    /**
     * Ajoute une valeur dans la configuration.
     * @param clef le nom de la clef
     * @param valeur la valeur de la clef
     */
    static JSONObject ajouterValeur(JSONObject jo, String clef, int valeur) {
        /*
          Ajouter les données dans l'objet JSON (attribut 'config')
         */
        try {
            jo.put(clef, (Integer)valeur);
        }
        catch(JSONException jes) {
            System.out.println("JSONException in ajouterValeur() "+jes);
        }
        return jo;
    }
    /**
     * Ajoute une valeur dans la configuration.
     * @param clef le nom de la clef
     * @param valeur la valeur de la clef
     */
    static JSONObject ajouterValeur(JSONObject jo, String clef, String valeur) {
        /*
          Ajouter les données dans l'objet JSON (attribut 'config')
         */
        try {
            jo.put(clef, valeur);
        }
        catch(JSONException jes) {
            System.out.println("JSONException in ajouterValeur() "+jes);
        }
        return jo;
    }
}

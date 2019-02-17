package A_auth_certif.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.Scanner;

/**
 * Classe permettant de créer/gérer un fichier de configuration.
 * @author Cyril Rabat
 * @date 21/10/2018
 */
public class Config {

    private String nomFichier;      // Nom du fichier de configuration
    private JSONObject config;      // La configuration

    /**
     * Ouverture d'un fichier de configuration.
     * @param nomFichier le nom du fichier de configuration
     */
    public Config(String nomFichier) {
        this.nomFichier = nomFichier;
        charger();
    }

    /**
     * Ouverture/création d'un fichier de configuration.
     * @param nomFichier le nom du fichier de configuration
     * @param creation si 'true', crée un nouveau fichier vide
     */
    public Config(String nomFichier, boolean creation) {

        if(!creation) {
            this.nomFichier = nomFichier;
            charger();
        }
        else {
            this.nomFichier = nomFichier;
            config = new JSONObject();
        }
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
        /*
          Récupère la donnée dont la clef est spécifiée dans l'objet
          JSON (attribut 'config').
         */
        // System.out.println("this config "+ this.config);
        String str="";
        try {
            str = config.get(clef).toString();
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
    public int getInt(String clef) {
        /*
          Récupère la donnée dont la clef est spécifiée dans l'objet
          JSON (attribut 'config').
         */
        int i=-1;
        try {
            i = Integer.parseInt(config.get(clef).toString());
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
    public void ajouterValeur(String clef, int valeur) {
        /*
          Ajouter les données dans l'objet JSON (attribut 'config')
         */
        try {
            config.put(clef, (Integer)valeur);
        }
        catch(JSONException jes) {
            System.out.println("JSONException in ajouterValeur() "+jes);
        }
    }

    /**
     * Ajoute une valeur dans la configuration.
     * @param clef le nom de la clef
     * @param valeur la valeur de la clef
     */
    public void ajouterValeur(String clef, String valeur) {
        /*
          Ajouter les données dans l'objet JSON (attribut 'config')
         */
        try {
            config.put(clef, valeur);
        }
        catch(JSONException jes) {
            System.out.println("JSONException in ajouterValeur() "+jes);
        }
    }
    
    /**
     * Charge un fichier de configuration en mémoire.
     */
    private void charger() {
        // Ouverture du fichier
        
        /*
          Charger le fichier JSON dont le nom correspond à l'attribut
          'nomFichier' dans l'attribut 'config' (un objet JSONObject).
         */
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
                this.config = new JSONObject(json);
                // System.out.println("Contenu JSON : "+json);
                // System.out.println("key adresse "+objet.get("adresse"));
                // System.out.println("key port "+objet.get("port"));
            }
            catch(JSONException jes) {
                System.out.println("erreur chargement JSONException 0+jes");
            }
        }
        else {
            System.out.println("le fichier n'existe pas");
        }
    }
    
    /**
     * Sauvegarde la configuration dans le fichier.
     */
    public void sauvegarder() {
        // Création du fichier de sortie
        
        /*
          Sauvegarder le JSONObject config dans le fichier dont le nom
          correspond à l'attribut 'nomFichier'.
         */
        // Ajout du tableau

        // Création du fichier de sortie
        FileWriter fs = null;
        try {
            fs = new FileWriter(nomFichier);
        } catch(IOException e) {
            System.err.println("Erreur lors de l'ouverture du fichier '" + nomFichier + "'."+e);
            System.exit(-1);
        }

        // Sauvegarde dans le fichier
        try {
            config.write(fs);
            fs.flush();
            fs.close();   // Fermeture du fichier
        }
        catch(IOException e) {
            System.err.println("Erreur lors de l'écriture dans le fichier."+e);
            System.exit(-1);
        }
        catch(JSONException ej) {
            System.err.println("Erreur lors de l'écriture du json dans le fichier."+ej);
            System.exit(-1);
        }

        //System.out.println("Le fichier '" + nomFichier + "' a été généré.");
    }


    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("fichier", this.nomFichier);
            jo.put("config", this.config);
        }
        catch (JSONException e) {
            System.out.println("erreur "+e);
        }
        return jo;
    }
    
}
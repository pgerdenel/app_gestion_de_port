package C3_gestion_de_port.objects.port;

import C3_gestion_de_port.objects.MyJSONApi;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/*
 * Gérer un port pour le stationnement des bateaux.
 *
 * Un port propose la location de places de parking,
 * L'application permet à un client de récupérer les informations sur les emplacements libres, le prix des places.
 * Un port est caractérisé par :
 * -un nom de port (String)
 * -un récapitulatif des places par longueur de bateaux (hashmap<longueur_bateau, List<Place>>) (10 places maximum par longueur)
 * -une liste des prix (JSONObject)
 * Il est possible également de vérifier :
 * - les emplacements libres du parking
 * - d'ajouter/retirer un bateau d'un emplacement
 * - de vérifier si un bateau est présent sur une des places du port (par nom_bateau ou nom_bateau+longueur)
 * - de récupérer les informations d'un bateau situé à un emplacement particulier
 * - de remplacer un bateau par un autre à une certaine place
 *
 */

public class Parking_Port {
        public static void main(String[] args) {
        }

        /* Attributes */
        private String nom_port;
        private HashMap<Integer, LinkedList<Place>> map_place;
        private JSONObject prix_place;

        // Constructors
        public Parking_Port() {

        }
        // ParkingPort par default 2
        public Parking_Port(String nom_port) {
                this.nom_port = nom_port;
                this.map_place = new HashMap<>();
                // On parcours l'ensemble des pair<Longueur, prix>
                for (E_Prix longToPrix : E_Prix.values()) {
                        // On crée une Liste<place> pour chaque longeur
                        LinkedList<Place> l = new LinkedList<>();
                        // On crée 10 places de cette longeur que l'on met dans la liste
                        for(int i=0;i<10;i++) {
                                l.add(new Place(longToPrix.getE_Longueur().getLongueur(),longToPrix.getE_Prix(),i+1, true,false,"unknow"));
                                //System.out.println("new Place added = "+ l_empty.get(i).toString());
                        }
                        //System.out.println("list = "+l_empty.size());
                        // On ajoute la liste dans la map_place
                        this.map_place.put(longToPrix.getE_Longueur().getLongueur(),l);
                }
                this.prix_place = E_Prix.toJson();
        }
        // ParkingPort par initilisation
        public Parking_Port(String nom_port, HashMap<Integer, LinkedList<Place>> map_place, JSONObject prix_place_json) {
                this.nom_port = nom_port;
                this.map_place = map_place;
                this.prix_place = prix_place_json;
        }
        // ParkingPort par fichier json
        @SuppressWarnings("unchecked")
        public Parking_Port(String nom_port, String nomFichier_informations, String nomFichier_prixplace) {
                String path = "src/C3_gestion_de_port/resources/ports/"+nom_port+"/";
                this.nom_port = nom_port;
                // On reconstruit la HashMap<Longueur, List<Place>> avec les données du fichier informations_port.json
                try {
                        HashMap<Integer, LinkedList<Place>> map_tmp = new HashMap<>();
                        // Ouverture du fichier
                        FileInputStream fs = null;
                        try {
                                fs = new FileInputStream(path+nomFichier_informations);
                        } catch(FileNotFoundException e) {
                                System.err.println("Fichier '" + nomFichier_informations + "' introuvable "+e);
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

                        // Création d'un objet JSON
                        JSONObject jo = new JSONObject(json);

                        // On itère toutes les longueurs (clé 1)
                        Iterator<String> keys = jo.keys();
                        while(keys.hasNext()) {
                                String key = keys.next();
                                if (jo.get(key) instanceof JSONObject) {
                                        LinkedList<Place> l = new LinkedList<>();
                                        JSONObject je = ((JSONObject) jo.get(key));
                                        // System.out.println("key= "+key+" value= "+je.toString());
                                        //System.out.println("key longueur = "+key);
                                        // On itère toutes les places (clé2)
                                        Iterator<String> keys2 = je.keys();
                                        while(keys2.hasNext()) {
                                                String key2 = keys2.next();
                                                if (je.get(key2) instanceof JSONObject) {
                                                        JSONObject je2 = ((JSONObject) je.get(key2));
                                                        //System.out.println("key num_place = "+key2);
                                                        l.add(new Place(Integer.valueOf(key),je2.getInt("prix"),Integer.valueOf(key2), je2.getBoolean("isEmpty"), je2.getBoolean("option_gardiennage"), je2.getString("nom_bateau")));
                                                }
                                        }
                                        // On ajoute la liste de cette longueur dans la hashmap
                                        map_tmp.put(Integer.valueOf(key), l);
                                        //System.out.println();
                                }
                        }
                        this.map_place = new HashMap<>(map_tmp);
                }
                catch(JSONException jo) {
                        System.err.println("Erreur lors de la construction de l'objet Place a partir d'un JSONObject place_json "+jo);
                }
                // On reconstruit la HashMap<Longueur, List<Place>> avec les données du fichier informations_port.json
                // Ouverture du fichier
                FileInputStream fs = null;
                try {
                        fs = new FileInputStream(path+nomFichier_prixplace);
                }
                catch(FileNotFoundException e) {
                        System.err.println("Fichier '" + nomFichier_prixplace + "' introuvable");
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
                }
                catch(IOException e) {
                        System.err.println("Erreur lors de la fermeture du fichier.");
                        System.err.println("error "+e);
                        System.exit(-1);
                }

                try {
                        // L'objet json récupéré est l'objet JSON prix_place
                        this.prix_place = new JSONObject(json);
                }
                catch(JSONException jes) {
                        System.out.println("erreur chargement JSONException 0+jes");
                }
        }
        // par copie
        public Parking_Port(Parking_Port p) {
                this.nom_port = p.nom_port;
                this.map_place = p.map_place;
                this.prix_place = p.prix_place;
        }
        // Getters & Setters
        public HashMap<Integer, LinkedList<Place>> getMap_place() {
                return map_place;
        }
        public void setMap_place(HashMap<Integer, LinkedList<Place>> map_place) {
                this.map_place = new HashMap<>(map_place);
        }
        public String getNom_port() {
                return nom_port;
        }
        public void setNom_port(String nom_port) {
                this.nom_port = nom_port;
        }
        public JSONObject getPrix_place() {
                return prix_place;
        }
        public void setPrix_place(JSONObject prix_place) {
                this.prix_place = prix_place;
        }

        /* Others Methodes */
        // Convertit une map en string
        private String mapToString() {
                System.out.println("mapToString called");
                StringBuilder map_str = new StringBuilder();
                for (HashMap.Entry<Integer, LinkedList<Place>> entry : map_place.entrySet())
                {
                        map_str.append(entry.getKey());
                        map_str.append(" --> ");
                        map_str.append(entry.getValue());
                        map_str.append("\n");
                }
                return map_str.toString();
        }
        @Override
        public String toString() {
                return "\n> Parking_Port Object = \n" +
                        " nom_port = " + nom_port+
                        "\n\n map_place = \n" + mapToString()+
                        "\n prix_place = \n" + prix_place;
        }
        // Permet de récupérer le nom des bateaux garés dans le port
        public JSONObject getNomBateau() {
                JSONObject jo = new JSONObject();
                // On parcours chaque liste<Place> de chaque longueur de la map
                for (HashMap.Entry<Integer, LinkedList<Place>> entry : map_place.entrySet()) {
                        // Pour chaque place des différentes listes
                        for (Place place : entry.getValue()) {
                                // on vérifie si le nom_bateau est présent
                                if (!place.isEmpty()) {
                                        try {
                                                jo.put(String.valueOf(place.getNum_place()), place.getNom_bateau());
                                        }
                                        catch (JSONException je) {
                                                System.out.println("getFreePlace(), erreur de construction de l'objet JSON");
                                        }
                                }
                        }
                }
                return jo;
        }
        // Récupère les places libres
        public JSONObject getFreePlace() {
                //System.out.println(this);
                JSONObject jo_longueur = new JSONObject();
                JSONObject jo_final = new JSONObject();
                //int i=1;
                HashMap<Integer, LinkedList<Place>> lltmp = new HashMap<>(map_place);
                try {
                        Iterator<Map.Entry<Integer, LinkedList<Place>>> it = lltmp.entrySet().iterator();
                        while (it.hasNext()) {
                                Map.Entry<Integer, LinkedList<Place>> pair = it.next();
                                JSONObject jo_numplace = new JSONObject();
                                for (int i = 0; i < pair.getValue().size(); ++i) {
                                        if (pair.getValue().get(i).isEmpty()) {
                                                jo_numplace.put(String.valueOf(i+1), pair.getValue().get(i).toJson());
                                                //System.out.println("la place "+i+ " de longueur "+pair.getKey().toString()+" est retenu "+pair.getValue().get(i).toJson().toString(1));
                                        }
                                        else {
                                                System.out.println("la place "+i+ " de longueur "+pair.getKey().toString()+" n'est pas retenu "+pair.getValue().get(i).toJson().toString(1));

                                        }
                                }

                                jo_longueur.put(pair.getKey().toString(), new JSONObject(jo_numplace.toString()));

                        }
                        //System.out.println(jo_longueur.toString(1));
                        jo_final = new JSONObject(jo_longueur.toString());
                }
                catch (JSONException je) {
                        System.out.println("getFreePlace(), erreur de construction de l'objet JSON");
                }
                return jo_final;
        }
        // ajoute un bateau à une place précise
        public boolean addBateauAtPlace(String nom_bateau, int longueur, int num_place) {
                System.out.println("on ajoute le bateau "+nom_bateau+ " de longueur "+longueur+" a l'emplacement "+num_place);
                boolean result;
                num_place--; // les index des listes commencent à 0 mais pour les utilisateurs à 1

                LinkedList<Place> ll_tmp = map_place.get(longueur);
                Place p_tmp = ll_tmp.get(num_place);
                if(p_tmp.isEmpty()) {
                        p_tmp.setNom_bateau(nom_bateau);
                        p_tmp.setEmpty(false);
                        ll_tmp.set(num_place, p_tmp);
                        map_place.put(longueur, ll_tmp);
                        result = true;
                }
                else {
                        result = false;
                }

                /*if(map_place.get(longueur).get(num_place).isEmpty()) { // si la place est vide
                        map_place.get(longueur).get(num_place).setNom_bateau(nom_bateau);
                        // on affiche la map du parking pour visualiser l'enregistrement
                        result = true;
                }
                else { // si la place n'est pas vide
                       result = false;
                }*/

                return result;
        }
        // enlève un bateau à une place précise
        public boolean removeBateauAtPlace(int longueur, int num_place) {
                System.out.println("\t> on enleve le bateau de longueur "+longueur+" a l'emplacement "+num_place);
                boolean result;
                num_place--; // les index des listes commencent à 0 mais pour les utilisateurs à 1

                /*LinkedList<Place> ll_tmp = map_place.get(longueur);
                Place p_tmp = ll_tmp.get(num_place);
                p_tmp.setNom_bateau("unknow");
                ll_tmp.set(num_place, p_tmp);
                map_place.put(longueur, ll_tmp);*/

                if(!map_place.get(longueur).get(num_place).isEmpty()) { // si la place n'est pas vide
                        map_place.get(longueur).get(num_place).setNom_bateau("");
                        map_place.get(longueur).get(num_place).setEmpty(true);
                        //System.out.println("\n\tnouvel map = "+map_place.get(longueur).toString());
                        result = true;
                }
                else { // si la place est vide
                        result = false;

                }
                return result;
        }
        // Vérifie si un bateau est garé dans le port par le nom_bateau
        public boolean checkBateauIn(String nom_bateau) {
                boolean isIn =false;

                System.out.println("on verifie si le bateau de nom \"" + nom_bateau + "\" est gare dans le port ...");

                if(map_place.size() > 0) {
                        // On parcours chaque liste<Place> de chaque longueur de la map
                        for (Map.Entry<Integer, LinkedList<Place>> entry : map_place.entrySet()) {
                                // Pour chaque place des différentes listes
                                for (Place place : entry.getValue()) {
                                        // on vérifie si le nom_bateau est présent
                                        if (place.getNom_bateau().equals(nom_bateau)) {
                                                isIn = true;
                                        }
                                }
                        }
                }
                return isIn;
        }
        // Vérifie si un bateau est garé dans le port par le nom_bateau et le numéro de place
        public boolean checkBateauInWithNumPlace(String nom_bateau, int num_place) {
                boolean isIn =false;

                System.out.println("\t> on verifie si le bateau de nom \"" + nom_bateau + "\" est gare dans le port ...");

                if(map_place.size() > 0) {
                        // On parcours chaque liste<Place> de chaque longueur de la map
                        for (Map.Entry<Integer, LinkedList<Place>> entry : map_place.entrySet()) {
                                // Pour chaque place à l'index num_place des différentes listes
                                /*for (Place place : entry.getValue()) {
                                        // on vérifie si le nom_bateau est présent
                                        if (place.getNom_bateau().equals(nom_bateau)) {
                                                isIn = true;
                                        }
                                }*/
                                if(entry.getValue().get((num_place-1)).getNom_bateau().equals(nom_bateau)) {
                                        isIn = true;
                                }
                        }
                }



                return isIn;
        }
        // Vérifie si un bateau est garé dans le port par le nom_bateau et la longueur
        public boolean checkBateauIn(String nom_bateau, int longueur) {
                boolean isIn =false;

                System.out.println("on verifie si le bateau de nom \"" + nom_bateau + "\" est gare dans le port ...");

                if(map_place.size() > 0) {
                        int i = 0;
                        while (i < map_place.get(longueur).size()) {
                                if(map_place.get(longueur).get(i).getNom_bateau().equals(nom_bateau))  {
                                        isIn = true;
                                }
                                i++;
                        }
                }

                return isIn;
        }
        // Vérifie si un bateau est garé dans le port par le nom_bateau et la longueur et le numéro de place
        public boolean checkBateauIn(String nom_bateau, int longueur, int num_place) {
                boolean isIn =false;

                System.out.println("on verifie si le bateau de nom \"" + nom_bateau + "\" est gare dans le port ...");

                if(map_place.size() > 0) {
                        // On vérifie si le nom_bateau correspond à celui présent à la place de la liste de longueur spécifié
                        isIn = map_place.get(longueur).get(num_place).getNom_bateau().equals(nom_bateau);
                }

                return isIn;
        }
        // Récupére le nom du bateau situé à un emplacement particulier
        public String retrieveNomBateau(int longueur, int emplacement) {
                return map_place.get(longueur).get(emplacement-1).getNom_bateau();
        }
        // Permet de remplacer un bateau à une place
        public void replaceBateau(String nom_bateau_old, String nom_bateau_new, int longueur, boolean option_gardiennage) {

                System.out.println("on cherche le " + nom_bateau_old + " et on le remplace par nom_bateau_new...");

                // On parcours chaque liste<Place> de chaque longueur de la map
                for(Map.Entry<Integer, LinkedList<Place>> entry : map_place.entrySet()){
                        // Pour chaque place des différentes listes
                        for(Place place : entry.getValue()){
                                // on vérifie si le nom_bateau est présent
                                if(place.getNom_bateau().equals(nom_bateau_old))  {
                                        place.setNom_bateau(nom_bateau_new);
                                        place.setLongueur(longueur);
                                        // place.setPrix();
                                }
                        }
                }
        }
        // Permet de convertir un objet ParkingPort en un Objet JSONObject
        public JSONObject toJson() {
                JSONObject jo = new JSONObject();
                try {
                        jo.put("nom_port", nom_port);
                        // On parcours chaque liste<Place> de chaque longueur de la map
                        for(Map.Entry<Integer, LinkedList<Place>> entry : map_place.entrySet()) {
                                // Pour chaque place des différentes listes
                                for (Place place : entry.getValue()) {
                                        jo.put(String.valueOf(place.getNum_place()), place.toJson());
                                }
                        }
                        jo.put("prix_place", E_Prix.toJsonArray());
                }
                catch (JSONException e) {
                        System.out.println("erreur "+e);
                }
                return jo;
        }
        /**
         * Permet de découper et d'enregistrer les informations d'un port au format JSON
         * -Dossier de nom "nom_port"
         * -fichier informations_port.json (sans nom_port et sans prix_place)
         * -fichier prix_place.json
         */
        public void writeAllPortInJSONFile() {
                String path = "src/C3_gestion_de_port/resources/ports/"+nom_port+"/";

                // On crée le dossier portant le nom du port
                if (!MyJSONApi.fichierExiste(path+"informations_port.json")) {
                        try {
                                Path pathToFile = Paths.get(path + "informations_port.json");
                                Files.createDirectories(pathToFile.getParent());
                                Files.createFile(pathToFile);
                        }
                        catch (IOException io) {
                                System.out.println("error create folder nom_port");
                        }
                }

                // Création du fichier de sortie pour les informations du port
                FileWriter fs_informations_port = null;
                FileWriter fs_prix_place = null;

                try {
                        fs_informations_port = new FileWriter(path+"informations_port.json");
                        fs_prix_place = new FileWriter(path+"prix_place.json");
                } catch(IOException e) {
                        System.err.println("Erreur lors de la création du dossier "+"src/C3_gestion_de_port/resources/ports/"+nom_port+" ou du fichier informations_port.json");
                        System.err.println("ou");
                        System.err.println("Erreur lors de la création du dossier "+"src/C3_gestion_de_port/resources/ports/"+nom_port+" ou du fichier prix_place.json");
                        System.err.println("error "+e);
                        System.exit(-1);
                }

                // Sauvegarde dans le fichier informations_port.json
                try {
                        // création d'un objet json des informations du port
                        JSONObject jo_long = new JSONObject();
                        JSONObject jo_place = new JSONObject();
                        Iterator<Map.Entry<Integer, LinkedList<Place>>> it = map_place.entrySet().iterator();
                        while (it.hasNext()) {
                                Map.Entry<Integer, LinkedList<Place>> pair = it.next();
                                //System.out.println(pair.getKey().toString()); // longueur
                                //System.out.println(pair.getValue()); // place
                                for (int i = 0; i < pair.getValue().size(); i++) {
                                        //System.out.println(pair.getValue().get(i).toString()); // place index
                                        jo_place.put(String.valueOf(i), new Place(pair.getValue().get(i)).toJson());
                                }
                                //System.out.println(jo_place.toString(1));
                                jo_long.put(pair.getKey().toString(), new JSONObject(jo_place.toString()));
                        }
                        jo_long.write(fs_informations_port);
                        fs_informations_port.flush();
                        fs_informations_port.close();   // Fermeture du fichier

                        // création d'un objet json des prix_place du port
                        prix_place.write(fs_prix_place);
                        fs_prix_place.flush();
                        fs_prix_place.close();   // Fermeture du fichier

                }
                catch(IOException e) {
                        System.err.println("Erreur lors de l'écriture dans le fichier "+e);
                        System.exit(-1);
                }
                catch(JSONException ej) {
                        System.err.println("Erreur lors de l'écriture du json dans le fichier "+ej);
                        System.exit(-1);
                }
        }
        /**
         * Permet d'enregistrer les informations d'un port au format JSON
         * -Dossier de nom "nom_port"
         * -fichier informations_port.json (sans nom_port et sans prix_place)
         */
        public void writeOnlyPortInJSONFile() {
            String path = "src/C3_gestion_de_port/resources/ports/"+nom_port+"/";

            // On crée le dossier portant le nom du port
            if (!MyJSONApi.fichierExiste(path+"informations_port.json")) {
                try {
                    Path pathToFile = Paths.get(path + "informations_port.json");
                    Files.createDirectories(pathToFile.getParent());
                    Files.createFile(pathToFile);
                }
                catch (IOException io) {
                    System.out.println("error create folder nom_port");
                }
            }

            // Création du fichier de sortie pour les informations du port
            FileWriter fs_informations_port = null;

            try {
                fs_informations_port = new FileWriter(path+"informations_port.json");
            } catch(IOException e) {
                System.err.println("Erreur lors de la création du dossier "+"src/C3_gestion_de_port/resources/ports/"+nom_port+" ou du fichier informations_port.json "+e);
                System.exit(-1);
            }

            // Sauvegarde dans le fichier informations_port.json
            try {
                // création d'un objet json des informations du port
                JSONObject jo_long = new JSONObject();
                JSONObject jo_place = new JSONObject();

                Iterator<Map.Entry<Integer, LinkedList<Place>>> it = map_place.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, LinkedList<Place>> pair = it.next();
                    //System.out.println(pair.getKey().toString()); // longueur
                    //System.out.println(pair.getValue()); // place
                    for (int i = 0; i < pair.getValue().size(); i++) {
                            //System.out.println(pair.getValue().get(i).toString()); // place index
                            jo_place.put(String.valueOf(i), new Place(pair.getValue().get(i)).toJson());
                    }
                    //System.out.println(jo_place.toString(1));
                    jo_long.put(pair.getKey().toString(), new JSONObject(jo_place.toString()));
                }

                //System.out.println(jo_long.toString(1));
                jo_long.write(fs_informations_port);
                fs_informations_port.flush();
                fs_informations_port.close();   // Fermeture du fichier

            }
            catch(IOException e) {
                System.err.println("Erreur lors de l'écriture dans le fichier informations.json "+e);
                System.exit(-1);
            }
            catch(JSONException ej) {
                System.err.println("Erreur lors de l'écriture du json dans le fichier informations.json "+ej);
                System.exit(-1);
            }
        }
        /**
         *  Permet d'enregistrer les prixplace d'un port au format JSON
         * -Dossier de nom "nom_port"
         * -fichier informations_port.json (sans nom_port et sans prix_place)
         */
        public void writePrixPlacePortInJSONFile() {
            String path = "src/C3_gestion_de_port/resources/ports/"+nom_port+"/";

            // On crée le dossier portant le nom du port
            if (!MyJSONApi.fichierExiste(path+"prix_place.json")) {
                try {
                    Path pathToFile = Paths.get(path + "prix_place.json");
                    Files.createDirectories(pathToFile.getParent());
                    Files.createFile(pathToFile);
                }
                catch (IOException io) {
                    System.out.println("error create folder nom_port");
                }
            }

            // Création du fichier de sortie pour les prix du port
            FileWriter fs_prix_place = null;

            try {
                fs_prix_place = new FileWriter(path+"prix_place.json");
            } catch(IOException e) {
                System.err.println("Erreur lors de la création du dossier "+"src/C3_gestion_de_port/resources/ports/"+nom_port+" ou du fichier prix_place.json "+e);
                System.exit(-1);
            }

            // Sauvegarde dans le fichier informations_port.json
            try {
                // création d'un objet json des prix_place du port
                prix_place.write(fs_prix_place);
                fs_prix_place.flush();
                fs_prix_place.close();   // Fermeture du fichier

            }
            catch(IOException e) {
                System.err.println("Erreur lors de l'écriture dans le fichier prix_place.json "+e);
                System.exit(-1);
            }
            catch(JSONException ej) {
                System.err.println("Erreur lors de l'écriture du json dans le fichier prix_place.json "+ej);
                System.exit(-1);
            }
        }
        // Renvoie la liste des prix du parking port
        public JSONArray prixToJsonArray() {
                return E_Prix.toJsonArray();
        }
}

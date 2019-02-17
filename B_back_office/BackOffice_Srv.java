package B_back_office;

import B_back_office.objects.Config;
import B_back_office.objects.MyAPI;
import B_back_office.objects.cert.MyCertificat;
import B_back_office.objects.security.*;
import com.sun.net.httpserver.HttpServer;
import B_back_office.objects.MyJSONApi;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * BackOffice permettant de faire la liaison avec les différentes entités du projet
 * port HTTP:8080
 * port socket_handler: de 2080 jusque 2087
 * port 2027 pour l'authorité de certification
 */

public class BackOffice_Srv implements MyJSONApi {

    private static String path = "src\\B_back_office\\resources\\";
    private static HashMap<String, Integer> map_port; // <nom_port, port> == <"port1", 2091>
    private static JSONArray list_proprio;
    private static JSONArray list_model;
    private static JSONArray list_longueur;

    public static void main(String[] args) {

        /* CONFIG **************************************************************************************/
        System.out.println("# CONFIGURATION");
        // Création/récupération de l'objet config
        Config c;
        if(Config.fichierExiste(path+"config.json")) {
            c = new Config(path+"config.json");
            System.out.println("config exist -> config loaded from \""+path+"config.json"+"\""+"\n");
        }
        else {
            c = new Config(path+"config.json", true);
            c.ajouterValeur("adresse", "localhost");
            c.ajouterValeur("port", 8080);
            c.sauvegarder();
            c.sauvegarder();
            System.out.println("config does not exist -> config created at \""+path+"config.json"+"\""+"\n");
        }
        /* INIT CERTIFICAT ***********************************************************************************/
        System.out.println("# INIT CERTIFICAT");
        // Création/récupération des clés publiques & privés
        if(Config.fichierExiste(path+"private_key.bin") && Config.fichierExiste(path+"public_key.bin")) { // on les charge
            System.out.println("keys exist -> key loaded"+"\n");
        }
        else { // On les crée
            System.out.println("keys does not exist -> keys created"+"\n");
            GenerationClesRSA.main(new String[]{path+"private_key.bin", path+"public_key.bin"});
        }
        if(!MyAPI.checkIfCertEtcOk()) {
            while(MyAPI.checkSocketListen(2025)) { // tant que l'autorité de certification n'est pas joignable
                System.out.print("\nAutorite de certification non joignable, retry in "+ 5 +"sec ...");
            }
            // Récupération du certificat
            ask_certificate(2025, 2027);
        }

        /* INIT HTTP_SERVEUR *********************************************************************************/
        System.out.println("# INIT HTTP_SERVEUR");
        HttpServer serveur = null;
        try {
            serveur = HttpServer.create(new InetSocketAddress(c.getInt("port")), 0);
            System.out.println("HTTP-Server -> created"+"\n");
        } catch(IOException e) {
            System.err.println("Erreur lors de la creation du serveur " + e+"\n");
            System.exit(-1);
        }
        /* INIT DATA HTTP_SERVEUR ****************************************************************************/
        System.out.println("# INIT DATA HTTP_SERVEUR");
        map_port = new HashMap<>();
        System.out.println("Data server -> loaded\n");

        // récupération des différents données nécessaires pour l'interface utilisateur
        /*list_proprio = ;
        list_longueur = ;
        list_model = ;*/

        /* INIT EXECUTOR **************************************************************************************/
        // SingleThreadExecutor : un pool qui ne contient qu'un seul thread. Toutes les tâches soumises sont executees de manière sequentielle
        ExecutorService executorService = Executors.newSingleThreadExecutor();


        /* HANDLERS REQUEST ***********************************************************************************/
        System.out.println("# INIT HANDLER REQUEST");

        // Handler All entities
        serveur.createContext("/exchange_certificate.html", new ExchangeCertificate_Handler());
        serveur.createContext("/exchange_AES_key.html", new ExchangeAESKey_Handler());

        // Handler Portail request
        serveur.createContext("/query_list_port.html", new Ajax_Query_ListPort_Handler());
        serveur.createContext("/query_refresh.html", new Ajax_Query_RefreshGUI_Handler());
        serveur.createContext("/query_login.html", new Query_LoginHandler()); // gère les requêtes de login transmises par le C1_portail et à transmettre au gestionnaire_user

        // Handler GestionnairePort request
        serveur.createContext("/record_port.html", new Record_PortHandler(map_port)); // gère les enregistrements des ports lors de leur démarrage

        // Handlers Portail AJAX request
        serveur.createContext("/query_place_price.html", new Ajax_Get_PlacePrice_Handler());             // requête pour récupérer le prix des places (ok)
        serveur.createContext("/query_free_place_infos.html", new Ajax_Get_InfosFreePlace_Handler());    // requête pour récupérer les places libres
        serveur.createContext("/query_boat_infos.html", new Ajax_Get_BoatInfos_Handler());         // requête pour récupérer les infos d'un bateau
        serveur.createContext("/query_check_boat_in.html", new Ajax_Query_BoatInPort_Handler());             // requête pour verifier si un bateau est à un emplacement précis d'un port
        serveur.createContext("/query_add_boat.html", new Ajax_Add_BoatAtPlace_Handler());            // requête pour ajouter un bateau à un emplacement particulier
        serveur.createContext("/query_remove_boat.html", new Ajax_Remove_BoatAtPlace_Handler());      // requête pour enlever un bateau d'un emplacement particulier
        serveur.createContext("/query_transfer_boat.html", new Ajax_Transfer_BoatAtPort_Handler());      // requête pour transférer un bateau d'un port à un autre

        System.out.println("All Handler -> loaded\n");

        serveur.setExecutor(executorService);
        serveur.start();

        System.out.println("Back-office started & listen at http://"+c.getString("adresse")+":"+c.getInt("port")+"\n");
    }

    /* Getters & Setters */
    public static String getPath() {
        return path;
    }
    public static void setPath(String path) {
        BackOffice_Srv.path = path;
    }
    public static HashMap<String, Integer> getMap_port() {
        return map_port;
    }
    public static void setMap_port(HashMap<String, Integer> map_port) {
        BackOffice_Srv.map_port = map_port;
    }
    public static void setNewPort(String nom_port, int port) {
        map_port.put(nom_port, port);
        // On enregistre la map_port dans le fichier JSON
        writeToJSONFile(mapPortToJson(), path+"map_port.json");

    }

    /* Others Methods */
    // charge le fichier map_port.json dans la map_port et renvoie un objet JSON de son contenu
    public static JSONObject loadMapPortFromJsonFile() {

        String path_filename = path+"map_port.json";

        /*
         * Charger le fichier JSON dont le nom correspond à l'attribut
         * 'nomFichier' dans l'attribut 'config' (un objet JSONObject).
         */
        JSONObject objet = null;

        if (MyJSONApi.fichierExiste(path_filename)) {
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
                System.err.println("Erreur lors de la fermeture du fichier "+e);
                System.exit(-1);
            }

            // Création d'un objet JSON
            objet = MyJSONApi.castinJSONObject(json.trim());
            //HashMap<String, Integer> h = new HashMap<>();
            Iterator<?> keys = objet.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                //System.out.println("key= "+key + "value= "+objet.get(key)); // key= map_portvalue= {"port1":"2091"}
                JSONObject jo = MyJSONApi.castinJSONObject(MyJSONApi.getString(objet, key));
                //System.out.println("jo = "+jo.toString());

                Iterator<?> keys2 = jo.keys();
                while (keys2.hasNext()) {
                    String key2 = (String) keys2.next();
                    //System.out.println("key= "+key2 + "value= "+jo.get(key2));
                    map_port.put(key2, Integer.valueOf(MyJSONApi.getString(jo, key2)));
                }
            }
            //System.out.println("hashmap temp "+h.toString());
        }
        else {
            System.out.println("le fichier n'existe pas");
        }
        return objet;
    }
    // Convertit la liste de port en une liste_port(JSONObject) {nomport:portnumber}
    public static JSONObject mapPortToJson() {
        HashMap<String, Integer> tmp = new HashMap<>(map_port);
        JSONObject mainObj = new JSONObject();
        JSONObject jo = new JSONObject();

        Iterator it = tmp.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            MyJSONApi.ajouterValeur(jo, pair.getKey().toString(), pair.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
        MyJSONApi.ajouterValeur(mainObj, "map_port", jo);

        return mainObj;
    }
    // Convertit la liste de port en un JSONArray de port(String)
    public static JSONArray getListPortJson() {
        HashMap<String, Integer> tmp = new HashMap<>(map_port);
        JSONArray mainObj = new JSONArray();
        Iterator it = tmp.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            mainObj.put(pair.getKey().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
        return mainObj;
    }
    // Enregistre la map_port dans un fichier json
    public static void writeToJSONFile(JSONObject jo, String path_filename) {
        // Création du fichier de sortie
        FileWriter fs = null;
        try {
            fs = new FileWriter(path_filename);
        }
        catch(IOException e) {
            System.err.println("Erreur lors de l'ouverture du fichier '" + path_filename + "'. "+e);
            System.exit(-1);
        }

        // Sauvegarde dans le fichier
        try {
            jo.write(fs);
            fs.flush();
            fs.close();   // Fermeture du fichier
        }
        catch(IOException e) {
            System.err.println("Erreur lors de l'écriture dans le fichier "+e);
            System.exit(-1);
        }
        catch(JSONException ej) {
            System.err.println("Erreur lors de l'écriture du json dans le fichier "+ej);
            System.exit(-1);
        }

        System.out.println("\t> File \""+path_filename+"\\map_port.json\" updated");
    }
    /**
     * Envoie une requête à l'authorité de certification
     * envoie sa clé publique
     * reçoit la clé publique du serveur
     * enregistre la clé publique du serveur
     * reçoit son certificat
     * enregistre son certificat
     * @param port_number_auth_certif : port sur lequelle l'autorité de certification sera joignable
     * @param port_number_ask_cert : port sur lequelle l'autorité de certification pourra envoyer des données au backoffice
     *
     */
    private static void ask_certificate(int port_number_auth_certif, int port_number_ask_cert) {

        boolean isMessageAskCert_sended = false;        // boolean état "Envoie du message askCert"
        boolean isPublic_Key_client_sended = false;     // boolean état "Envoie de sa clé publique"
        boolean isPublic_key_serveur_received = false;  // boolean état "En reception de la clé publique serveur"
        boolean isSymetric_key_received = false;        // boolean état "En reception de la clé symetrique"
        boolean isCertificat_received = false;          // boolean état "En attente de reception de son certificat
        boolean isRequestEnded = false;                 // boolean état "Etat des échanges terminés"

        while(!isMessageAskCert_sended) {

            // création d'un JSONObject ask_cert
            JSONObject message_ask_cert = new JSONObject();
            MyJSONApi.ajouterValeur(message_ask_cert, "nom_entite", "back_office");
            MyJSONApi.ajouterValeur(message_ask_cert, "port_number", port_number_ask_cert);

            /* INIT UDP_SOCKET_CLIENT */
            // Création de la socket
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket();
                System.out.println("Socket Client created");

            } catch (SocketException e) {
                System.err.println("Erreur lors de la creation de la socket : " + e);
                System.exit(-1);
            }

            // Transformation en tableau d'octets
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(message_ask_cert.toString());
            } catch (IOException e) {
                System.err.println("Erreur lors de la sérialisation : " + e);
                System.exit(-1);
            }

            // Création et envoi du segment UDP
            try {
                byte[] donnees = baos.toByteArray();
                InetAddress adresse = InetAddress.getByName("localhost");
                DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, port_number_auth_certif);
                socket.send(msg);
                System.out.println("message_ask_cert sended");
                isMessageAskCert_sended = true;
                //i--;
                socket.close();

            } catch (UnknownHostException e) {
                System.err.println("Erreur lors de la création de l'adresse : " + e);
                System.exit(-1);
            } catch (IOException e) {
                System.err.println("Erreur lors de l'envoi du message_ask_cert : " + e);
                System.exit(-1);
            } finally {
                System.out.println("socket closed");
                socket.close();
            }

            // on attend 5 secondes entre chaque envoie
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ie) {
                System.err.println("Erreur lors de l'attente imposé entre chaque envoie : " + ie);
                System.exit(-1);
            }
        }

        // Récupération de la clé publique
        PublicKey publicKey_client = GestionClesRSA.lectureClePublique(path+"public_key.bin");
        PrivateKey privateKey = GestionClesRSA.lectureClePrivee(path+"private_key.bin");
        // Création de la socket
        DatagramSocket socket = null;

        /* (2) ENVOIE ET ECHANGE DES CLES *****************************************************************************/

        while(!isRequestEnded) { //tant que le traitement n'est pas correctement fait

            /* (2.1) ENVOIE DE LA CLE PUBLIQUE AU SERVEUR *************************************************************/

            while (!isPublic_Key_client_sended) { // tant que le client n'a pas envoyé sa clé et que le serveur ne l'a pas reçu, on l'envoie 3 fois toutes les 30secondes

                System.out.println("\n> Envoie de la cle publique au serveur");

                /* INIT UDP_SOCKET_CLIENT */
                try {
                    socket = new DatagramSocket();
                    System.out.println("Socket Client created");

                } catch(SocketException e) {
                    System.err.println("Erreur lors de la creation de la socket : " + e);
                    System.exit(-1);
                }

                // Transformation en tableau d'octets
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(publicKey_client);
                } catch (IOException e) {
                    System.err.println("Erreur lors de la sérialisation : " + e);
                    System.exit(-1);
                }

                // Création et envoi du segment UDP
                try {
                    byte[] donnees = baos.toByteArray();
                    InetAddress adresse = InetAddress.getByName("localhost");
                    DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, port_number_ask_cert);
                    socket.send(msg);
                    System.out.println("public key sended");
                    isPublic_Key_client_sended = true;
                    //i--;
                    socket.close();

                }
                catch (UnknownHostException e) {
                    System.err.println("Erreur lors de la création de l'adresse : " + e);
                    System.exit(-1);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de l'envoi du message : " + e);
                    System.exit(-1);
                }
                finally {
                    System.out.println("socket closed");
                    socket.close();
                }


                // si le nombre d'envoie est dépassé mais que la clé publique n'a pas été reçu
                /*if(!isPublic_Key_client_sended && i==0) {
                    isPublic_Key_client_sended = true;
                    // on ferme la socket
                    System.out.println("nombre envoie atteinte\nfermeture de la socket");
                    //socket.close();
                    System.exit(0);
                }*/


                // on attend 30 seconds entre chaque envoie
                try {
                    TimeUnit.SECONDS.sleep(5);
                }
                catch(InterruptedException ie) {
                    System.err.println("Erreur lors de l'attente imposé entre chaque envoie : " + ie);
                    System.exit(-1);
                }

            }
            /* (2.2) RECEPTION DE LA CLE PUBLIQUE SERVEUR ***************************************************************/
            while (!isPublic_key_serveur_received) {

                System.out.println("\n> En attente de la cle publique du serveur");

                /* INIT UDP_SOCKET_SERVEUR */
                try {
                    socket = new DatagramSocket(port_number_ask_cert);
                    System.out.println("Socket listen on "+"localhost:"+port_number_ask_cert);

                } catch(SocketException e) {
                    System.err.println("Erreur lors de la creation de la socket : " + e);
                    System.exit(-1);
                }

                // Lecture du message du serveur
                DatagramPacket msgRecu = null;
                try {
                    byte[] tampon = new byte[1024];
                    msgRecu = new DatagramPacket(tampon, tampon.length);
                    socket.receive(msgRecu);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la réception du message : " + e);
                    System.exit(-1);
                }

                // Récupération de la clé publique serveur
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    PublicKey publicKey_serveur = (PublicKey) ois.readObject();

                    // on sauvegarde la clé publique du serveur dans un fichier
                    GestionClesRSA.sauvegardeClePublique(publicKey_serveur, path+"public_key_authority.bin");

                    System.out.println("¨Public_key recu : " + publicKey_serveur);
                    isPublic_key_serveur_received = true;
                    socket.close();
                    System.out.println("socket closed");
                }
                catch (ClassNotFoundException e) {
                    System.err.println("Objet reçu non reconnu : " + e);
                    System.exit(-1);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la récupération de l'objet : " + e);
                    System.exit(-1);
                }
            }

            /* (3) RECEPTION DE LA CLE SYMETRIQUE ET DECHIFFREMENT ****************************************************/
            String decrypted_symetric_key = null;
            while (!isSymetric_key_received) {

                System.out.println("\n> En attente de la symetric_key de la part du serveur ... ");

                /* INIT UDP_SOCKET_SERVEUR */
                try {
                    socket = new DatagramSocket(port_number_ask_cert);
                    System.out.println("Socket listen on localhost:"+port_number_ask_cert);

                } catch(SocketException e) {
                    System.err.println("Erreur lors de la creation de la socket : " + e);
                    System.exit(-1);
                }

                // Lecture du message du serveur
                DatagramPacket msgRecu = null;
                try {
                    byte[] tampon = new byte[283]; // 10
                    msgRecu = new DatagramPacket(tampon, tampon.length);
                    socket.receive(msgRecu);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la réception de la symetric_key : " + e);
                    System.exit(-1);
                }

                // Récupération de la clé symétrique chiffrée par la clé publique de nous(ce client du code là)
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    byte[] encrypted_symetric_key = (byte[]) ois.readObject();
                    System.out.println("Clé symétrique encrypted: " + new String(encrypted_symetric_key));

                    // on décrypte la clé symétrique avec la clé asymétrique privée RSA du client
                    decrypted_symetric_key = MyChiffrement_Asymetric_RSA.decrypt_message_str(privateKey, encrypted_symetric_key); // /*(SecretKey) MySecurity.deserialize(*/
                    System.out.println("Clé symétrique decrypted: " + decrypted_symetric_key);

                    isSymetric_key_received = true;
                    socket.close();
                }
                catch (Exception e) {
                    System.err.println("Erreur lors de la récupération de la symetric_key : " + e);
                    System.exit(-1);
                }
            }

            /* (4) RECEPTION DE L'ENCRYPTED CERTIFICAT ET DECHIFFREMENT ***********************************************/
            while (!isCertificat_received) {

                System.out.println("\n> En attente du certificat de la part du serveur .... ");

                /* INIT UDP_SOCKET_SERVEUR ****************************************************************************/
                try {
                    socket = new DatagramSocket(port_number_ask_cert);
                    System.out.println("Socket listen on localhost:"+port_number_ask_cert);

                } catch(SocketException e) {
                    System.err.println("Erreur lors de la creation de la socket : " + e);
                    System.exit(-1);
                }

                // Lecture du message du serveur
                DatagramPacket msgRecu = null;
                try {
                    byte[] tampon = new byte[1024]; // 779
                    msgRecu = new DatagramPacket(tampon, tampon.length);
                    socket.receive(msgRecu);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la réception du certificat : " + e);
                    System.exit(-1);
                }

                // Récupération du certificat
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    byte[] encrypted_certificat = (byte[]) ois.readObject();

                    // on décrypte le certificat avec la clé symetric
                    String decrypted_certificat = MyChiffrement_Symetric_AES.decrypt_message(decrypted_symetric_key, encrypted_certificat);

                    // on reconstruit l'objet json depuis le certificat String décrypté
                    JSONObject my_certificat_json = new JSONObject(decrypted_certificat);
                    // on reconstruit l'objet certificat depuis le JSONObject certiticat
                    MyCertificat myCertificat = new MyCertificat(my_certificat_json);
                    System.out.println("Recu : " + myCertificat.toString());

                    // on enregistre le certificat dans un fichier
                    myCertificat.writeJSONCert(path, "cert_back_office.json");
                    isCertificat_received = true;
                    isRequestEnded = true;
                }
                catch (ClassNotFoundException e) {
                    System.err.println("certificat reçu non reconnu : " + e);
                    System.exit(-1);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la récupération du certificat : " + e);
                    System.exit(-1);
                }
                catch(JSONException je) {
                    System.err.println("Erreur lors de la désérialisation de l'objet certificat : " + je);
                    System.exit(-1);
                }
                finally {
                    System.out.println("\n");
                }
            }

        }
    }

    /* Getters & Setters */
    public static JSONArray getList_proprio() {
        return list_proprio;
    }
    public static void setList_proprio(JSONArray list_proprio) {
        BackOffice_Srv.list_proprio = list_proprio;
    }
    public static JSONArray getList_model() {
        return list_model;
    }
    public static void setList_model(JSONArray list_model) {
        BackOffice_Srv.list_model = list_model;
    }
    public static JSONArray getList_longueur() {
        return list_longueur;
    }
    public static void setList_longueur(JSONArray list_longueur) {
        BackOffice_Srv.list_longueur = list_longueur;
    }
}

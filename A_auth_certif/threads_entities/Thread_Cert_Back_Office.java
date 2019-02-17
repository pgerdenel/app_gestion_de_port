package A_auth_certif.threads_entities;

import A_auth_certif.AuthCertif_Srv;
import A_auth_certif.objects.MyAPI;
import A_auth_certif.objects.cert.MyCertificat;
import A_auth_certif.objects.security.GestionClesRSA;
import A_auth_certif.objects.security.MyChiffrement_Asymetric_RSA;
import A_auth_certif.objects.security.MyChiffrement_Symetric_AES;
import A_auth_certif.objects.security.MySecurity;

import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Thread liés au traitement du backoffice
 */
public class Thread_Cert_Back_Office implements Runnable {

    public static void main(String args[]) {

    }

    private int port_number;

    // Constructor
    public Thread_Cert_Back_Office(String port_number) {
            this.port_number = Integer.valueOf(port_number);
    }

    @Override
    public void run() {
        System.out.println("\t> Thread_Cert_Back_Office RUN() called with data parameter = "+port_number);

        // Récupération de la clé publique
        PublicKey publicKey_serveur = GestionClesRSA.lectureClePublique(AuthCertif_Srv.getPath()+ "public_key.bin");

        boolean isPublic_key_client_received = false;   // boolean état "En attente de clé publique"
        boolean isPublic_key_serveur_sended = false;    // boolean état "Envoie de sa clé publique au client"
        boolean isSymetric_key_sended  = false;         // boolean état "Envoie de la clé symétrique au client"
        boolean isCertificat_sended = false;            // boolean état "Envoie du certificat client généré"
        boolean isRequestEnded = false;                 // boolean état "Etat des échanges terminés"
        PublicKey publicKey_client = null;

        // Création de la socket
        DatagramSocket socket = null;

        /* (2) ENVOIE ET ECHANGE DES CLES *****************************************************************************/

        while(!isRequestEnded) { // tant que le traitement n'est pas fait

            /* (2.1) RECEPTION DE LA CLE PUBLIQUE CLIENT **************************************************************/
            while (!isPublic_key_client_received) { // tant que la clé du client n'a pas été reçu, on se met en attente de requête

                System.out.println("\n> Reception de la cle publique du back_office");

                /* INIT UDP_SOCKET_SERVEUR */
                try {
                    socket = new DatagramSocket(port_number);
                    System.out.println("Socket listen on localhost"+port_number);

                }
                catch(SocketException e) {
                    System.err.println("Erreur lors de la création de la socket : " + e);
                    System.exit(-1);
                }

                System.out.println("En attente de la cle publique du back_office ....\n");
                // Lecture du message du client
                DatagramPacket msgRecu = null;
                try {
                    byte[] tampon = new byte[1024];
                    msgRecu = new DatagramPacket(tampon, tampon.length);
                    socket.receive(msgRecu);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la réception de la cle publique : " + e);
                    System.exit(-1);
                }

                // Récupération de la clé publique cliente
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    publicKey_client = (PublicKey) ois.readObject();
                    // on enregistre la clé publique du client
                    GestionClesRSA.sauvegardeClePublique(publicKey_client, AuthCertif_Srv.getPath()+"public_key_back_office.bin");
                    System.out.println("Recu : " + publicKey_client);
                    isPublic_key_client_received = true; // la clé est reçu on passe à autre chose

                }
                catch (ClassNotFoundException e) {
                    System.err.println("Objet reçu non reconnu : " + e);
                    System.exit(-1);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la récupération de la clé publique : " + e);
                    System.exit(-1);
                }
                finally {
                    System.out.println("socket closed");
                    socket.close();
                }
            }
            /* (2.2) ENVOIE DE LA CLE PUBLIQUE SERVEUR AU CLIENT ******************************************************/
            while(!isPublic_key_serveur_sended) {

                // on attend 5 secondes avant envoie de la clé
                try {
                    TimeUnit.SECONDS.sleep(5);
                }
                catch(InterruptedException ie) {
                    System.err.println("Erreur lors de l'attente imposé entre chaque envoie : " + ie);
                    System.exit(-1);
                }

                System.out.println("\n> Envoie de la cle publique au client");

                /* INIT UDP_SOCKET_CLIENT */
                try {
                    socket = new DatagramSocket();
                    System.out.println("Socket Client created");

                }
                catch(SocketException e) {
                    System.err.println("Erreur lors de la création de la socket : " + e);
                    System.exit(-1);
                }

                // Transformation en tableau d'octets
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(publicKey_serveur);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la sérialisation : " + e);
                    System.exit(-1);
                }

                // Création et envoi du segment UDP
                try {
                    byte[] donnees = baos.toByteArray();
                    InetAddress adresse = InetAddress.getByName("localhost");
                    DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, port_number);
                    socket.send(msg);
                    System.out.println("public key sended");
                    isPublic_key_serveur_sended = true;

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
                    //System.out.println("socket closed");
                    socket.close();
                }
            }
            /* (3) GENERATE CERTIFICAT OF CLIENT **********************************************************************/

            DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
            Date dateStart=null;
            Date dateEnd=null;
            try {
                dateStart = formatter.parse("07/01/19");
                dateEnd = formatter.parse("17/01/19");
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            // creation du certificat du client
            MyCertificat certificat = new MyCertificat("Auth_srv_en_bikini", "email@admin.com", "BackOffice_Srv", "v1", "58:57:59:58:57:59:58:57:59:58:57:59", "RSA", "localhost", dateStart.toString(), dateEnd.toString(), "O", publicKey_client,  MyAPI.getMac()); // O business
            // sauvegarde du certificat du client
            certificat.writeJSONCert(AuthCertif_Srv.getPath(), "cert_back_office.json");
            //MyCertificat certificat = new MyCertificat("Auth_srv_verisian", "email@admin.com", "Client1", "v1", "58:57:59:58:57:59:58:57:59:58:57:59", "RSA", "localhost", dateStart.toString(), dateEnd.toString(), "O", publicKey_client,  MyAPI.getMac()); // O business
            System.out.println("certificat created for client= "+certificat.toJson());

            /* (4) GENERATE A SYMETRIC KEY ****************************************************************************/
            //SecretKey passphrase = MySecurity.generate_key(); // on génère une passphrase(clé AES)
            String passphrase = MySecurity.generate_key_str(); // on génère une passphrase(clé AES)
            System.out.println("passphrase base "+passphrase);

            /* (5) ENCRYPT THE DATA WITH THE SYMETRIC KEY *************************************************************/
            byte[] encrypted_certificat = MyChiffrement_Symetric_AES.encrypte_message(passphrase, certificat.toJson().toString()); // certificat chiffres

            /* (6) ENCRYPT THE SYMETRIC KEY WITH PUBLIC KEY RSA CLIENT ************************************************/
            byte[] encrypted_symetric_key = MyChiffrement_Asymetric_RSA.encrypte_message(publicKey_client, passphrase, false);

            /* (7) SEND ENCRYPTED KEY *********************************************************************************/
            while(!isSymetric_key_sended) {

                // on attend 5 secondes avant envoie de la clé symétrique
                try {
                    TimeUnit.SECONDS.sleep(5);
                }
                catch(InterruptedException ie) {
                    System.err.println("Erreur lors de l'attente imposé entre chaque envoie : " + ie);
                    System.exit(-1);
                }

                System.out.println("\n> Envoie de la symetric key au client");

                /* INIT UDP_SOCKET_CLIENT */
                try {
                    socket = new DatagramSocket();
                    System.out.println("Socket Client created");

                }
                catch(SocketException e) {
                    System.err.println("Erreur lors de la création de la socket : " + e);
                    System.exit(-1);
                }


                // Transformation en tableau d'octets
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(encrypted_symetric_key);

                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la sérialisation : " + e);
                    System.exit(-1);
                }

                // Création et envoi du segment UDP
                try {
                    byte[] donnees = baos.toByteArray();
                    InetAddress adresse = InetAddress.getByName("localhost");
                    DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, port_number);
                    //DatagramPacket msg = new DatagramPacket(encrypted_symetric_key, encrypted_symetric_key.length, adresse, port_number);
                    socket.send(msg);

                    // on affiche la taille en Byte de l'objet
                    System.out.println("encrypted_symetric_key sended "+ new String(encrypted_symetric_key)+" (size= "+donnees.length+")");

                    isSymetric_key_sended = true;

                }
                catch (UnknownHostException e) {
                    System.err.println("Erreur lors de la création de l'adresse : " + e);
                    System.exit(-1);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de l'envoi de la clé symétrique : " + e);
                    System.exit(-1);
                }
                finally {
                    System.out.println("socket closed");
                    socket.close();
                }
            }
            // on attend 5 seconds entre chaque envoie
            try {
                TimeUnit.SECONDS.sleep(5);
            }
            catch(InterruptedException ie) {
                System.err.println("Erreur lors de l'attente imposé entre chaque envoie : " + ie);
                System.exit(-1);
            }
            /* (8) SEND ENCRYPTED CERTIFICAT **************************************************************************/
            while(!isCertificat_sended) {

                // on attend 5 secondes avant envoie du certificat
                try {
                    TimeUnit.SECONDS.sleep(5);
                }
                catch(InterruptedException ie) {
                    System.err.println("Erreur lors de l'attente imposé entre chaque envoie : " + ie);
                    System.exit(-1);
                }

                System.out.println("\n> Envoie du certificat au client");

                /* INIT UDP_SOCKET_CLIENT *****************************************************************************/
                try {
                    socket = new DatagramSocket();
                    System.out.println("Socket Client created");

                }
                catch(SocketException e) {
                    System.err.println("Erreur lors de la création de la socket : " + e);
                    System.exit(-1);
                }

                // Transformation en tableau d'octets
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(encrypted_certificat);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la sérialisation : " + e);
                    System.exit(-1);
                }

                // Création et envoi du segment UDP
                try {
                    byte[] donnees = baos.toByteArray();
                    InetAddress adresse = InetAddress.getByName("localhost");
                    DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, port_number);
                    socket.send(msg);
                    // on affiche la taille en Byte de l'objet
                    System.out.println("encrypted_certificat sended, (size= "+donnees.length+")");
                    isCertificat_sended = true;

                    // on remplit la map_entite de AuthCertif_srv
                    AuthCertif_Srv.getMap_entite().put("back_office", true);
                    System.out.println("\nNew Map Entitie= \n"+AuthCertif_Srv.getMap_entite().toString()+"\n");

                    isRequestEnded = true;
                }
                catch (UnknownHostException e) {
                    System.err.println("Erreur lors de la création de l'adresse : " + e);
                    System.exit(-1);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de l'envoi du certificat : " + e);
                    System.exit(-1);
                }
                finally {
                    socket.close();
                    System.out.println("\n> Socket always listenning ");
                    System.out.println("> En attente de requete demande de certicat de la part des entites ....\n");
                }
            }
        }
    }
}

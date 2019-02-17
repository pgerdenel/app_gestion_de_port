package A_auth_certif.threads_entities;

import A_auth_certif.AuthCertif_Srv;
import A_auth_certif.objects.MyAPI;
import A_auth_certif.objects.cert.MyCertificat;
import A_auth_certif.objects.security.GestionClesRSA;
import A_auth_certif.objects.security.MyChiffrement_Asymetric_RSA;
import A_auth_certif.objects.security.MyChiffrement_Symetric_AES;
import A_auth_certif.objects.security.MySecurity;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Thread_Cert_Portail implements Runnable {

    public static void main(String args[]) {

    }

    private int port_number;

    // Constructor
    public Thread_Cert_Portail(String port_number) {
            this.port_number = Integer.valueOf(port_number);
    }

    @Override
    public void run() {
        System.out.println("\t> Thread_Cert_Portail RUN() called with data parameter = "+port_number);

        // Récupération de la clé publique
        PublicKey publicKey_serveur = GestionClesRSA.lectureClePublique(AuthCertif_Srv.getPath()+ "public_key.bin");

        boolean isPublic_key_client_received = false;   // boolean état "En attente de clé publique"
        boolean isPublic_key_serveur_sended = false;    // boolean état "Envoie de sa clé publique au client"
        boolean isSymetric_key_sended  = false;         // boolean état "Envoie de la clé symétrique au client"
        boolean isCertificat_sended = false;            // boolean état "Envoie du certificat client généré"
        boolean isRequestEnded = false;                 // boolean état "Etat des échanges terminés"
        PublicKey publicKey_client = null;

        int i=2;
        // Création de la socket
        DatagramSocket socket = null;

        /* (2) ENVOIE ET ECHANGE DES CLES *****************************************************************************/

        while(!isRequestEnded) { // tant que le traitement n'est pas fait

            /* (2.1) RECEPTION DE LA CLE PUBLIQUE CLIENT **************************************************************/
            while (!isPublic_key_client_received) { // tant que la clé du client n'a pas été reçu, on se met en attente de requête

                System.out.println("\n> Reception de la cle publique du portail");


                /* INIT UDP_SOCKET_SERVEUR */
                try {
                    socket = new DatagramSocket(port_number);
                    System.out.println("Socket listen on localhost"+port_number);

                }
                catch(SocketException e) {
                    System.err.println("Erreur lors de la création de la socket : " + e);
                    System.exit(-1);
                }

                System.out.println("En attente de la cle publique du portail ....\n");
                // Lecture du message du client
                DatagramPacket msgRecu = null;
                try {
                    byte[] tampon = new byte[1024]; // 451
                    msgRecu = new DatagramPacket(tampon, tampon.length);
                    socket.receive(msgRecu);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la réception de la cle publique : " + e);
                    System.exit(-1);
                }

                /* Récupération de la clé publique cliente */
                // Récupérer la clé en String depuis les bytes lus
                ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData(), 0 ,451);
                String public_key_portail_header = readPEMFromPHP(bais);

                // Ecriture dans un fichier de la clé PEM avec header et footer
                System.out.println("PEM publickey_portail_header= \n"+public_key_portail_header+"\n");
                MySecurity.backupPublicKeyInFile(AuthCertif_Srv.getPath(), "publickey_portail_header.pem", public_key_portail_header);
                // Ecriture dans un fichier de la clé PEM sans header & footer
                MySecurity.backupPublicKeyInFile(AuthCertif_Srv.getPath(), "public_key_portail.pem", extractKeyFromPHP(public_key_portail_header));

                // Conversion de la clé PEM String en PublicKey
                publicKey_client = getPublicKey(public_key_portail_header);
                System.out.println("clé publique convertit en java "+publicKey_client.toString());

                // Ecriture de la clé PublicKey dans un fichier
                GestionClesRSA.sauvegardeClePublique(publicKey_client, AuthCertif_Srv.getPath()+"public_key_portail.bin");
                System.out.println("Recu : " + publicKey_client.toString());
                isPublic_key_client_received = true; // la clé est reçu on passe à autre chose

                System.out.println("socket closed");
                socket.close();
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

                // on convertit la clé public_serveur en format pem
                String PEM_public_key_serveur = MySecurity.convertPublicKeyToPem(publicKey_serveur);

                // Création et envoi du segment UDP
                try {
                    //publicKey_serveur
                    //byte[] donnees = baos.toByteArray();
                    //byte[] donnees = "test".getBytes();
                    byte[] donnees = PEM_public_key_serveur.getBytes();
                    InetAddress adresse = InetAddress.getByName("localhost");
                    DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, port_number);
                    socket.send(msg);
                    System.out.println("public key sended (size="+donnees.length+")");
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
            MyCertificat certificat = new MyCertificat("Auth_srv_en_bikini", "email@admin.com", "Portail", "v1", "58:57:59:58:57:59:58:57:59:58:57:59", "RSA", "localhost", dateStart.toString(), dateEnd.toString(), "O", publicKey_client,  MyAPI.getMac()); // O business
            // sauvegarde du certificat du client
            certificat.writeJSONCert(AuthCertif_Srv.getPath(), "cert_portail.json");
           System.out.println("certificat created for client= "+certificat.toJson());

            /* (4) GENERATE A SYMETRIC KEY ****************************************************************************/
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

                // Création et envoi du segment UDP
                try {
                    // encrypted_symetric_key as byte[]
                    InetAddress adresse = InetAddress.getByName("localhost");
                    DatagramPacket msg = new DatagramPacket(encrypted_symetric_key, encrypted_symetric_key.length, adresse, port_number);
                    socket.send(msg);

                    // on affiche la taille en Byte de l'objet
                    System.out.println("encrypted_symetric_key sended "+ new String(encrypted_symetric_key)+" (size= "+encrypted_symetric_key.length+")");

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

                // si le nombre d'envoie est dépassé mais que la clé publique n'a pas été reçu
                /*if(!isPublic_key_serveur_sended && i==0) {
                    // on ferme la socket
                    System.out.println("nombre envoie atteinte\nfermeture de la socket");
                    socket.close();
                    System.exit(0);
                }*/
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

                /*INIT UDP_SOCKET_CLIENT ******************************************************************************/
                try {
                    socket = new DatagramSocket();
                    System.out.println("Socket Client created");

                }
                catch(SocketException e) {
                    System.err.println("Erreur lors de la création de la socket : " + e);
                    System.exit(-1);
                }

                // Transformation en tableau d'octets
                /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(encrypted_certificat);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la sérialisation : " + e);
                    System.exit(-1);
                }*/

                // Création et envoi du segment UDP
                try {
                    //byte[] donnees = baos.toByteArray();
                    InetAddress adresse = InetAddress.getByName("localhost");
                    DatagramPacket msg = new DatagramPacket(encrypted_certificat, encrypted_certificat.length, adresse, port_number);
                    socket.send(msg);
                    // on affiche la taille en Byte de l'objet
                    System.out.println("encrypted_certificat sended, (size= "+encrypted_certificat.length+")");
                    isCertificat_sended = true;

                    // on remplit la map_entite de AuthCertif_srv
                    AuthCertif_Srv.getMap_entite().put("portail", true);
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
                    System.out.println("\n> Socket always listenning ");
                    System.out.println("> En attente de requete demande de certicat de la part des entites ....\n");
                }
            }
        }
    }

    /**
     * Convertit la clé reçue en un String
     * @param bais à lire
     * @return data as String
     */
    private static String readPEMFromPHP(ByteArrayInputStream bais) {
        java.util.Scanner s = new java.util.Scanner(bais).useDelimiter("\\A");
        StringBuilder sb = new StringBuilder();
        while(s.hasNext()) {
            String tchar = s.next();
                sb.append(tchar);
        }
        return sb.toString();
    }

    /**
     * Permet de remplacer le begin et le end de la clé php par rien
     * @param php_key_str clé php string
     * @return clé php sans pre ni post
     */
    private static String extractKeyFromPHP(String php_key_str) {
        StringBuilder sb =new StringBuilder(php_key_str);
        // on replace les 26 premiers caractères "-----BEGIN PUBLIC KEY-----"
        sb.replace(0, 26, "");
        sb.reverse(); // on inverse le String
        // on replace les 24 premiers caractères "-----END PUBLIC KEY-----"
        sb.replace(0, 25, "");
        sb.reverse(); // on inverse le String (bon sens)
        return sb.toString().replace("\n", "");
    }

    /**
     * Convertit la clé publique php reçu en une PublicKey
     * @param is flux contenant la clé
     * @return clePublique au format java
     */
    private static PublicKey convertPHPPubKey(/*Byte[] pub_key*/InputStream is) {
        /*// create the key factory
        KeyFactory kFactory = KeyFactory.getInstance("RSA");
        // generate the public key
        X509EncodedKeySpec spec =  new X509EncodedKeySpec(yourKey);
        PublicKey publicKey = (PublicKey) kFactory.generatePublic(spec);*/
        //byte[] binary = Base64.decode(php_public_key_str);
        BufferedInputStream bis = new BufferedInputStream(is, 16);
        BigInteger modulo = null;
        BigInteger exposant = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            while (true) {
                bis.mark(16);
                if (bis.read() == -1) {
                    bis.reset();
                    break;
                }
                bis.reset();

                Object o = ois.readObject();
                if (o instanceof BigInteger) {
                    BigInteger bi = (BigInteger) o;

                    if (modulo == null) {
                        modulo = bi;
                    } else if (exposant == null) {
                        exposant = bi;
                    }
                }
            }
        }
        catch(Exception e) {
            System.out.println("Execetpion convertrphpukley "+e);
        }

        PublicKey clePublique = null;
        try {
            RSAPublicKeySpec specification = new RSAPublicKeySpec(modulo, exposant);
            KeyFactory usine = KeyFactory.getInstance("RSA");
            clePublique = usine.generatePublic(specification);
        } catch(NoSuchAlgorithmException e) {
            System.err.println("Algorithme RSA inconnu : " + e);
            System.exit(-1);
        } catch(InvalidKeySpecException e) {
            System.err.println("Spécification incorrecte : " + e);
            System.exit(-1);
        }
        return clePublique;
    }


    private static String getKey(String filename)  {
        StringBuilder strKeyPEM = new StringBuilder();
        try {
            // Read key from file
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                strKeyPEM.append(line);
                strKeyPEM.append("\n");
            }
            br.close();
        }
        catch(IOException io) {
            System.out.println("getKey() IOException "+io);
        }
        return strKeyPEM.toString();
    }
    public static PublicKey getPublicKey(String publickey_header)  {
        return getPublicKeyFromString(publickey_header);
    }

    public static PublicKey getPublicKeyFromString(String key)  {
        PublicKey pubKey = null;
        try {
            String publicKeyPEM = key;
            publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
            publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
            byte[] encoded = Base64.decode(publicKeyPEM);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            pubKey = kf.generatePublic(new X509EncodedKeySpec(encoded));
        }
        catch(NoSuchAlgorithmException io) {
            System.out.println("getPublicKeyFromString() NoSuchAlgorithmException "+io);
        }
        catch(InvalidKeySpecException io) {
            System.out.println("getPublicKeyFromString() InvalidKeySpecException "+io);
        }
        return pubKey;
    }

}

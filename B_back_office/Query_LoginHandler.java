package B_back_office;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 *  reçoit le login et pass envoyé par le C1_portail lorsqu'un client se connecte
 *  renvoie ce couple d'identifiant au gestionnaire utilisateur pour vérification
 */

public class Query_LoginHandler implements HttpHandler {

    public void handle(HttpExchange t) {

        // variables
        StringBuilder reponse = null;

        try {
            // Utilisation d'un flux pour lire les donnees du message Http
            BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8));

            // Variables de traitement
            String query = br.readLine(); // on récupère l'objet User au format String Json (chiffré)


            /*// récupération du message en byte[]
            final InputStream ins = t.getRequestBody();

            byte[] buffer = new byte[16];
            System.out.println("buffer size=" + buffer.length);

            final ByteArrayOutputStream baos = new ByteArrayOutputStream(buffer.length);
            int length;
            while ((length = ins.read(buffer, 0, buffer.length)) >= 0) {
                baos.write(buffer);
            }*/

            // on déchiffre la données avec la privatekey du backoffice
            /*byte[] buff = new byte[64];
            String a = MyChiffrement_Asymetric_RSA.decrypt_message(BackOffice_Srv.getPath(), buff);//*query.getBytes())*//*;
            System.out.println("\nQuery_LoginHandler: data déchiffrée recue du Portail= "+a);*/

            /*ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write( query.getBytes() );
            outputStream.write( new byte[84] );

            byte c[] = outputStream.toByteArray( );
            //on déchiffre la données avec la clé AES
            System.out.println("\nQuery_LoginHandler: input length= "+c.length);
            String a = MyChiffrement_Symetric_AES.decrypt_message("CCC08D5EB7CF282C", c);
            System.out.println("\nQuery_LoginHandler: data déchiffrée recue du Portail= "+a);*/

            /* on déchiffre la données avec la clé AES
            byte[] buff = new byte[128];
            byte[] buff2 = new byte[128];
            buff =  query.getBytes();
            buff2 = br.readLine().getBytes();
            // on déchiffre les données avec la clé AES CCC08D5EB7CF282C
            String a = MyChiffrement_Symetric_AES.decrypt_message("CCC08D5EB7CF282C",buff);
            String b = MyChiffrement_Symetric_AES.decrypt_message("CCC08D5EB7CF282C", buff2);
            System.out.println("\nQuery_LoginHandler: data déchiffrée recue du Portail= "+a);
            System.out.println("\nQuery_LoginHandler: data déchiffrée recue du Portail= "+b);*/

            /* On déchiffre la clé avec la clé AES stocké dans le fichier aes_key.json */
            /*JSONObject key_json = MyJSONApi.retrieveJSONObjectFromFile(BackOffice_Srv.getPath()+"key_aes.json");
            System.out.println("key recupere du fichier json "+MyJSONApi.getString(key_json, "portail"));

            String key = MyJSONApi.getString(key_json, "portail");
            String mess = MyChiffrement_Symetric_AES.decrypt_message(key, query.getBytes(StandardCharsets.UTF_8));*/


            // on chiffre cette nouvelle donnée avec la clé publique du gestionnaire user

            System.out.println("\nQuery_LoginHandler: user_json_string encrypted recue du Portail= "+query);
            //System.out.println("\nQuery_LoginHandler: user_json_string decrypted recue du Portail= "+mess);

            /*  Construction d'une requête HTTP
             *  Données envoyées : objet User au format String Json (query)
             *  Destinataire server : Gestionnaire utilisateur
             *  Destinataire handler : Query_LoginHandler
             */

            byte[] postData = query.getBytes(StandardCharsets.UTF_8);
            URL myurl = new URL("http://localhost:9090/query_login.html");
            try {

                HttpURLConnection con = (HttpURLConnection) myurl.openConnection();

                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Java client");
                con.setRequestProperty("Content-Type", "application/json");

                // Envoie de la requête
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.write(postData);
                System.out.println("\nidentifiants utilisateur envoye au gestionnaire_user");

                // reception de la requête
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                reponse = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    reponse.append(line);
                    reponse.append(System.lineSeparator());
                }
                con.disconnect();
                System.out.println("\nQuery_LoginHandler: reponse recue du gestionnaire user= " + reponse);
            }
            catch(ConnectException ce) {
                System.out.println("Error ConnectException "+ce);
                System.exit(-1);
            }
        }
        catch(MalformedURLException me) {
            System.out.println("Error MalformedURLException "+me);
            System.exit(-1);
        }
        catch(ProtocolException pe) {
            System.out.println("Error ProtocolException "+pe);
            System.exit(-1);
        }
        catch(IOException io) {
            System.out.println("Error IOException "+io);
            System.exit(-1);
        }

        /* ENVOIE de la réponse à l'origine de la requête (içi le C1_portail) */
        // Envoi de l'en-tete Http
        try {
            // System.out.println("Envoi de l'en-tete Http");
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, reponse.toString().getBytes().length);
        } catch(IOException e) {
            System.err.println("Erreur lors de l'envoi de l'en-tete : " + e);
            System.exit(-1);
        }
        // Envoi du corps (donnees HTML)
        try {
            // System.out.println("Envoi du corps (donnees HTML)");
            OutputStream os = t.getResponseBody();
            os.write(reponse.toString().getBytes());
            os.close();
        } catch(IOException e) {
            System.err.println("Erreur lors de l'envoi du corps : " + e);
        }
    }
}

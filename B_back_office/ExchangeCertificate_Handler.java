package B_back_office;

import B_back_office.objects.MyJSONApi;
import B_back_office.objects.cert.MyCertificat;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ExchangeCertificate_Handler implements HttpHandler {

    public void handle(HttpExchange t) {

        // Utilisation d'un flux pour lire les donnees du message Http
        String reponse = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8));

            // Variables de traitement
            String query = br.readLine(); // on récupère les données au format String Json
            System.out.println("query recue "+ query+"\n");

            // on reçoit le certificat de l'entité
            JSONObject cert_received = MyJSONApi.castinJSONObject(query);
            MyCertificat cert_entitie = new MyCertificat(cert_received);
            System.out.println("cert_entitie reçue "+cert_entitie.toString());
            System.out.println("nom de l'entite "+ cert_entitie.getProprio());
            // on enregistre le certificat dans un fichier
            MyJSONApi.writeToJSONFile(cert_received, BackOffice_Srv.getPath()+"cert_"+cert_entitie.getProprio()+".json");

            // on récupère le certificat du back-office & on le met dans la réponse
            if(cert_entitie.getProprio().equals("Portail")) {

                // on récupère le certificat du backoffice au format JSONObject
                JSONObject cert_back_office = MyJSONApi.retrieveJSONObjectFromFile(BackOffice_Srv.getPath()+"cert_back_office.json");
                // on reconstruit l'objet certificat du backoffice
                MyCertificat my = new MyCertificat(cert_back_office);
                // on récupère la clé publique du backoffice et on la convertit en clé pem
                // on lui assigne un clé pem
                final String PUBLICKEY_PREFIX    = "-----BEGIN PUBLIC KEY-----";
                final String PUBLICKEY_POSTFIX   = "-----END PUBLIC KEY-----";
                String publicKeyPEM = PUBLICKEY_PREFIX + "\n" + DatatypeConverter.printBase64Binary(my.getPublicKey().getEncoded()).replaceAll("(.{64})", "$1\n") + "\n" + PUBLICKEY_POSTFIX;
                // assigne cette nouvelle clé au certificat
                MyJSONApi.ajouterValeur(cert_back_office, "publicKey", publicKeyPEM);
                reponse = cert_back_office.toString();
            }
            else {
                reponse = MyJSONApi.retrieveJSONObjectFromFile(BackOffice_Srv.getPath()+"cert_back_office.json").toString();
            }

        }
        catch(IOException e) {
            System.out.println("errro "+e);
        }

        //ENVOIE de la réponse à l'origine de la requête
        System.out.println("> Envoie du certificat à l'entité ...");
        // Envoi de l'en-tete Http
        try {
            // System.out.println("Envoi de l'en-tete Http");
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, reponse.getBytes().length);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi de l'en-tete : " + e);
            System.exit(-1);
        }
        // Envoi du corps (donnees HTML)
        try {
            // System.out.println("Envoi du corps (donnees HTML)");
            OutputStream os = t.getResponseBody();
            os.write(reponse.getBytes());
            os.close();
            System.out.println("> certificat envoyee à l'entité");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du corps : " + e);
        }


    }
}

package B_back_office;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;

/**
 * Renvoie la donnée de l'interface à rafraichir
 * list des ports
 */

public class Ajax_Query_ListPort_Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) {

        System.out.println("\nAjax_Query_ListPort_Handler called()");

        // Réponse et traitement de la requête
        String reponse = BackOffice_Srv.getListPortJson().toString();

        //ENVOIE de la réponse à l'origine de la requête (içi le C1_portail)
        System.out.println("> Envoie de la reponse au portail..." +reponse);
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
            System.out.println("> Response envoyee au portail");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du corps : " + e);
        }
    }
}

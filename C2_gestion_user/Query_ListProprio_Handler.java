package C2_gestion_user;

import C2_gestion_user.objects.GestionnaireUtilisateurs;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;

/*
 * Gère la requête HTTP
 * -donne la liste des utilisateurs de type 'propriétaire'
 *
 * Requête '/query_list_proprio.html'
 * Emetteur : BackOffice
 * paramètres reçues:    aucune
 *
 * Réponse 'Ajax_Query_RefreshGUI_Handler'
 * Destinataire : BackOffice
 * paramètres renvoyés:  JSONArray
 *
 */

public class Query_ListProprio_Handler implements HttpHandler {

    public void handle(HttpExchange t) {

        System.out.println("\nQuery_ListProprio_Handler called()");

        // Variables de traitement
        String reponse = new GestionnaireUtilisateurs(GestionUser_Srv.getPath_all_users()).getAllProprio().toString();

        //ENVOIE de la réponse à l'origine de la requête (içi le C1_portail)
        System.out.println("> Envoie de la reponse au backoffice ..." + reponse);

        try {
            // Envoi de l'en-tete Http
            // System.out.println("Envoi de l'en-tete Http");
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, reponse.getBytes().length);

            // Envoi du corps (donnees HTML)
            // System.out.println("Envoi du corps (donnees HTML)");
            OutputStream os = t.getResponseBody();
            os.write(reponse.getBytes());
            os.close();
            System.out.println("> Response envoyee au backoffice");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du corps : " + e);
        }
    }

}

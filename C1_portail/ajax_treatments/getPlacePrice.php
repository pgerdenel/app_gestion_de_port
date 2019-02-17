<?php
/**
 * Script PHP permettant de récupérer les informations sur le prix des places
 * Données renvoyées: longueur, prix
 * Paramètres : nom_port
 * Stockage permanent(cadre du projet): stockage du résultat de la requête dans la session $_SESSION['place_price'] = nomport
 * Stockage permanent(cadre du projet): nom de la requête dans les cookies $_COOKIE['place_price'] = "true"
 */
header("Content-type: application/json");

// On récupère les paramètre de la requête GET
if(isset($_POST['nomport']) && !empty($_POST['nomport']) /*&& isset($_POST['exist'])*/) {

    //if($_POST['exist'] == -1) {
        // On construit la requête http vers le backoffice pour récupérer le prix des places en fonction de la longueur
        $authToken = "test";
        $nom_port = $_POST['nomport'];
        // post request url
        $url = 'http://localhost:8080/query_place_price.html';
        // Setup cURL
        $ch = curl_init($url);
        curl_setopt_array($ch, array(
            CURLOPT_POST => TRUE,
            CURLOPT_RETURNTRANSFER => TRUE,
            CURLOPT_HTTPHEADER => array(
                'Authorization: ' . $authToken,
                'Content-Type: application/json'
            ),
            CURLOPT_POSTFIELDS => $nom_port
        ));

        // Send the request
        $response = trim(curl_exec($ch)); // on trim car espace

        // Gestion des erreurs de connection
        if (curl_error($ch)) {
            $error_msg = curl_error($ch);
            // echo("<br/><br/>> Erreur lors de l'envoie de la requête getPlacePrice(): <br/>&emsp;<b>".$error_msg." at /query_place_price.html</b>");
        }
        /*else {
            //echo("<br/><br/>> Requête getPlacePrice() sended = ".$nom_port);
        }*/

        /*// on stocke le résultat de cette requête en dans $_SESSION['place_price']
        $_SESSION['place_price'] = $response;
        // on crée un cookie du nom de la requête pour que l'Ajax soit au courant que le résultat sera stocké en session
        setcookie("place_price", 0, time() + 3600, '/');*/

        // Si la réponse contient des données on les affiche
        if(!empty($response)) {

            // on notifie par cette variable de session que l'historique doit être rafraichit
            $_SESSION['hist_change'] = 0;

            $placePrice = json_decode($response, true); // on met true pour convertir le résultat en tableau associatif
            echo trim(json_encode($placePrice));
        }
        else { // sinon on écho "un message d'erreur"
            echo 'Erreur lors de la requête';
            die(curl_error($ch));
        }
    /*}
    else {
        $response = json_decode($_SESSION['place_price']);
        // Si la réponse contient des données on les affiche

        // on notifie par cette variable de session que l'historique doit être rafraichit
        $_SESSION['hist_change'] = 0;

        echo $response;
        //$placePrice = json_decode($response, true); // on met true pour convertir le résultat en tableau associatif
        //echo trim(json_encode($placePrice));
    }*/
}
else {
    echo 'erreur de paramètre pour la requête POST';
}

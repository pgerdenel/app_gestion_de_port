<?php
/**
 * Script PHP permettant de récupérer les informations sur les places libres
 * Données renvoyées: longueur, prix, num_place
 * Paramètres : nom_port
 * URL: http://localhost/C1_PORTAIL/ajax_treatments/getInfoFreePlace.php?nomport=port1
 */
header("Content-type: application/json");

// On récupère les paramètre de la requête GET
if(isset($_POST['nomport']) && !empty($_POST['nomport'])) {

    // On construit la requête http vers le backoffice pour récupérer le prix des places en fonction de la longueur
    $authToken = "test";
    $nom_port = $_POST['nomport'];
    // post request url
    $url = 'http://localhost:8080/query_free_place_infos.html';
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
        //echo("<br/><br/>> Erreur lors de l'envoie de la requête getInfosFreePlace(): <br/>&emsp;<b>".$error_msg." at /query_free_place_infos.html</b>");
    }
    /*else {
        //echo("<br/><br/>> Requête getInfosFreePlace() sended = ".$nom_port);
    }*/

    // Si la réponse contient des données on les affiche
    if(!empty($response)) {
        // on notifie par cette variable de session que l'historique doit être rafraichit
        $_SESSION['hist_change'] = 0;
        $listeInfosFreePlace = json_decode($response, true); // on met true pour convertir le résultat en tableau associatif
        echo json_encode($listeInfosFreePlace);
    }
    else { // sinon on écho "un message d'erreur"
        echo 'Erreur lors de la requête';
        die(curl_error($ch));
    }
}
else {
    echo 'erreur de paramètre pour la requête POST';
}
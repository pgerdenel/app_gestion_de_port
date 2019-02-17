<?php
/**
 * Récupère les données de l'interface à rafraichir
 * Données renvoyées: JSONObject de JSONArrray(list_port, list_proprio, list_longueur, list_modele)
 * Paramètres : aucun
 * URL bateau existant: http://localhost/C1_PORTAIL/ajax_treatments/refreshData.php
 */
header("Content-type: application/json");

    // On construit la requête http vers le backoffice pour récupérer l'état de l'ajout du bateau
    $authToken = "test";
    $url = 'http://localhost:8080/query_refresh.html';
    // Setup cURL
    $ch = curl_init($url);
    curl_setopt_array($ch, array(
        CURLOPT_POST => TRUE,
        CURLOPT_RETURNTRANSFER => TRUE,
        CURLOPT_HTTPHEADER => array(
            'Authorization: ' . $authToken,
            'Content-Type: application/json'
        )
    ));

    // Send the request
    $response = trim(curl_exec($ch)); // on trim car espace
    $json = json_decode($response, true); // on met true pour convertir le résultat en tableau associatif

    // Gestion des erreurs de connection
    if (curl_error($ch)) {
        $error_msg = curl_error($ch);
        //echo("<br/><br/>> Erreur lors de l'envoie de la requête addBoatAtLocation(): <br/>&emsp;<b>".$error_msg." at /query_add_boat.html</b>");
    }
    /*else {
        //echo("<br/><br/>> Requête addBoatAtLocation() sended = ".$_GET['nomport'].'&emsp;'.$_GET['nombateau'].'&emsp;'.$_GET['longueur'].'&emsp;'.$_GET['nomemplacement']);
    }*/

    // Si la réponse contient des données on l'écho
    if(!empty($response) && isset($response)) {
        // on stocke le nom et le résultat de la requête dans les cookies
        $json = json_decode($response, true); // on met true pour convertir le résultat en tableau associatif
        echo trim(json_encode($json));
    }
    else {
        echo 'Erreur lors de la requête';
        die(curl_error($ch));
    }
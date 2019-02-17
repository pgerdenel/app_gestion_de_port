<?php
/**
 * Permet de supprimer un bateau à un emplacement particulier
 * Données renvoyées: true or false
 * Paramètres : nom_port, nom_emplacement, longueur, $login
 * URL: http://localhost/C1_PORTAIL/ajax_treatments/removeBoatAtLocation.php?nomport=port1&longueur=-6&nomemplacement=1
 * */

header("Content-type: application/json");

// On récupère les paramètre de la requête GET
// On récupère les paramètre de la requête GET
if(isset($_POST['nomport']) && isset($_POST['nomemplacement']) && isset($_POST['longueur']) && isset($_POST['nomuser']) &&
    !empty($_POST['nomport']) && !empty($_POST['nomemplacement']) && !empty($_POST['longueur'])&& !empty($_POST['nomuser']) ) {
    /* TEST POUR AJAX FONCTIONNEL
    $json_array_string  = "{\"result\":\"true\"}";
    echo $json_array_string;*/

    // On construit la requête http vers le backoffice pour récupérer l'état de la suppression du bateau
    $authToken = "test";
    $parameter = array("nom_port" => $_POST['nomport'], "longueur" => $_POST['longueur'], "num_place" => $_POST['nomemplacement'], "nom_user" => $_POST['nomuser']);
    // post request url
    $url = 'http://localhost:8080/query_remove_boat.html';
    // Setup cURL
    $ch = curl_init($url);
    curl_setopt_array($ch, array(
        CURLOPT_POST => TRUE,
        CURLOPT_RETURNTRANSFER => TRUE,
        CURLOPT_HTTPHEADER => array(
            'Authorization: ' . $authToken,
            'Content-Type: application/json'
        ),
        CURLOPT_POSTFIELDS => json_encode($parameter)
    ));

    // Send the request
    $response = trim(curl_exec($ch)); // on trim car espace
    $json = json_decode($response, true); // on met true pour convertir le résultat en tableau associatif
    // Gestion des erreurs de connection
    if (curl_error($ch)) {
        $error_msg = curl_error($ch);
        //echo("<br/><br/>> Erreur lors de l'envoie de la requête removeBoatAtLocation(): <br/>&emsp;<b>".$error_msg." at /query_remove_boat.html</b>");
    }
    /*else {
        //echo("<br/><br/>> Requête removeBoatAtLocation() sended = ".$_POST['nomport'].'&emsp;'.$_POST['nombateau'].'&emsp;'.$_POST['longueur'].'&emsp;'.$_POST['nomemplacement']);
    }*/

    // Si la réponse contient des données on l'écho
    if($json['result']===true) {
        // on notifie par cette variable de session que l'historique doit être rafraichit
        $_SESSION['hist_change'] = 0;
        echo "true";
    }
    else if($json['result']===false) {
        // on notifie par cette variable de session que l'historique doit être rafraichit
        $_SESSION['hist_change'] = 0;
        echo "false";
    }
    else {
        echo 'Erreur lors de la requête';
        die(curl_error($ch));
    }
}
else {
    echo '<p style="color:red">Erreur de paramètre, l\'emplacement et/ou la longueur n\'ont pas été renseigné correctement</p>';
}
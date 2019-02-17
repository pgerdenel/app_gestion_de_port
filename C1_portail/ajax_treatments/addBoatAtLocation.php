<?php
/**
 * Permet d'ajouter un bateau à un emplacement particulier
 * Données renvoyées: true or false
 * Paramètres : nom_port, nom_bateau, nom_emplacement, longueur, nom_user, gard
 * URL bateau existant: http://localhost/C1_PORTAIL/ajax_treatments/addBoatAtLocation.php?nomport=port1&nombateau=test&longueur=-6&nomemplacement=1&modele=voilier&nomuser=user2&gard=true
 * URL bateau non existant: http://localhost/C1_PORTAIL/ajax_treatments/addBoatAtLocation.php?nomport=port1&nombateau=test&longueur=-6&nomemplacement=1&modele=voilier&nomuser=user2&gard=true
 */
header("Content-type: application/json");

// On récupère les paramètre de la requête GET
if(isset($_POST['nomport']) && isset($_POST['nombateau']) && isset($_POST['nomemplacement']) && isset($_POST['longueur']) && isset($_POST['nomuser']) && isset($_POST['modele']) && isset($_POST['gard']) &&
!empty($_POST['nomport']) && !empty($_POST['nombateau']) && !empty($_POST['nomemplacement']) && !empty($_POST['longueur']) && !empty($_POST['nomuser']) && !empty($_POST['modele'])) {
    /* TEST POUR AJAX FONCTIONNEL
    $json_array_string  = "{\"result\":\"true\"}";
    echo $json_array_string;*/

    // On construit la requête http vers le backoffice pour récupérer l'état de l'ajout du bateau
    $authToken = "test";
    $parameter = array("nom_port" => $_POST['nomport'], "nom_bateau" => $_POST['nombateau'], "longueur" => $_POST['longueur'], "num_place" => $_POST['nomemplacement'], "nom_user" => $_POST['nomuser'], "modele" => $_POST['modele'], "gard"=>$_POST['gard']);
    // post request url
    $url = 'http://localhost:8080/query_add_boat.html';
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
        //echo("<br/><br/>> Erreur lors de l'envoie de la requête addBoatAtLocation(): <br/>&emsp;<b>".$error_msg." at /query_add_boat.html</b>");
    }
    /*else {
        //echo("<br/><br/>> Requête addBoatAtLocation() sended = ".$_POST['nomport'].'&emsp;'.$_POST['nombateau'].'&emsp;'.$_POST['longueur'].'&emsp;'.$_POST['nomemplacement']);
    }*/

    // Si la réponse contient des données on l'écho
    if($json['result'] === "true") {
        // on notifie par cette variable de session que l'historique doit être rafraichit
        $_SESSION['hist_change'] = 0;
        echo "true";
    }
    else if($json['result'] === "false") {
        // on notifie par cette variable de session que l'historique doit être rafraichit
        $_SESSION['hist_change'] = 0;
        echo "false";
    }
    else {
        echo 'Erreur lors de la requête';
        //echo json_encode($json);
        echo $response;
        die(curl_error($ch));
    }
}
else {
    echo '<p style="color:red">Erreur de paramètre, le nom du bateau et/ou l\'emplacement et/ou la longueur et/ou le modèle n\'ont pas été renseigné correctement</p>';

}
<?php
/**
 * Vérifie si un bateau est bien dans un emplacement d'un certain port
 * Données renvoyées: true or false
 * Paramètres obligatoires : nomport, nomemplacement, nombateau
 * Paramètres facultatifs : longueur
 * URL: http://localhost/C1_PORTAIL/ajax_treatments/checkBoatIn.php?nomport=port1&nombateau=unknow&nomemplacement=1
 * URL: http://localhost/C1_PORTAIL/ajax_treatments/checkBoatIn.php?nomport=port1&nombateau=unknow&nomemplacement=1&longueur=-6
 **/

header("Content-type: application/json");

// On récupère les paramètre de la requête GET
if(isset($_POST['nomport']) && isset($_POST['nombateau']) && isset($_POST['nomemplacement'])
    && !empty($_POST['nomport']) && !empty($_POST['nombateau']) && !empty($_POST['nomemplacement'])) {
    // On construit la requête http vers le backoffice pour récupérer le prix des places en fonction de la longueur
    $authToken = "test";
    $parameter = array("nom_port"=>$_POST['nomport'], "nom_bateau"=>$_POST['nombateau'], "num_place"=>$_POST['nomemplacement']);
    // post request url
    $url = 'http://localhost:8080/query_check_boat_in.html';
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
        //echo("<br/><br/>> Erreur lors de l'envoie de la requête checkBoatIn(): <br/>&emsp;<b>".$error_msg." at /query_check_boat_in.html</b>");
    }
    /*else {
        //echo("<br/><br/>> Requête checkBoatIn() sended = ".$_POST['nomport'].'&emsp;'.$_POST['nombateau'].'&emsp;'.$_POST['nomemplacement']);
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
    echo '<p style="color:red">Erreur de paramètre, le nom du bateau et/ou l\'emplacement n\'ont pas été renseigné correctement</p>';
}
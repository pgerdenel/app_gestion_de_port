<?php
/**
 * Permet de tranférer un bateau dans un autre port
 * Données renvoyées: true or false
 * Paramètres : nom_port, nom_bateau, num_place, portdest, longueur
 * URL: http://localhost/C1_PORTAIL/ajax_treatments/transferBoatAtPort.php?nomport=port1&nombateau=test&nomemplacement=1&nomportdest=port1&longueur=-6
 * Stockage temporaire(30sec): stockage du résultat de la requête stocké dans un objet historique json_encodé dans $_COOKIE
 */

header("Content-type: application/json");

$isExist = false;

// On récupère les paramètre de la requête GET
if(!$isExist && isset($_POST['nomport']) && isset($_POST['nombateau']) && isset($_POST['nomemplacement']) && isset($_POST['nomportdest']) && isset($_POST['longueur']) &&
    !empty($_POST['nomport']) && !empty($_POST['nombateau']) && !empty($_POST['nomemplacement']) && !empty($_POST['nomportdest']) && !empty($_POST['longueur'])) {
    /* TEST POUR AJAX FONCTIONNEL
    $json_array_string = "{\"result\":\"true\"}";
    $result = json_decode($json_array_string, true); // on met true pour convertir le résultat en tableau associatif
    echo $json_array_string;*/

    if($_POST['nomport'].trim(" ") != $_POST['nomportdest'].trim(" ")) {
        // On construit la requête http vers le backoffice pour récupérer le prix des places en fonction de la longueur
        $authToken = "test";
        $parameter = array("nom_port" => $_POST['nomport'], "nom_bateau" => $_POST['nombateau'], "num_place" => $_POST['nomemplacement'], "nom_portdest" => $_POST['nomportdest'], "longueur" => $_POST['longueur']);
        // post request url
        $url = 'http://localhost:8080/query_transfer_boat.html';
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
            //echo("<br/><br/>> Erreur lors de l'envoie de la requête transferBoatAtPort(): <br/>&emsp;<b>".$error_msg." at /query_transfer_boat.html</b>");
        }
        /*else {
            //echo("<br/><br/>> Requête transferBoatAtPort() sended = ".$_POST['nomport'].'&emsp;'.$_POST['nombateau'].'&emsp;'.$_POST['nomemplacement']);
        }*/

        // Si la réponse contient des données on l'écho
        if ($json['result'] === true) {
            // on notifie par cette variable de session que l'historique doit être rafraichit
            $_SESSION['hist_change'] = 0;
            echo "true";
        } else if ($json['result'] === false) {
            // on notifie par cette variable de session que l'historique doit être rafraichit
            $_SESSION['hist_change'] = 0;
            echo "false";
        } else {
            echo 'Erreur lors de la requête';
            die(curl_error($ch));
        }
    }
    else {
        echo '<p style="color:red">Erreur le port source et le port de destination est le même</p>';
    }
}
else {
    echo '<p style="color:red">Erreur de paramètre, le nom du bateau et/ou l\'emplacement et/ou le port de destination n\'ont pas été renseigné correctement</p>';
}
<?php
/**
 * Permet de récupérer les informations d'un bateau selon son nom et son emplacement
 * Données renvoyées: longueur, prix, num_place
 * Paramètres : nom_port, nom_emplacement, nom_user
 * URL with data : http://localhost/C1_PORTAIL/ajax_treatments/getBoatInfosAdmin.php?nomport=port1&nombateau=unknow&nomemplacement=1
 * URL without data: http://localhost/C1_PORTAIL/ajax_treatments/getBoatInfosAdmin.php?nomport=port1&nombateau=test&nomemplacement=1
 * */

// On démarre la session
session_start();


if(isset($_COOKIE['user'])) {
    // on récupère le login de l'user
    $tab = unserialize($_COOKIE['user']);
    $login = $tab['login'];

    // on récupère le type de l'user
    $data2 = unserialize($_SESSION[$login]);
    $type = $data2['type'];
}
else {
    // On redirige vers l'espace de service en passant le type d'utilisateur
    header('Location: http://localhost:80/C1_portail/index.php');
}
// On récupère les paramètre de la requête GET
if(isset($_POST['nomport']) && isset($_POST['nombateau']) && isset($_POST['nomemplacement']) && isset($_POST['nomuser']) && isset($type) &&
!empty($_POST['nomport']) && !empty($_POST['nombateau']) && !empty($_POST['nomemplacement']) && !empty($_POST['nomuser']) && !empty($type) ) {

    // On construit la requête http vers le backoffice pour récupérer le prix des places en fonction de la longueur
    $authToken = "test";
    $parameter = array("nom_port"=>$_POST['nomport'], "nom_bateau"=>$_POST['nombateau'], "nom_user"=>$_POST['nomuser'], "type_user"=>$type, "num_place"=>$_POST['nomemplacement']);
    // post request url
    $url = 'http://localhost:8080/query_boat_infos.html';
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

    // Gestion des erreurs de connection
    if (curl_error($ch)) {
        $error_msg = curl_error($ch);
        //echo("<br/><br/>> Erreur lors de l'envoie de la requête getBoatInfos(): <br/>&emsp;<b>".$error_msg." at /query_boat_infos.html</b>");
    }
    /*else {
        //echo("<br/><br/>> Requête getBoatInfos() sended = ".$_POST['nomport'].'&emsp;'.$_POST['nombateau'].'&emsp;'.$_POST['nomemplacement']);
    }*/

    // Si la réponse contient des données on l'écho
    if(!empty($response) && isset($response)) {
        // on notifie par cette variable de session que l'historique doit être rafraichit
        $_SESSION['hist_change'] = 0;

        // on stocke le nom et le résultat de la requête dans les cookies
        $json = json_decode($response, true); // on met true pour convertir le résultat en tableau associatif
        echo trim(json_encode($json));
    }
    else {
        echo 'Erreur lors de la requête';
        die(curl_error($ch));
    }
}
else {
    echo '<p style="color:red">Erreur de paramètre, le nom du bateau et/ou l\'emplacement n\'ont pas été renseigné correctement</p>';
}
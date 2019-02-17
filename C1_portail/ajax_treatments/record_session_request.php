<?php
/**
 * Enregistre les résultats de certains requêtes effectuées dans la session
 * http://localhost/C1_PORTAIL/ajax_treatments/record_cookie_hist.php
 */
// On démarre la session
session_start();

if(isset($_COOKIE['user'])) {
    // on récupère le login de l'user
    $tab = unserialize($_COOKIE['user']);
    $login = $tab['login'];
}
else {
    // On redirige vers l'espace de service en passant le type d'utilisateur
    header('Location: http://localhost:80/C1_portail/index.php');
}

if(isset($_SESSION[$login]) && isset($_GET['key']) && isset($_GET['value'])) {

    switch($_GET['key']) {
        case "place_price":
            // on vérifie que le nom_port est renseigné dans la requête
            if(isset($_GET['nom_port'])) {
                // on enregistre le résultat de la requête
                $_SESSION['place_price'] = $_GET['value']; //résultat stocké
                $_SESSION['place_price'][$_GET['nom_port']] = $_GET['nom_port']; // nom du port stocké
            }
            break;

        case "boat_infos":
            // si la variable de session de ce résultat existe et que sa dernière activité bonne
            if(isset($_SESSION[$_GET['key']]) && (time()-$_SESSION['recent_activity'] > 600)) {
                // on enregistre le résultat de la requête
                $_SESSION['place_price'] = $_GET['value'];
            }
            else {
                // sinon on crée et enregistre le résultat de la requête
                $_SESSION[$_GET['key']] = $_GET['value'];
                // et on met le temps actuelle comme plus temps plus récent
                $_SESSION[$_GET['key']]['most_recent_activity'] = time();
            }
            break;

        case "transfer":
            // si la variable de session de ce résultat existe et que sa dernière activité bonne
            if(isset($_SESSION[$_GET['key']]) && (time()-$_SESSION['recent_activity'] > 600)) {
                // on enregistre le résultat de la requête
                $_SESSION['place_price'] = $_GET['value'];
            }
            else {
                // sinon on crée et enregistre le résultat de la requête
                $_SESSION[$_GET['key']] = $_GET['value'];
                // et on met le temps actuelle comme plus temps plus récent
                $_SESSION[$_GET['key']]['most_recent_activity'] = time();
            }
            break;
    }
}
else {
    echo ' - la sesssion de l\'utilisateur a expiré ou les paramètres ne sont pas correctes<br/>';
}

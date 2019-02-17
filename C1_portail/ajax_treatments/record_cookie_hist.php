<?php
/**
 * Enregistre les requêtes effectuées dans l'historique
 * http://localhost/C1_PORTAIL/ajax_treatments/record_cookie_hist.php
 */

require_once('../objects/Historic.php');

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

if(isset($_COOKIE['user_hist']) && isset($_GET['key'])) {
    echo '<script src="../my_function.js"></script>';
    switch($_GET['key']) {

        /* noms de requête où le résultat est stockée en session */
        case "place_price":
            // si aucun cookie du nom de clé n'est défini
            /*if(!isset($_COOKIE[$_GET['key']])) {
                // on le crée et on met sa valeur à true afin que lors d'une requête le php puisse vérifier si sa valeur pour savoir s'il a enregistré le résultat de la requête ou non
                setcookie($_GET['key'], "true", time() + (3600), '/'); // 1h
            }*/
            // on récupère l'historique de l'utilisateur stocké dans les cookies
            $hist = unserialize($_COOKIE['user_hist']);
            // on met également la requête dans l'historique
            $hist->pushRequest($_GET['key']);
            array_merge($hist->getTabRequest(), $_GET['key']);
            $hist_s = serialize($hist);
            setcookie('user_hist', $hist_s, time() + (3600), '/'); // 1h
            // on notifie par cette variable de session que l'historique doit être rafraichit
            $_SESSION['hist_change'] = 0;
            break;

        case "boat_infos":
            // si aucun cookie du nom de clé n'est défini
            /*if(!isset($_COOKIE[$_GET['key']])) {
                // on le crée et on met sa valeur à true afin que lors d'une requête le php puisse vérifier si sa valeur pour savoir s'il a enregistré le résultat de la requête ou non
                setcookie($_GET['key'], "true", time() + (20), '/'); // 20sec
            }*/
            // on récupère l'historique de l'utilisateur stocké dans les cookies
            $hist = unserialize($_COOKIE['user_hist']);
            // on met également la requête dans l'historique
            $hist->pushRequest($_GET['key']);
            array_merge($hist->getTabRequest(), $_GET['key']);
            $hist_s = serialize($hist);
            setcookie('user_hist', $hist_s, time() + (3600), '/'); // 1h
            // on notifie par cette variable de session que l'historique doit être rafraichit
            $_SESSION['hist_change'] = 0;
            break;

        case "transfer":
            // si aucun cookie du nom de clé n'est défini
            /*if(!isset($_COOKIE[$_GET['key']])) {
                // on le crée et on met sa valeur à true afin que lors d'une requête le php puisse vérifier si sa valeur pour savoir s'il a enregistré le résultat de la requête ou non
                setcookie($_GET['key'], "true", time() + (20), '/'); // 20sec
            }*/
            // on récupère l'historique de l'utilisateur stocké dans les cookies
            $hist = unserialize($_COOKIE['user_hist']);
            // on met également la requête dans l'historique
            $hist->pushRequest($_GET['key']);
            array_merge($hist->getTabRequest(), $_GET['key']);
            $hist_s = serialize($hist);
            setcookie('user_hist', $hist_s, time() + (3600), '/'); // 1h
            // on notifie par cette variable de session que l'historique doit être rafraichit
            $_SESSION['hist_change'] = 0;
            break;

        /* noms de requête où le résultat n'est pas stockée en session */

        case "free_place":
            // on récupère l'historique de l'utilisateur stocké dans les cookies
            $hist = unserialize($_COOKIE['user_hist']);
            // on met également la requête dans l'historique
            $hist->pushRequest($_GET['key']);
            array_merge($hist->getTabRequest(), $_GET['key']);
            $hist_s = serialize($hist);
            setcookie('user_hist', $hist_s, time() + (3600), '/'); // 1h
            // on notifie par cette variable de session que l'historique doit être rafraichit
            $_SESSION['hist_change'] = 0;
            break;

        case "check_boat_in":
            // on récupère l'historique de l'utilisateur stocké dans les cookies
            $hist = unserialize($_COOKIE['user_hist']);
            // on met également la requête dans l'historique
            $hist->pushRequest($_GET['key']);
            $hist_s = serialize($hist);
            setcookie('user_hist', $hist_s, time() + (3600), '/'); // 1h
            // on notifie par cette variable de session que l'historique doit être rafraichit
            $_SESSION['hist_change'] = 0;
            break;

        case "add_boat":
            // on récupère l'historique de l'utilisateur stocké dans les cookies
            $hist = unserialize($_COOKIE['user_hist']);
            // on met également la requête dans l'historique
            $hist->pushRequest($_GET['key']);
            $hist_s = serialize($hist);
            setcookie('user_hist', $hist_s, time() + (3600), '/'); // 1h
            // on notifie par cette variable de session que l'historique doit être rafraichit
            $_SESSION['hist_change'] = 0;
            break;

        case "rm_boat":
            // on récupère l'historique de l'utilisateur stocké dans les cookies
            $hist = unserialize($_COOKIE['user_hist']);
            // on met également la requête dans l'historique
            $hist->pushRequest($_GET['key']);
            $hist_s = serialize($hist);
            setcookie('user_hist', $hist_s, time() + (3600), '/'); // 1h
            // on notifie par cette variable de session que l'historique doit être rafraichit
            $_SESSION['hist_change'] = 0;
            break;
    }

    //echo $hist->displayHistoric();
}
else {
    echo ' - cookie not exist or no parameter<br/>';
    echo ' - value of cookie is: <br/><br/>';
    $hist = unserialize($_COOKIE['user_hist']);
    echo $hist->displayFullHistoric();

}

<?php
/**
 * Gère les requêtes pour mettre à jour l'historique
 *
 */
session_start();
require_once '../objects/Historic.php';

if(isset($_GET['t']) && !empty($_GET['t'])) {

    switch($_GET['t']) {

        case "init":
            // creer l'objet historique
            $hist = new Historic();
            $hist_s = serialize($hist);
            setcookie('user_hist', $hist_s, time()+3600, '/');
            $_SESSION['hist_change'] = -1;
            echo $hist->displayHistoric();
            break;

        case "refresh":
            if(isset($_SESSION['hist_change']) && $_SESSION['hist_change'] == 0) {
                $hist = unserialize($_COOKIE['user_hist']);
                //$_SESSION['hist_change'] = -1;
                echo $hist->displayHistoric();
            }
            break;

        case "clear":
            setcookie('user_hist', null, time()-3600);
            unset($_COOKIE['user_hist']);
            unset($_SESSION['hist_change']);
            echo "historique supprimé";
            break;
    }
}
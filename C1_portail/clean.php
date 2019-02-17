<?php
/**
 * Page PHP permettant de nettoyer correctement les informations
 */

session_start();

if(isset($_POST['disconnect']))
{
    disconnect();
}

function disconnect() {

    // destruction session
    $data = unserialize($_COOKIE['user']);
    $login = $data['login'];
    unset($_SESSION[$login]);
    unset($_SESSION);
    session_destroy();

    // destruction cookie 'user'
    setcookie('user', null, time()-3600);
    setcookie('user', false);
    unset($_COOKIE['user']);
    // destruction cookie 'user_hist'
    setcookie('user_hist', null, time()-3600);
    setcookie('user_hist', false);
    unset($_COOKIE['user_hist']);
    unset($_COOKIE);
    unset($_POST['disconnect']);

    echo '* cookie supprimé<br/>';
    echo '* session supprimée<br/>';
    echo 'vous êtes maintenant déconnecté<br/>';
    echo 'redirection vers le formulaire d\'authentification ... ';

    // Redirection en 4sec au formulaire de connexion
    header("refresh:4;url=index.php");
}
<?php
/**
 *
 */
// On démarre la session
session_start();
require_once 'objects/User.php';
require_once 'socket_certificate.php';

if(isset($_POST["login"]) && isset($_POST["pass"]) && !empty($_POST['login']) && !empty($_POST['pass'])) { // si données POST définie et non vide

    echo '> Données post correctes <br/>';

    // On crée un cookie contenant les identifiants entrés par l'utilisateur
    if(!isset($_COOKIE['user'])) {
        echo '<br/>> cookie non existant: <br/>';
        $tab = array("login"=>trim($_POST['login']), "pass"=>trim($_POST['pass']));
        setcookie('user', serialize($tab), time()+3660, '/');
        echo ' cookie created  &emsp;';
    }
    else {
        echo '<br/>> cookie existant: ';
        $tab = array("login"=>trim($_POST['login']), "pass"=>trim($_POST['pass']));
        setcookie('user', serialize($tab), time()+3660, '/');
        echo ' cookie updated  &emsp;';
        // print_r($_COOKIE['user']);
    }

    $login = $_POST['login'];
    $pass = $_POST['pass'];
    /*echo 'login: '.$_POST['login'].'<br/>';
    echo 'pass: '.$_POST['pass'].'<br/>';*/

    /* Création d'une requête http : envoie des identifiants
     * Destinataire : BackOffice
     * Destinataire Handler: Query_LoginHandler
     */
    // array of post field
    $user = \portail\User::construct_Login_And_Pass($login, $pass);
    echo '<br/><br/>> Verification des identifiants: ( '.$user->getLogin().', '.$user->getPass().' ) en cours ...';
    $user_json = $user->to_json();
    echo("<br/><br/>> Tentative d'envoie de l'user au format JSON ...");

    $parameter = array("test" => "test", "data" => "data");
    $lenght = sizeof($parameter);
    echo "<script>console.log(".$lenght.");</script>";
    $json_parameter = json_encode($parameter);

    // on récupère une clé AES
    //$key = exchangeAESKey();
    // on chiffre $json_parameter avec AES
    //$encrypted = AESEncrypt($json_parameter, $key);

    // on chiffre $key avec la clé publique du backoffice
    //openssl_public_encrypt($key, $encrypted, openssl_pkey_get_public(load_public_key_back_office()));

    // post request url
    $url = 'http://localhost:8080/query_login.html';
    // Setup cURL
    $ch = curl_init($url);
    curl_setopt_array($ch, array(
        CURLOPT_POST => TRUE,
        CURLOPT_RETURNTRANSFER => TRUE,
        CURLOPT_HTTPHEADER => array(
            'Content-Type: application/json' //'Content-Type: application/octet-stream'
        ),
        CURLOPT_POSTFIELDS => $user_json
    ));

    // Send request and retrieve response
    $response = trim(curl_exec($ch)); // on trim car espace
    // On décode la réponse json que l'on convertit en tableau associatif
    $json = json_decode($response, true); // on met true pour convertir le résultat en tableau associatif
    echo '<br/>'.$json['result'].'<br/>';
    echo '<br/>'.$json['type'].'<br/>';
    // Gestion des erreurs de connection
    if (curl_error($ch)) {
        $error_msg = curl_error($ch);
        echo("<br/><br/>> Erreur lors de l'envoie de l'user: <br/>&emsp;<b>".$error_msg." at /query_login.html</b>");
    }
    else {
        echo("<br/><br/>> User au format JSON envoyé = ".$user_json);
    }
    // Si la réponse est ok et donc que l'utilisateur est authentifié, on affiche l'interface_user.php
    if($json['result'] == "success") {
        echo '<br/><br/>> Utilisateur authentifié '.$response;
        $type=$json['type'];
        // on crée une session et on stocke l'utilisateur dans la session
        $user_session = \portail\User::construct_Session($login, $type);
        $myUser_s = serialize($user_session);
        $data_session = array("login"=>trim($_POST['login']), "type"=>$type);
        $_SESSION[$login] = serialize($data_session);
        // echo '<br/>session['.$login.']<br/>';
        print_r('<br/>'.$_SESSION[$login]);

        echo '<br/><br/>> Session started';

        // On redirige vers l'espace de service en passant le type d'utilisateur
        header('Location: http://localhost:80/C1_portail/interface_user.php');
    }
    else if($response == "fail") {
        echo '<br/><br/>> Utilisateur non authentifié '.$response;
        echo '&emsp;</tab><a href="http://localhost:80/C1_portail/index.php">retour login</a>';
        // On redirige vers l'espace de service en passant le type d'utilisateur
        header('Location:http://localhost:80/C1_portail/index.php');
        die(curl_error($ch));
    }
    else {
        echo 'Erreur lors de la requête';
        header('Location: http://localhost:80/C1_portail/index.php');
    }
}
else {
    echo '> Données post incorrectes <';
    echo '<a href="http://localhost:80/C1_portail/index.php">retour login</a>';
}
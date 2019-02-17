<?php
/**
 * Permet de gérer l'ensemble des fonctionnalitées liées au certificat du portail
 * 1.  Generate a rsa pair public & private key
 * 2.  Exchange the public key between client
 * 2.1 Send the public key to server
 * 2.2 Receive the public key from the server
 * 3.  Receive the encrypted _symetric_key from the server & decrypt key with RSA
 * 4.  Receive the encrypted_certificat from the server & decrypt it with symetric key
 */

require_once('./objects/MyCertificate.php');

/**
 * Demande son certiticat à l'autorité de certification
 * @return bool
 */
function ask_certificate() {
// 1. Generate a rsa pair public & private key
    echo '<br/>1. Generate a rsa pair public & private key and store it<br/>';
    $config = array(
        "private_key_bits" => 2048,
        "private_key_type" => OPENSSL_KEYTYPE_RSA
    );
    // Create the private and public key
    $res = openssl_pkey_new($config);
    // Extract the private key from $res to $privKey
    openssl_pkey_export($res, $privKey);
    // on sauvegarde la clé privée dans un fichier
    file_put_contents('resources/private_key.pem', $privKey);
    echo '<br/><li>file created like "resources/private_key.pem"</li>';
    // Extract the public key from $res to $pubKey
    $pubKey = openssl_pkey_get_details($res);
    $pubKey = $pubKey["key"];
    // on sauvegarde la clé public dans un fichier
    file_put_contents('resources/public_key.pem', $pubKey);
    echo '<li>file created like "resources/public_key.pem"</li>';

// 2. send message_ask_certificat
    $adresse = '127.0.0.1';
    $port_join_first = 2025;
    $port_listen = 2028;
    /**
     * AF_INET : Protocole basé sur IPv4. TCP et UDP
     * SOCK_DGRAM : Support des datagrammes (UDP)
     * SOL_UDP : protocole
     */
    echo '<br/>2. send message_ask_certificat<br/>';
    echo '<br/><li>Envoie du Message_Ask_cert ...</li>';
    $sock = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP);
    $msg_ask_cert = array("nom_entite"=>"portail", "port_number" => $port_listen);
    $msg_ask_cert_json = json_encode($msg_ask_cert);
    $len = strlen($msg_ask_cert_json);
    socket_sendto($sock, $msg_ask_cert_json, $len, 0, $adresse, $port_join_first);
    socket_close($sock);
    echo '<li>Message_Ask_cert envoyé | size='.$len.'</li>';

    sleep(8); // on temporise

// 3 Send the public key to server
    echo '<br/>3 Send the public key to server<br/>';
    echo '<br/><li>Envoie de la clé publique ...</li>';
    $sock = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP);
    $public_key = load_public_key();
    $len = strlen($public_key);
    socket_sendto($sock, $public_key, $len, 0, $adresse, $port_listen);
    socket_close($sock);
    echo '<li>Publickey envoyée| size='.$len.'</li>';

    sleep(4); // on temporise pour que le serveur java ait le temps de fermer sa socket

// 4 Receive the public key from the server
    $len=512; // 450 taille de la clé privé
    echo '<br/>4 Receive the public key from the server<br/>';
    echo '<br/><li>Reception de la clé publique du serveur ...</li>';
    $sock = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP);
    socket_bind($sock, $adresse, $port_listen);
    //Receive some data
    socket_recv($sock, $buf, $len, 0);/**/
    socket_close($sock);
    echo '<li>Publickey received</li><br/>';
    print_r($buf);
    // on enregistre la clé publique du serveur
    save_public_key_serveur($buf);

    sleep(2); // on temporise pour que le serveur java ait le temps de fermer sa socket

// 5  Receive the encrypted _symetric_key from the server & decrypt key with private_key
    $len=256; // 256 taille de la clé symétrique
    echo '<br/><br/>5  Receive the encrypted _symetric_key & decrypt key with private_key<br/>';
    echo '<br/><li>Reception de la clé symétrique chiffrée ...</li>';
    $sock = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP);
    socket_bind($sock, $adresse, $port_listen);
    //Receive some data
    socket_recv($sock, $buf, $len, 0);
    socket_close($sock);
    echo '<li>Symetric key encrypted received</li><br/>';
    print_r($buf);
    // on déchiffre la clé symétrique avec la clé privé
    openssl_private_decrypt($buf, $decrypted, $privKey);
    echo '<br/><br/><li>Symetric key decrypted received</li>';
    $AES_symetric_key = $decrypted; // on stocke cette clé dans une variable nommé
    print_r($AES_symetric_key);
// 6  Receive the encrypted_certificat from the server & decrypt it with symetric key
    $len=2048; // 2048 taille du certificat
    echo '<br/><br/>6  Receive the encrypted_certificat from the server & decrypt it with symetric key<br/>';
    echo '<br/><li>Reception du certificat chiffrée ...</li>';
    $sock = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP);
    socket_bind($sock, $adresse, $port_listen);
    //Receive some data
    socket_recv($sock, $buf, $len, 0);
    socket_close($sock);
    echo '<li>certificate encrypted received</li><br/>';
    print_r($buf);
    // on déchiffre le certificat avec la clé symétrique
    $certificate_json_decrypted = AESDecrypt($buf, $AES_symetric_key);
    echo '<br/><br/><li>certificate received decrypted & rebuild as object</li>';
    //echo "cert decrypt ".$certificate_json_decrypted.'<br/>';
    $cert = \C1_portail\MyCertificate::__construct_JSON(json_decode($certificate_json_decrypted, true));
    echo $cert->toString();
    // on stocke ce certificat dans un fichier cert_portail.json
    save_cert('resources/cert.json', $cert->toJson());
    return checkKeyAndCert();
}
/**
 * Permet d'échanger son certificat avec le backoffice
 */
function exchange_certificate(){
    // On construit la requête http vers le backoffice pour envoyer notre certificat et récupérer le sien

    // on passe le certificat en paramètre
    $parameter = load_cert('resources/cert.json');

    // post request url
    $url = 'http://localhost:8080/exchange_certificate.html';
    // Setup cURL
    $ch = curl_init($url);
    curl_setopt_array($ch, array(
        CURLOPT_POST => TRUE,
        CURLOPT_RETURNTRANSFER => TRUE,
        CURLOPT_HTTPHEADER => array(
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

    // Si la réponse contient des données on l'écho
    if(isset($json) && !empty($json)) {
        $cert = \C1_portail\MyCertificate::__construct_JSON($json);
        save_cert('resources/cert_back_office.json', $cert->toJson());
        echo '<br/>reception du certiticat Success';
        echo '<br/>exchange du certificat avec le backoffice Success';
    }
    else {
        echo 'Erreur lors de la requête'.$error_msg;
        //echo json_encode($json);
        echo $response;
        die(curl_error($ch));
    }
}
/**
 * Permet de rediriger vers l'index.php une fois as_certificat terminer
 */
function finiDeLire() {
    echo '<script type="text/javascript">setTimeout(function() {alert("Fini de lire ???");window.location.href = "index.php";}, 20000);</script>';
}
/**
 * Verifie si la clé publique et privé et le certificat et la clé publique du serveur existent
 * @return bool
 */
function checkKeyAndCert() {
    return (
        file_exists('resources/public_key.pem') &&
        file_exists('resources/private_key.pem') &&
        file_exists('resources/cert.json') &&
        file_exists('resources/public_key_authority.pem')
    );
}
/**
 * Verifie si la clé publique et privé et le certificat et la clé publique du serveur existent
 * @return bool
 */
function checkKeyAndCertAll() {
    return (
        file_exists('resources/public_key.pem') &&
        file_exists('resources/private_key.pem') &&
        file_exists('resources/cert.json') &&
        file_exists('resources/public_key_authority.pem') &&
        file_exists('resources/cert_back_office.json')
    );
}
/**
 * Sauvegarde la public_key en String
 * @return bool|string
 */
function load_public_key() {
    // public key as string
    return file_get_contents('resources/public_key.pem');
}
/**
 * Sauvegarde la public_key en String
 * @return bool|string
 */
function load_public_key_back_office() {
    // public key as string
    return file_get_contents('resources/public_key_back_office.pem');
}
/**
 * Sauvegarde la private_key en String
 * @return bool|string
 */
function load_private_key() {
    // Private key as string
    return file_get_contents('resources/private_key.pem');
}
/**
 * Sauvegarde la public_key en String
 * @param $pubkey : clé publique
 * @return bool|string
 */
function save_public_key($pubkey) {
    return file_put_contents('resources/public_key.pem', $pubkey);
}
/**
 * Sauvegarde la public_key du serveur en String
 * @param $pubkey : clé publique
 * @return bool|string
 */
function save_public_key_serveur($pubkey) {
    return file_put_contents('resources/public_key_authority.pem', $pubkey);
}
function save_pem_public_key_backoffice($pubkey) {
    file_put_contents('resources/public_key_back_office.pem', $pubkey);
}
/**
 * Sauvegarde la private_key en String
 * @param $privKey : clé privé
 * @return bool|string
 */
function save_private_key($privKey) {
    return file_put_contents('resources/private_key.pem', $privKey);
}
/**
 * sauvegarde le certificat en JSON
 * @param $path_file_name : chemin d'accès au certificat
 * @param $cert
 * @return bool|string
 */
function save_cert($path_file_name, $cert) {
    return file_put_contents($path_file_name, $cert); //
}
/**
 * Retourne le certificat dans le fichier au format json
 * @param $path_file_name
 * @return String $cert_json
 */
function load_cert($path_file_name) {
    return json_decode(file_get_contents($path_file_name),TRUE);
}
/**
 * Permet de chiffré des données avec AES
 * @param $data
 * @param $symetric_key
 * @return string
 */
function AESEncrypt($data, $symetric_key) {
    // Encrypt the data to $encrypted using the public key
    //openssl_public_encrypt($data, $encrypted, $pubKey);
    return openssl_encrypt($data, "AES-128-ECB", $symetric_key,  OPENSSL_RAW_DATA);
}
/**
 * Permet de déchiffré des données avec AES
 * @param $data
 * @param $symetric_key
 * @return string
 */
function AESDecrypt($data, $symetric_key) {
    // Decrypt the data using the private key and store the results in $decrypted
    //openssl_private_decrypt($encrypted, $decrypted, $privKey);
    // "AES128"
    // "aes128"
    // "AES-128-ECB"
    // "aes-128-ecb"
    // "AES256"
    // "aes256"
    // "AES-256-ECB"
    // "aes-256-ecb"
    /*$value = "debut";
    for($i=0;$i<sizeof(openssl_get_cipher_methods(true));$i++) {
        if (strpos(openssl_get_cipher_methods()[$i], 'aes') !== false || strpos(openssl_get_cipher_methods()[$i], 'AES') !== false) {
            $value.="algo: ".openssl_get_cipher_methods()[$i]." result= ".openssl_decrypt($data, openssl_get_cipher_methods()[$i], $symetric_key, OPENSSL_RAW_DATA).'<br/><br/>';
        }
        if($i==200) {
            break;
        }
     }
    return $value;*/
    return openssl_decrypt($data, "AES-128-ECB", $symetric_key, OPENSSL_RAW_DATA);

}
function ping_back_office() {
    ini_set('max_execution_time', -1);
    $socket = @fsockopen("localhost", 8080);
    return $socket;
}
function extractPublic_Key_Back_Office_From_Cert() {
    $json_arary = load_cert('resources/cert_back_office.json');
    $cert = \C1_portail\MyCertificate::__construct_JSON($json_arary);
    $pub_key_backoffice = $cert->getPublicKey();
    save_pem_public_key_backoffice($pub_key_backoffice);
    return $pub_key_backoffice;
}
/**
 * Permet d'envoyer une demande de clé AES au backoffice
 * @return String (renvoie la clé)
 */
function exchangeAESKey() {
    $key ="";
    // post request url
    $url = 'http://localhost:8080/exchange_AES_key.html';
    // Setup cURL
    $ch = curl_init($url);
    curl_setopt_array($ch, array(
        CURLOPT_POST => TRUE,
        CURLOPT_RETURNTRANSFER => TRUE
    ));

    // Send the request
    $response = trim(curl_exec($ch)); // on trim car espace

    // Gestion des erreurs de connection
    if (curl_error($ch)) {
        $error_msg = curl_error($ch);
        //echo("<br/><br/>> Erreur lors de l'envoie de la requête addBoatAtLocation(): <br/>&emsp;<b>".$error_msg." at /query_add_boat.html</b>");
    }
    /*else {
        //echo("<br/><br/>> Requête addBoatAtLocation() sended = ".$_POST['nomport'].'&emsp;'.$_POST['nombateau'].'&emsp;'.$_POST['longueur'].'&emsp;'.$_POST['nomemplacement']);
    }*/

    // Si la réponse contient des données on l'écho
    if(isset($response) && !empty($response)) {
        // on stocke la clé
        $key = $response;
    }
    else {
        echo 'Erreur lors de la requête'.$error_msg;
        //echo json_encode($json);
        echo $response;
        die(curl_error($ch));
    }
    return $key;
}
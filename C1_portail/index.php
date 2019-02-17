<?php
/**
 * Created by IntelliJ IDEA.
 * User: HcZ
 * Date: 07/12/2018
 * Time: 15:54
 */
$login = "";
$pass = "";

require_once('socket_certificate.php');
require_once('header.php');

$a = @fsockopen("localhost", 8080);

if(checkKeyAndCertAll() == 1) {
    // On vérifie si le cookie utilisateur existe
    if (isset($_COOKIE['user'])) {
        echo 'cookie existant<br/>';
        $tab = unserialize($_COOKIE['user']);
        $login = $tab['login'];
        $pass = $tab['pass'];

        /*echo 'cookie user';
        print_r($_COOKIE['user']);
        echo 'cookie historic';
        print_r($_COOKIE['user_hist']);*/
        /*echo '<br/>login= '.$tab['login'].'<br/>'.'pass= '.$tab['pass'].'<br/>';*/

    } else {
        echo 'cookie non existant<br/>';
    }


    echo '
<h1>Formulaire de connexion</h1>
<div id="connexion_container">
    <form id="form_connexion" action="login.php" method="post" >
    
        <input class="input_connexion" type="text" name="login" placeholder="login" required minlength="4" maxlength="20" size="20" value="' . $login . '"/><br/>
        <input class="input_connexion" type="password" name="pass" placeholder="pass" required minlength="4" maxlength="20" size="20" value="' . $pass . '"/><br/>
        
        <!--<label id="labell" class="labell" for="type-select">Choisissez un type d\'utilisateur: </label>
        <select id="type-select" type="text" name="type_user">
            <option value="proprio">Propriétaire</option>
            <option value="admin">Administrateur</option>
        </select><br/>-->
        
        <button>se connecter</button>
    </form>
</div>
';

}
else {
    if(!checkKeyAndCert()) {
        ask_certificate();
    }
    while (!ping_back_office()){
        sleep(2);
    };
    sleep(20);
    exchange_certificate();
}
require_once('footer.php');


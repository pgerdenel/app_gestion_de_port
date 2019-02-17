<?php
/**
 * Interface utilisateur pour les administrateurs de port et les propriétaires des bateaux
 * - affiche l'interface utilisateur en fonction du type récupéré du gestionnaire utilisateur
 * - effectue des requêtes ajax au backoffice pour les fonctionnalitées proposés
 * - listes de tous les paramètres utilisés en fonction des requêtes : nom_user, nom_port, nom_bateau, num_place, longueur_bateau; modele_bateau
 */
// On démarre la session
session_start();
require_once 'objects/User.php';

if (isset($_COOKIE['user'])) {
// On récupère l'user en session
// echo 'cookie data= '.$_COOKIE['user'];
    $tab = unserialize($_COOKIE['user']);
    $login = $tab['login'];
// echo '<br/>session data= '.$_SESSION[$login];
// echo '<br/>'.$_SESSION[$login];

// on récupère le type de l'user
    $data2 = unserialize($_SESSION[$login]);
    $type = $data2['type'];
}
else {
    // On redirige vers l'espace de service en passant le type d'utilisateur
    header('Location: http://localhost:80/C1_portail/index.php');
}
require_once 'header.php';


echo '
    <div id="espace_user">
    Bienvenue <b>'.$login.'</b><br/><br/>
            
';

// On affiche le bouton connexion/déconnexion en fonction de la session de l'utilisateur
if (isset($_COOKIE['user'])) {
    // On récupère le nom du login de l'user
    $tab = unserialize($_COOKIE['user']);
    $login = $tab['login'];

    // on récupère le type de l'user
    if(isset($_SESSION[$login])) {

        echo'
        <form method="post" action="clean.php">
            <input type="submit" value="se déconnecter" name="disconnect">
        </form>';
    }
}

echo '<script src="my_function.js"></script>';
echo'
    </div>
    <h1>Interface utilisateur</h1>
';

// On affiche l'interface correspondant au type user
if(!empty($type) && isset($type) && $type=="a") { // admin
    echo '
        <div id="abody_container">
        <p><u><b>Administration du port alias "'.$type.'"</b></u></p>
        <div id="afeatures_container">
        <div id="refresh">
        <button onclick="refresh()">clean & refresh</button>
        <p id="refresh_mess"></p>
        </div>
        <div id="port_container">
        <label>Selectionner un port: </label> 
        <select id="list_port_main">
        </select>   
        </div>   
        <div class="feature">
        <!--Fonctionnalitée-->
        <label> Récupérer informations sur les emplacements libres: </label>
        <button id="click_f1" onclick="getInfoFreePlace()">Récupérer emplacements libres</button>
        <!--Résultat-->
        <ul id="list_free_place_longueur">
        </ul> 
        </div>
        <div class="feature">
        <!--Fonctionnalitée-->
        <label> Récupérer informations sur les prix des places: </label> 
        <button id="click_f2" onclick="getPlacePrice()">Récupérer prix des places</button>
        <!--Résultat-->
        <ul id="list_price">
        </ul> 
        </div>
        <div class="feature">
        <!--Fonctionnalitée-->
        <label> Vérifier si un bateau est present sur un des emplacements du port: </label> 
        <input id="nom_bateau_f3" class="input_feature" type="text" name="nom_bateau" placeholder="nom du bateau" minlength="4" maxlength="20"/> 
        <input id="nom_emplacement_f3" class="input_feature" type="number" name="emplacement" placeholder="numP" min="1" step="1" max="10"/>
        <button id="click_f3" type="submit" onclick="checkBoatIn()">Verifier</button>
        <!--Résultat-->
        <p id="check_boat_in"></p>
        </div>
        <div class="feature">
        <!--Fonctionnalitée-->
        <label> Récupérer les informations d\'un bateau situé a un emplacement particulier: </label> 
        <input id="nom_bateau_f4" class="input_feature" type="text" name="nom_bateau" placeholder="nom du bateau" minlength="4" maxlength="20"/> 
        <input id="nom_emplacement_f4" class="input_feature" type="number" name="emplacement" placeholder="numP" min="1" step="1" max="10"/>
        <select id="list_user_f4">
        </select>
        <button id="click_f4" type="submit" onclick="getBoatInfos()">Récupérer informations</button>
        <!--Résultat-->
        <ul id="list_boat_infos">
        </ul>
        <p id="message"></p>
        </div>
        <div class="feature">
        <!--Fonctionnalitée-->
        <label> Ajouter un bateau sur un emplacement: </label> 
        <input id="nom_bateau_f5" class="input_feature" type="text" name="nom_bateau" placeholder="nom du bateau" minlength="4" maxlength="20"/> 
        <select id="listlongueur_f5">
        </select> 
        <input id="nom_emplacement_f5" class="input_feature" type="number" name="emplacement" placeholder="numP" min="1" step="1" max="10"/>
        <select id="list_modele_f5">
        </select>
        <select id="list_user_f5">
        </select>
        <label for="gard">Option de gardiennage</label><input type="checkbox" id="gard" name="gard" checked> 
        <button id="click_f5" onclick="addBoatAtLocation()">Ajouter bateau</button>
        <!--Résultat-->
        <p id="adding_result"></p>
        </div>
        <div class="feature">
        <!--Fonctionnalitée-->
        <label> Retirer un bateau d\'un emplacement: </label> 
        <select id="listlongueur_f6">
        </select> 
        <select id="list_user_f6">
        </select>
        <input id="nom_emplacement_f6" class="input_feature" type="number" name="emplacement" placeholder="numP" min="1" step="1" max="10"/> 
        <button id="click_f6" type="submit" onclick="removeBoatAtLocation()">Retirer bateau</button>
        <!--Résultat-->
        <p id="removing_result"></p>
        </div>
        <div class="feature">
        <!--Fonctionnalitée-->
        <label> Transférer un bateau vers un autre port: </label> 
        <input id="nom_bateau_f7" class="input_feature" type="text" name="nom_bateau" placeholder="nom du bateau" minlength="4" maxlength="20"/>
        <input id="nom_emplacement_f7" class="input_feature" type="number" name="emplacement" placeholder="numP" min="1" step="1" max="10"/>
        <select id="list_longueur_f7">
        </select>
        <select id="list_port_dest_f7">
        </select>  
        <button id="click_f7" type="submit" onclick="transferBoatAtPort()">Transférer</button>
        <!--Résultat-->
        <p id="transfer_result"></p>
        </div>
        </div>
        </div>
        <script src="fonctions_admin.js"></script>
        <script>refresh();</script>
    ';
}
else if (!empty($type) && isset($type) && $type=="p") { // proprio
    echo '
        <div id="pbody_container">
            <p><u><b>Proprietaire de bateau alias "'.$type.'"</b></u></p> 
            <div id="pfeatures_container">
                <div id="refresh">
                    <button onclick="refresh()">clean & refresh</button>
                    <p id="refresh_mess"></p>
                </div>
                <div id="port_container">
                    <label>Selectionner un port: </label>
                    <select id="list_port_main">
                    </select>    
                </div>
                <div class="feature">
                     <!--Fonctionnalitée-->
                    <label> Récuperer informations sur les emplacements libres</label>
                    <button id="click_f1" onclick="getInfoFreePlace()">Récupérer emplacements libres</button>
                    <!--Résultat-->
                    <ul id="list_free_place_longueur">
                        
                    </ul>  
                </div>
                
                <div class="feature"> 
                    <!--Fonctionnalitée-->
                    <label> Récuperer informations sur les prix des places</label>
                    <button id="click_f2" onclick="getPlacePrice()">Récupérer prix des places</button>
                    <!--Résultat-->
                    <ul id="list_price">
                    </ul> 
                </div>
                
                <div class="feature">
                    <!--Fonctionnalitée-->
                    <label> Récupérer les informations de votre bateau situé à un numero de place</label>
                    <input id="nom_bateau_f3" class="input_feature" type="text" name="nom_bateau" placeholder="nom du bateau" minlength="4" maxlength="20"/>
                    <input id="nom_emplacement_f3" class="input_feature" type="number" name="emplacement" placeholder="numP" min="1" step="1" max="10"/>&emsp;
                    <button id="click_f3" onclick="getBoatInfos()">Récupérer informations</button>
                    <!--Résultat-->
                    <ul id="list_boat_infos">
                    </ul> 
                    <p id="message"></p>
                </div>
            </div>
        </div>
        <script src="fonctions_proprio.js"></script>
        <script>refresh();</script>
    ';
}
else {
    if(!empty($type) && isset($type)) {
        echo 'type d\'utilisateur |' . $type . '| incorrect et donc aucun interface';
    }
}

// on affiche l'historique des requêtes

//$current_request="test";
echo '
    <div id="hbody_container">
        <div id="historique" style="display:inline-block; position: absolute; float:left; border-style: outset; padding:1em; margin-top:1em;">
            <h2 style="text-align: center;">Historique de vos requêtes</h2>
            <div style="text-align: center">
                <div id="div_hist"></div>
                <button onclick="clear_hist();">clear</button>
            </div>
        </div>
    </div> 
';

require_once 'footer.php';
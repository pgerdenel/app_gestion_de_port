const url_base = "http://localhost/C1_portail/ajax_treatments/";

/* GUI */
const requeteHTTP_refresh = new XMLHttpRequest();

/* FEATURES */
const requeteHTTP_getInfoFreePlace = new XMLHttpRequest();            // Création d'un objet XMLHttpRequest
const requeteHTTP_getPlacePrice = new XMLHttpRequest();
const requeteHTTP_getBoatInfos = new XMLHttpRequest();

/* GUI */
requeteHTTP_refresh.onloadend = handler_refresh;

/* FEATURES */
requeteHTTP_getInfoFreePlace.onloadend = handler_getInfoFreePlace;  // Spécification de l'handler à exécuter
requeteHTTP_getPlacePrice.onloadend = handler_getPlacePrice;
requeteHTTP_getBoatInfos.onloadend = handler_getBoatInfos;

/* DATA GUI */
function refresh() {
    let URL = url_base+"queryListPort.php";
    requeteHTTP_refresh.open("GET", URL, true);
    requeteHTTP_refresh.send();
    clear_hist();
    setTimeout(
        function() {
            init_hist();
        }, 1000);
}

/* DATA REQUEST */
// Fonctions pour récupérer les informations sur les places libres
function getInfoFreePlace() {
    document.getElementById('list_free_place_longueur').innerHTML = "";
    let nomPort = document.getElementById("list_port_main").options[document.getElementById("list_port_main").selectedIndex].value;
    let URL = url_base+"getInfoFreePlace.php";
    requeteHTTP_getInfoFreePlace.open("POST", URL, true);
    requeteHTTP_getInfoFreePlace.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    requeteHTTP_getInfoFreePlace.send("nomport="+nomPort);
}
// Fonction pour récupérer les prix des places
function getPlacePrice() {
    document.getElementById('list_price').innerHTML = "";
    let nomPort = document.getElementById("list_port_main").options[document.getElementById("list_port_main").selectedIndex].value;
    if(getCookie("place_price") === -1) {
        let URL = url_base + "getPlacePrice.php";
        requeteHTTP_getPlacePrice.open("POST", URL, true);
        requeteHTTP_getPlacePrice.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        requeteHTTP_getPlacePrice.send("nomport=" + nomPort + "&exist=" + getCookie("place_price")); // on ajoute un champ exist avec -1 si cookie n'existe pas ou 0 s'il existe
    }
    else {
        console.log("on charge le résultat depuis le cookie");
        handler_getPlacePrice();
    }
}
// Fonction pour récupérer les informations d'un bateau
function getBoatInfos() {
    document.getElementById('list_boat_infos').innerHTML = "";
    document.getElementById('message').innerHTML = "";
    let nomPort = document.getElementById("list_port_main").options[document.getElementById("list_port_main").selectedIndex].value;
    let nomBateau = document.getElementById("nom_bateau_f3").value;
    let nomEmplacement = document.getElementById("nom_emplacement_f3").value;
    if(getCookie("boat_infos") === -1) {
        let URL = url_base + "getBoatInfosProprio.php";
        requeteHTTP_getBoatInfos.open("POST", URL, true);
        requeteHTTP_getBoatInfos.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        requeteHTTP_getBoatInfos.send("nomport=" + nomPort + "&nombateau=" + nomBateau + "&nomemplacement=" + nomEmplacement + "&exist=" + getCookie("boat_infos")); // on ajoute un champ exist avec -1 si cookie n'existe pas ou 0 s'il existe
    }
    else {
        console.log("on charge le résultat depuis le cookie");
        handler_getBoatInfos();
    }
}

/* DATA GUI */
function handler_refresh() {
    if((requeteHTTP_refresh.readyState === 4) && (requeteHTTP_refresh.status === 200)) {

        // clean result
        document.getElementById('refresh_mess').innerHTML = "";
        document.getElementById('list_free_place_longueur').innerHTML = "";
        document.getElementById('list_price').innerHTML = "";
        document.getElementById('list_boat_infos').innerHTML = "";
        document.getElementById('message').innerHTML = "";

        // List Port Refresh
        document.getElementById('list_port_main').innerHTML = "";
        let docJSON = JSON.parse(requeteHTTP_refresh.responseText);

        //console.log(docJSON);
        for(let i=0;i< docJSON.length; i++) {
            document.getElementById('list_port_main').innerHTML += '<option value="'+docJSON[i]+'">'+docJSON[i]+'</option>';
        }
    }
    else {
        alert("Erreur lors de la requête AJaX");
        document.getElementById('refresh_mess').innerHTML = "Erreur lors de la requête AJaX";
    }
}

/* DATA REQUEST */
function handler_getInfoFreePlace() {
    if((requeteHTTP_getInfoFreePlace.readyState === 4) && (requeteHTTP_getInfoFreePlace.status === 200)) {
        let docJSON = JSON.parse(requeteHTTP_getInfoFreePlace.responseText);
        for (let key in docJSON) {
            if (docJSON.hasOwnProperty(key)) {
                //console.log("key longueur = "+key); // longueur
                document.getElementById('list_free_place_longueur').innerHTML +=
                    '<li id="free_place_longueur_item">' + "<b>Longueur</b>: "+key+" m";
                // on itère sur les places
                for (let key2 in docJSON[key]) {
                    //console.log("\tkey num_place = "+key2+", prix = "+docJSON[key][key2]['prix']); // data cut
                    if (docJSON[key].hasOwnProperty(key2)) {
                        document.getElementById('list_free_place_longueur').innerHTML +=
                            '<li id="free_place_numplace_item" style="color:cornflowerblue;margin-left:1em;">' + "<b>Numéro de place</b>: "+key2 + ", " + "<b>Prix</b>: "+docJSON[key][key2]['prix'] + " €"+ '</li>';
                    }
                }

                document.getElementById('list_free_place_longueur').innerHTML +='</li>';
                document.getElementById("list_free_place_longueur").style.color = "red";
            }
        }
        document.getElementById("click_f1").disabled = true;
        setTimeout(function(){document.getElementById("click_f1").disabled = false;},5000);

        // on enregistre la requête dans l'historique et on le rafraichit
        const record_cookie_free_place = new XMLHttpRequest();
        let URL = url_base+"record_cookie_hist.php?"+"key="+"free_place";
        record_cookie_free_place.open("GET", URL, true);
        record_cookie_free_place.send();
        setTimeout(
            function() {
                refresh_hist()
            }, 250);

    }
    else {
        alert("Erreur lors de la requête AJaX");
        document.getElementById('list_free_place').innerHTML = "Erreur lors de la requête AJaX";
    }
}
function handler_getPlacePrice() {
    if((requeteHTTP_getPlacePrice.readyState === 4) && (requeteHTTP_getPlacePrice.status === 200)) {
        //console.log("pas parse= "+requeteHTTP_getPlacePrice.responseText);
        //console.log("parse= "+JSON.parse(requeteHTTP_getPlacePrice.responseText));
        let result_place_price="";
        let docJSON = JSON.parse(requeteHTTP_getPlacePrice.responseText);
            for (let key in docJSON) {
                if (docJSON.hasOwnProperty(key)) {
                    result_place_price += '<li style="color:green">' + "<b>Longueur</b>: "+docJSON[key]['longueur'] + ", " + "<b>Prix</b>: "+docJSON[key]['prix'] + "€"+ '</li>';
                }
            }
        document.getElementById('list_price').innerHTML = result_place_price;
        document.getElementById("click_f2").disabled = true;
        setTimeout(function(){document.getElementById("click_f2").disabled = false;},5000);

        // on enregistre la requête dans l'historique et on le rafraichit
        const record_cookie_place_price = new XMLHttpRequest();
        let URL = url_base+"record_cookie_hist.php?key=place_price";
        record_cookie_place_price.open("GET", URL, true);
        record_cookie_place_price.send();
        setTimeout(
            function() {
                refresh_hist()
            }, 1000);

        // on enregistre le résultat de cette requête dans les cookies $_Cookie['place_price']
        setCookie("place_price", result_place_price, 3600);
    }
    else {
        if(getCookie("place_price") === 0) {
            document.getElementById('list_price').innerHTML = getCookieVal("place_price");
        }
        else {
            alert("Erreur lors de la requête AJaX.");
            document.getElementById('list_price').innerHTML = "Erreur lors de la requête AJaX";
        }
    }
}
function handler_getBoatInfos() {
    if((requeteHTTP_getBoatInfos.readyState === 4) && (requeteHTTP_getBoatInfos.status === 200)) {
        // On vérifie que la réponse est une réponse pour indiquer que le bateau n'existe pas à cette emplacement
        // ou que le bateau existe et que on la réponse contient les données de ce bateau
        // console.log("JSON recup "+requeteHTTP_getBoatInfos.responseText);
        let message="";
        try {
            let docJSON = JSON.parse(requeteHTTP_getBoatInfos.responseText.trim());
            if (docJSON['result'] === "data") {

                let carnet_bord ="";
                for (let key00 in docJSON['carnetb']) {
                    if (docJSON['carnetb'].hasOwnProperty(key00)) {
                        //console.log("key00 "+key00);
                        carnet_bord += "<br/>&emsp;&emsp;"+key00.toUpperCase()+": ";
                            let tab_port = docJSON['carnetb'][key00]['list_port'];
                            let tab_coord = docJSON['carnetb'][key00]['list_coord'];
                            for(let i=0;i<tab_port.length;i++) {
                                carnet_bord += "<br/>&emsp;&emsp;&emsp;"+tab_port[i]+": ";
                                for (let key11 in tab_coord[i]) {
                                    if (tab_coord[i].hasOwnProperty(key11)) {
                                        //console.log("key11: "+key11+" value: "+JSON.stringify(tab_coord[key11])+" value2: "+JSON.stringify(tab_coord[i][key11]));
                                        carnet_bord += "latitude: "+key11+" longitude: "+tab_coord[i][key11];
                                    }
                                }
                            }
                        carnet_bord+= "<br/>"
                    }
                }
                let carnet_entretien = "";
                // on itère les catégorie d'entretien
                for (let key in docJSON['carnetm']['entretien']) {
                    if (docJSON['carnetm']['entretien'].hasOwnProperty(key)) {
                        carnet_entretien += "&emsp;"+key.toUpperCase() + ": <br/>";
                        // on itère les points d'entretiens
                        for (let key2 in docJSON['carnetm']['entretien'][key]) {
                            if (docJSON['carnetm']['entretien'][key].hasOwnProperty(key2)) {
                                carnet_entretien += "&emsp;&emsp;<b>"+key2 + "</b>: <br/>";
                                // on itère les informaitons du point d'entretien
                                for (let key3 in docJSON['carnetm']['entretien'][key][key2]) {
                                    if (docJSON['carnetm']['entretien'][key][key2].hasOwnProperty(key3)) {
                                        carnet_entretien+= "&emsp;&emsp;"+key3 + "= " + docJSON['carnetm']['entretien'][key][key2][key3];
                                    }
                                }
                            }
                            carnet_entretien += "<br/>";
                        }
                    }
                    carnet_entretien += "<br/>";
                }
                //let i=0; // '<li id="point_info_boat'+(i++).toString()+'">'
                document.getElementById("list_boat_infos").innerHTML +=
                    '<li>' + "Nom du bateau: " + docJSON['nom'] + '</li>' +
                    '<li>' + "Modele: " + docJSON['modele'] + '</li>' +
                    '<li>' + "Longueur: " + docJSON['longueur'] + "m" + '</li>' +
                    '<li>' + "Propriétaire: " + docJSON['proprio'] + '</li>' +
                    '<li>' + "Carnet de bord: " + "<br/>"+
                    "&emsp;Liste des trajets: " + carnet_bord + '</li>'+
                    '<li>' + "Carnet de maintenance: " + "<br/>" +
                    "EntretienFait: " +docJSON['carnetm']['entretienfait'] + "<br/>" +
                    "Entretien: " + "<br/><br/>"+carnet_entretien + "<br/>"
                    + '</li>';

                let infos = document.getElementById("list_boat_infos").innerHTML;

                //document.getElementById("point_info_boat").style.marginBottom = "1em";

                document.getElementById("click_f3").disabled = true;
                setTimeout(function () {
                    document.getElementById("click_f3").disabled = false;
                }, 5000);

                // on enregistre la requête dans l'historique  et on le rafraichit
                const record_cookie_boat_infos = new XMLHttpRequest();
                let URL = url_base+"record_cookie_hist.php?"+"key="+"boat_infos";
                record_cookie_boat_infos.open("GET", URL, true);
                record_cookie_boat_infos.send();
                setTimeout(
                    function() {
                        refresh_hist()
                    }, 1000);

                // on enregistre le résultat de cette requête dans les cookies $_Cookie['boat_infos']
                setCookie("boat_infos", infos, 60);
            }
            else if (docJSON['result'] === "no_data") {
                message = "le bateau n'existe pas à cette emplacement";
                document.getElementById("message").innerHTML += message;
                document.getElementById("click_f3").disabled = true;
                setTimeout(function () {
                    document.getElementById("click_f3").disabled = false;
                }, 5000);

                // on enregistre la requête dans l'historique  et on le rafraichit
                const record_cookie_boat_infos = new XMLHttpRequest();
                let URL = url_base+"record_cookie_hist.php?"+"key="+"boat_infos";
                record_cookie_boat_infos.open("GET", URL, true);
                record_cookie_boat_infos.send();
                setTimeout(
                    function() {
                        refresh_hist()
                    }, 1000);
            }
            else {
                document.getElementById("list_boat_infos").innerHTML += "valeur de retour incorrectes";
                document.getElementById("click_f3").disabled = true;
                setTimeout(function () {
                    document.getElementById("click_f3").disabled = false;
                }, 5000);
            }
        }
        catch(e) {
            console.log("exception throwed "+e);
            message = requeteHTTP_getBoatInfos.responseText;
            document.getElementById("message").innerHTML += message;
        }
    }
    else {
        if(getCookie("boat_infos") === 0) {
            document.getElementById('list_boat_infos').innerHTML = getCookieVal("boat_infos");
        }
        else {
            alert("Erreur lors de la requête AJaX.");
            document.getElementById('list_boat_infos').innerHTML = "Erreur lors de la requête AJaX";
        }
    }
}
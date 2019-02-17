const url_base_my = "http://localhost/C1_portail/ajax_treatments/";

const requeteHTTP_init_hist = new XMLHttpRequest();
const requeteHTTP_refresh_hist = new XMLHttpRequest();
const requeteHTTP_clear_hist = new XMLHttpRequest();

requeteHTTP_init_hist.onloadend = handler_init_hist;
requeteHTTP_refresh_hist.onloadend = handler_refresh_hist;
requeteHTTP_clear_hist.onloadend = handler_clear_hist;

function init_hist() {
    //console.log("init_hist called");
    let URL = url_base_my+"manage_hist.php?t=init";
    requeteHTTP_init_hist.open("GET", URL, true);
    requeteHTTP_init_hist.send();
}
function refresh_hist() {
    console.log("refresh_hist called");
    let URL = url_base_my+"manage_hist.php?t=refresh";
    requeteHTTP_refresh_hist.open("GET", URL, true);
    requeteHTTP_refresh_hist.send();
}
function clear_hist() {
    //console.log("clear_hist called");
    let URL = url_base_my+"manage_hist.php?t=clear";
    requeteHTTP_clear_hist.open("GET", URL, true);
    requeteHTTP_clear_hist.send();
}

function handler_init_hist() {
    console.log("handler_init_hist called");
    if ((requeteHTTP_init_hist.readyState === 4) && (requeteHTTP_init_hist.status === 200)) {
        document.getElementById('div_hist').innerHTML = requeteHTTP_init_hist.responseText;
    }
    else {
        alert("Erreur lors de la requête AJaX");
        document.getElementById('div_hist').innerHTML = "Erreur lors de la requête AJaX";
    }
}
function handler_refresh_hist() {
    console.log("handler_refresh_hist called");
    if ((requeteHTTP_refresh_hist.readyState === 4) && (requeteHTTP_refresh_hist.status === 200)) {
        //let docJSON = JSON.parse(requeteHTTP_refresh_hist.responseText);=
        document.getElementById('div_hist').innerHTML = requeteHTTP_refresh_hist.responseText;
    }
    else {
        alert("Erreur lors de la requête AJaX");
        document.getElementById('div_hist').innerHTML = "Erreur lors de la requête AJaX";
    }
}
function handler_clear_hist() {
    console.log("handler_clear_hist called");
    if ((requeteHTTP_clear_hist.readyState === 4) && (requeteHTTP_clear_hist.status === 200)) {
        //let docJSON = JSON.parse(requeteHTTP_clear_hist.responseText);
        document.getElementById('div_hist').innerHTML = requeteHTTP_clear_hist.responseText;
    }
    else {
        alert("Erreur lors de la requête AJaX");
        document.getElementById('div_hist').innerHTML = "Erreur lors de la requête AJaX";
    }
}

function setCookie(cname, cvalue, seconde) {
    let d = new Date();
    d.setTime(d.getTime() + (seconde*500)/*+(3600*1000)*/);
    let expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    let name = cname + "=";
    let ca = document.cookie.split(';');
    for(let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return 0;
        }
    }
    return -1;
}

function getCookieVal(cname) {
    let name = cname + "=";
    let ca = document.cookie.split(';');
    for(let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}
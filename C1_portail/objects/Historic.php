<?php
/**
 * Gère l'historique des requêtes que l'utilisateur a effectué
 * - objet stocké dans les cookies
 * - permet de creer un objet Historic qui stocke les résultats des requêtes dans un tableau de requête
 * - résultat des requêtes au format json ou texte
 */



class Historic {

    // Attributes
    private $pseudo;        // pseudo de l'user
    private $tab_request;   // tableau clé=id_traitement et valeur= resultat json de la requête
    private $last_key_request;     // dernière requête effectué

    public function __construct_default() {
        $this->pseudo = "";
        $this->last_key_request = "aucune dernière requête";
        $this->tab_request = array();
    }
    // Destructor
    public function __destruct() {
        // echo "Destruction de  [...] \n";
    }

    // Getters and setters
    /**
     * @return array
     */
    public function getTabRequest()
    {
        return $this->tab_request;
    }
    /**
     * @param array $tab_request
     */
    public function setTabRequest($tab_request): void
    {
        $this->tab_request = $tab_request;
    }
    /**
     * @return string
     * @param int $i
     * insère une requete dans le tableau à l'indice spécifié
     */
    public function getRequest($i) {
        return $this->tab_request[$i];
    }
    /**
     * @param string $request
     * @param int $i
     * insère une requete dans le tableau à l'indice spécifié
     */
    public function setRequest($request, $i): void
    {
        $this->tab_request[$i] = $request;
    }
    /**
     * @return string
     */
    public function getPseudo()
    {
        return $this->pseudo;
    }
    /**
     * @param string $pseudo
     */
    public function setPseudo($pseudo): void
    {
        $this->pseudo = $pseudo;
    }
    /**
     * @return string
     */
    public function getLastKeyRequest()
    {
        return $this->last_key_request;
    }
    /**
     * @param string $last_key_request
     */
    public function setLastKeyRequest($last_key_request): void
    {
        $this->last_key_request = $last_key_request;
    }

    // Others Methods

    /**
     * @param string
     * Insère une requête en fin de tableau et supprime la même entrée si elle existe
     */
    public function pushRequest($key_request) {
        if(!empty($this->tab_request)) {
            // on cherche la clé du tableau correspondant à la valeur du paramètre si elle existe
            if(!in_array($key_request, $this->tab_request)) {
                array_push($this->tab_request, $key_request);
            }
            $key = array_search($key_request, $this->tab_request);
            array_push($this->tab_request, $key_request);
            unset($this->tab_request[$key]);
        }
        else {
            $this->tab_request = array();
            array_push($this->tab_request, $key_request);
        }
    }
    /**
     * @return string
     * Renvoie le tableau de requêtes convertit en string
     */
    function displayHistoric() {
        $historic = '';
        if(empty($this->tab_request) && !isset($this->tab_request)) {
            $historic="Aucune requête dans l'historique";
        }
        else {
            foreach(array_reverse($this->tab_request) as $value) {
                $historic .= $value.'<br/>';
            }
        }
        return $historic;
    }
    /**
     * @return void
     * Assigne la valeur null au tableau de requête et à la dernière requête effectuée
     */
    public function clearHistoric() {
        $this->tab_request = array();
    }
    /**
     * @return void
     * Itère l'objet ses attributs et valeurs
     */
    public function iterateVisible() {
        print 'Object Historic iterabled<br/>';
        foreach ($this as $key => $value) {
            if(gettype($value) != "array") {
                print "*$key => $value<br/>";
            }
            else {
                print "*$key =>".$this->displayHistoric();
            }
        }
    }
    /**
     * @return string
     * Convertit et affiche l'objet en string
     */
    public function __toString() {
        return 'Object Historic as String<br/>'.'* pseudo ='.$this->pseudo.'<br/>* lastRequest ='.$this->last_key_request.'<br/>* tabRequest = '.$this->displayHistoric();
    }

}

//default
/*$hist = new Historic();
$hist->pushRequest("test");
$hist->pushRequest("test2");
$hist->pushRequest("test3");
$hist->pushRequest("test");
echo $hist->displayHistoric();
echo 'taille '.count($hist->getTabRequest());*/




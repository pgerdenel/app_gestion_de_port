<?php
/**
 * Class d'objet Entretien
 * contient une map de toutes les catégories d'entretien
 * http://localhost/C1_portail/objects/carnet_maintenance/Entretien.php
 */

require_once('E_Model_to_Categorie.php');
require_once('E_Model_to_Nom.php');
require_once('E_Categorie_to_Point_Entretien.php');

class Entretien implements \JsonSerializable
{

    private $map_cat_entretien; // Map<nom_categorie, Categorie_Entretien>
    private $modele;

    /**
     * Entretien constructor.
     * Construit un objet Entretien par default
     * @param $modele
     */
    public function __construct($modele)
    {
        $this->modele = $modele;
        $map = array();
        // On switch sur les valeurs de l'enum
        switch ($modele) {
            case \E_Model_to_Nom::default:
                foreach (\E_Model_to_Categorie::default as $key => $value){
                    array_push($map, new \Categorie_Entretien($key, $modele));
                }
                $this->map_cat_entretien = $map;
                break;
            case \E_Model_to_Nom::voilier:
                foreach (\E_Model_to_Categorie::voilier as $key => $value){
                    array_push($map, new \Categorie_Entretien($key, $modele));
                }
                $this->map_cat_entretien = $map;
                break;
            case \E_Model_to_Nom::trois_mat:
                foreach (\E_Model_to_Categorie::trois_mat as $key => $value){
                    array_push($map, new \Categorie_Entretien($key, $modele));
                }
                $this->map_cat_entretien = $map;
                break;
            case \E_Model_to_Nom::moteur:
                foreach (\E_Model_to_Categorie::moteur as $key => $value){
                    array_push($map, new \Categorie_Entretien($key, $modele));
                }
                $this->map_cat_entretien = $map;
                break;
            case \E_Model_to_Nom::sportif:
                foreach (\E_Model_to_Categorie::sportif as $key => $value){
                    array_push($map, new \Categorie_Entretien($key, $modele));
                }
                $this->map_cat_entretien = $map;
                break;
            case \E_Model_to_Nom::zodiac:
                foreach (\E_Model_to_Categorie::zodiac as $key => $value){
                    array_push($map, new \Categorie_Entretien($key, $modele));
                }
                $this->map_cat_entretien = $map;
                break;
            case \E_Model_to_Nom::croisiere:
                foreach (\E_Model_to_Categorie::croisiere as $key => $value){
                    array_push($map, new \Categorie_Entretien($key, $modele));
                }
                $this->map_cat_entretien = $map;
                break;
        }
    }

    /**
     * @return array
     */
    public function getMapCatEntretien()
    {
        return $this->map_cat_entretien;
    }

    /**
     * @param $map_cat_entretien
     */
    public function setMapCatEntretien($map_cat_entretien): void
    {
        $this->map_cat_entretien = $map_cat_entretien;
    }

    /**
     * @param $categorie
     */
    public function addCatEntretienInMap($categorie) {
        // A FAIRE
    }

    /**
     * @return string
     */
    public function __toString()
    {
        return  '*Entretien object<br/>'.
                'carnet_entretien= <pre>'.implode('<br/><br/>',$this->map_cat_entretien).'</pre>';

    }

    public function jsonSerialize() {
        $json_array = array();
        foreach ($this->map_cat_entretien as $key => $value) {
            $json_array[$key] = $value->jsonSerialize();
        }
        return $json_array;
    }
}
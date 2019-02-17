<?php
/**
 * Class d'objet Categorie_Entretien
 * contient l'ensemble des points d'entretien de la catégorie
 * peut être associé à un type de bateau particulier
 * http://localhost/C1_portail/objects/carnet_maintenance/Categorie_Entretien.php
 */

require_once('Point_Entretien.php');

class Categorie_Entretien implements \JsonSerializable
{
    private $nom_categorie; // nom de la catégorie d'entretien (moteur, etc ....)
    private $map_point_entretien; // map<nom_point_entretien, point_entretien>
    private $modele;  // modele du bateau lié à cette catégorie

    /**
     * Categorie_Entretien constructor.
     * @param string $nom_categorie
     * @param string $modele
     */
    public function __construct(string $nom_categorie, string $modele)
    {
        $this->nom_categorie = $nom_categorie;
        $this->modele = $modele;
        $map = array();

        switch ($modele) {
            case E_Model_to_Nom::default:
                echo 'default';
                foreach (E_Model_to_Categorie::default as $key => $value){
                    foreach ($value as $key2 => $value2) {
                        $map[$key2] = new Point_entretien($key2, $value2);
                    }
                }
                $this->map_point_entretien = $map;
                break;
            case E_Model_to_Nom::voilier:
                //echo 'voilier<br/>';
                foreach (E_Model_to_Categorie::voilier as $key => $value){
                    foreach ($value as $key2 => $value2) {
                        $map[$key2] = new Point_entretien($key2, $value2);
                    }
                }
                $this->map_point_entretien = $map;
                break;
            case E_Model_to_Nom::trois_mat:
                // echo 'trois_mat';
                foreach (E_Model_to_Categorie::trois_mat as $key => $value){
                    foreach ($value as $key2 => $value2) {
                        $map[$key2] = new Point_entretien($key2, $value2);
                    }
                }
                $this->map_point_entretien = $map;
                break;
            case E_Model_to_Nom::moteur:
                // echo 'moteur';
                foreach (E_Model_to_Categorie::moteur as $key => $value){
                    foreach ($value as $key2 => $value2) {
                        $map[$key2] = new Point_entretien($key2, $value2);
                    }
                }
                $this->map_point_entretien = $map;
                break;
            case E_Model_to_Nom::sportif:
                // echo 'sportif';
                foreach (E_Model_to_Categorie::sportif as $key => $value){
                    foreach ($value as $key2 => $value2) {
                        $map[$key2] = new Point_entretien($key2, $value2);
                    }
                }
                $this->map_point_entretien = $map;
                break;
            case E_Model_to_Nom::zodiac:
                // echo 'zodiac';
                foreach (E_Model_to_Categorie::zodiac as $key => $value){
                    foreach ($value as $key2 => $value2) {
                        $map[$key2] = new Point_entretien($key2, $value2);
                    }
                }
                $this->map_point_entretien = $map;
                break;
            case E_Model_to_Nom::croisiere:
                // echo 'croisiere';
                foreach (E_Model_to_Categorie::croisiere as $key => $value){
                    foreach ($value as $key2 => $value2) {
                        $map[$key2] = new Point_entretien($key2, $value2);
                    }
                }
                $this->map_point_entretien = $map;
                break;
        }
    }

    /**
     * @return string
     */
    public function getNomCategorie(): string
    {
        return $this->nom_categorie;
    }
    /**
     * @param string $nom_categorie
     */
    public function setNomCategorie(string $nom_categorie): void
    {
        $this->nom_categorie = $nom_categorie;
    }
    /**
     * @return array
     */
    public function getMapPointEntretien(): array
    {
        return $this->map_point_entretien;
    }
    /**
     * @param array $map_point_entretien
     */
    public function setMapPointEntretien(array $map_point_entretien): void
    {
        $this->map_point_entretien = $map_point_entretien;
    }
    /**
     * @return string
     */
    public function getModele(): string
    {
        return $this->modele;
    }
    /**
     * @param string $modele
     */
    public function setModele(string $modele): void
    {
        $this->modele = $modele;
    }

    /**
     * @param Point_entretien $point_entretien_to_add
     */
    public function addPointToCat($point_entretien_to_add) {
        $this->map_point_entretien[$point_entretien_to_add->getNomPointEntretien()] = $point_entretien_to_add;
    }
    /**
     * @return string
     */
    public function __toString()
    {
        return  '*Categorie_Entretien object<br/>'.
                'nom_categorie= '.$this->nom_categorie.'<br/>'.
                'modele= '.$this->modele.'<br/>'.
                'map_point_entretien= <pre>'.implode('<br/><br/>',$this->map_point_entretien).'</pre>';
    }

    public function jsonSerialize() {
        $json_array = array();
        foreach($this->getMapPointEntretien() as $key => $value) {
            $json_array[$key] = $value->jsonSerialize();
        }
        return $json_array;
    }


}
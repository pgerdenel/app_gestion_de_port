<?php
/**
 * Class d'objet Bateau
 * http://localhost/C1_portail/objects/Bateau.php
 */

namespace objects;


class Bateau implements \JsonSerializable
{
    private $id;                // id du bateau
    private $nom;               // nom du bateau
    private $modele;            // modele du bateau
    private $taille;
    private $carnetMaintenance;
    private $carnet_bord;

    /**
     * Bateau constructor.
     * @param string $id
     * @param string $nom
     * @param string $modele
     * @param integer $taille
     * @param \Carnet_maintenance $carnetMaintenance
     * @param array $carnet_bord
     */
    public function __construct($id, $nom, $modele, $taille, $carnetMaintenance, $carnet_bord)
    {
        $this->id = $id;
        $this->nom = $nom;
        $this->modele = $modele;
        $this->taille = $taille;
        $this->carnetMaintenance = $carnetMaintenance;
        $this->carnet_bord = $carnet_bord;
    }
    public static function __constructBycopy($bateau)
    {
        $bateau = new Bateau($bateau->id, $bateau->nom, $bateau->modele, $bateau->taille, $bateau->carnetMaintenance, $bateau->carnet_bord);
        return $bateau;
    }

    /**
     * @return string
     */
    public function getId()
    {
        return $this->id;
    }
    /**
     * @param string $id
     */
    public function setId($id): void
    {
        $this->id = $id;
    }
    /**
     * @return string
     */
    public function getNom()
    {
        return $this->nom;
    }
    /**
     * @param string $nom
     */
    public function setNom($nom): void
    {
        $this->nom = $nom;
    }
    /**
     * @return string
     */
    public function getModele()
    {
        return $this->modele;
    }
    /**
     * @param $modele
     */
    public function setModele($modele): void
    {
        $this->modele = $modele;
    }
    /**
     * @return integer
     */
    public function getTaille()
    {
        return $this->taille;
    }
    /**
     * @param integer $taille
     */
    public function setTaille($taille): void
    {
        $this->taille = $taille;
    }
    /**
     * @return \Carnet_maintenance
     */
    public function getCarnetMaintenance()
    {
        return $this->carnetMaintenance;
    }
    /**
     * @param \Carnet_maintenance $carnetMaintenance
     */
    public function setCarnetMaintenance($carnetMaintenance): void
    {
        $this->carnetMaintenance = $carnetMaintenance;
    }
    /**
     * @return array
     */
    public function getCarnetBord()
    {
        return $this->carnet_bord;
    }
    /**
     * @param array $carnet_bord
     */
    public function setCarnetBord($carnet_bord): void
    {
        $this->carnet_bord = $carnet_bord;
    }

    /**
     * @return string
     */
    public function __toString()
    {
        return  '*Bateau object: <br/>'.
                'id : '.$this->id.'<br/>'.
                'nom : '.$this->nom.'<br/>'.
                'modele : '.$this->modele.'<br/>'.
                'taille : '.$this->taille.'<br/>'.
                '*carnet_bord attribute: <br/>'.implode('<br/>',$this->carnet_bord).'<br/><br/>'.
                'carnetMaintenance : '.$this->carnetMaintenance;
    }

    public function jsonSerialize() {
        return [
            'id' => $this->getId(),
            'nom' => $this->getNom(),
            'modele' => $this->getModele(),
            'taille' => $this->getTaille(),
            'carnet_bord' => $this->getCarnetBord(),
            'carnetMaintenance' => $this->getCarnetMaintenance()
        ];
    }

}

// test
/*$a = [];
$bat = new \objects\Bateau("id_test", "nom_test", "voilier", -6, new Carnet_maintenance(false, "voilier"), array("port1", "port2"));
//setcookie("bateau", json_encode($bat), time()+3600, "/");
echo $bat->jsonSerialize();*/
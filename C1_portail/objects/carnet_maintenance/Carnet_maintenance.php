<?php
/**
 * Class d'objet Carnet_maintenance
 * - contient un objet entretien
 * - contient un boolean déterminant si l'entretien a été fait
 * http://localhost/C1_portail/objects/carnet_maintenance/Carnet_maintenance.php
 */

require_once('Categorie_Entretien.php');
require_once('Entretien.php');

class Carnet_maintenance implements \JsonSerializable
{
    private $entretienfait;
    private $entretien;

    /**
     * Carnet_maintenance constructor.
     * @param $entretienfait
     * @param $modele
     */
    public function __construct($entretienfait, $modele) {
        $this->entretien = new Entretien($modele);
        $this->entretienfait = $entretienfait;
    }

    /**
     * @return mixed
     */
    public function getEntretienfait()
    {
        return $this->entretienfait;
    }
    /**
     * @param boolean $entretienfait
     */
    public function setEntretienfait($entretienfait): void
    {
        $this->entretienfait = $entretienfait;
    }

    /**
     * @return Entretien
     */
    public function getEntretien(): Entretien
    {
        return $this->entretien;
    }

    /**
     * @param Entretien $entretien
     */
    public function setEntretien(Entretien $entretien): void
    {
        $this->entretien = $entretien;
    }


    /**
     * @return string
     */
    public function __toString() {
        return  '*Carnet Maintenance object<br/>'.
                'entretienfait= '.$this->entretienfait.'<br/>'.
                'entretien= <br/>'.$this->entretien;
    }

    public function jsonSerialize() {
        return [
            'entretienfait' => $this->getEntretienfait(),
            'entretien' => $this->getEntretien()->jsonSerialize()
        ];
    }
}
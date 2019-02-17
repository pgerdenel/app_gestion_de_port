<?php
/**
 * Class d'objet Port
 * - représente le nom d'un port et ses coordonnées GPS
 */

namespace objects\carnet_bord;


class Port
{
    private $nom_port;
    private $coord_GPS;

    /**
     * Port constructor.
     * @param string $nom_port
     * @param Coord_GPS $coord_GPS
     */
    public function __construct($nom_port, $coord_GPS)
    {
        $this->nom_port = $nom_port;
        $this->coord_GPS = $coord_GPS;
    }

    /**
     * @return string
     */
    public function getNomPort()
    {
        return $this->nom_port;
    }
    /**
     * @param string $nom_port
     */
    public function setNomPort($nom_port): void
    {
        $this->nom_port = $nom_port;
    }
    /**
     * @return Coord_GPS
     */
    public function getCoordGPS()
    {
        return $this->coord_GPS;
    }
    /**
     * @param Coord_GPS $coord_GPS
     */
    public function setCoordGPS($coord_GPS): void
    {
        $this->coord_GPS = $coord_GPS;
    }

    /**
     * @return string
     */
    public function __toString()
    {
        return  '* Port Object <br/>'.
                'Nom_port = '.$this->nom_port.'<br/>'.
                $this->coord_GPS;
    }

}
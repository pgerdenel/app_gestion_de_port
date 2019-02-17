<?php
/**
 * Class d'objet Coord_GPS
 * - représente les coordonnées GPS latitude, longitude de la position d'un port
 */

namespace objects\carnet_bord;

class Coord_GPS
{
    private $latitude;
    private $longitude;

    /**
     * Coord_GPS constructor.
     * @param double $latitude
     * @param double $longitude
     */
    public function __construct($latitude, $longitude)
    {
        $this->latitude = $latitude;
        $this->longitude = $longitude;
    }

    /**
     * @return double
     */
    public function getLatitude()
    {
        return $this->latitude;
    }
    /**
     * @param double $latitude
     */
    public function setLatitude($latitude): void
    {
        $this->latitude = $latitude;
    }
    /**
     * @return double
     */
    public function getLongitude()
    {
        return $this->longitude;
    }
    /**
     * @param double $longitude
     */
    public function setLongitude($longitude): void
    {
        $this->longitude = $longitude;
    }

    /**
     * @return string
     */
    public function __toString()
    {
        return  '*Coord_GPS object: '.
                'latitude = '.number_format($this->latitude, 2, ',', ' ').'&emsp;'.
                'longitude = '.number_format($this->longitude, 2, ',', ' ');
    }

}
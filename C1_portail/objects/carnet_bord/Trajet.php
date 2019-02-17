<?php
/**
 * Class d'objet Trajet
 * - représente le trajet d'un bateau
 * - doit contenir 2 ports(départ, arrivée)
 */

namespace objects\carnet_bord;


class Trajet {

    private $list_port;

    /**
     * Trajet constructor.
     * @param array $list_port
     */
    public function __construct($list_port) {
        if (sizeof($list_port) > 1) {
            $this->list_port = $list_port;
        }
    }

    /**
     * @return array
     */
    public function getListPort(): array
    {
        return $this->list_port;
    }
    /**
     * @param array $list_port
     */
    public function setListPort(array $list_port): void
    {
        $this->list_port = $list_port;
    }

    /**
     * @return string
     */
    public function __toString()
    {
        $list_port_str='*Trajet object<br/>'.implode(',', $this->list_port);
        return $list_port_str;

    }


}
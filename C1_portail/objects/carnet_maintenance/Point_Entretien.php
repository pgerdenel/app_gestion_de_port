<?php
/**
 * Class d'objet Point_Entretien
 * représente un point d'entretien parmi d'autre au sein d'une catégorie
 * http://localhost/C1_portail/objects/carnet_maintenance/Point_Entretien.php
 */

class Point_entretien implements \JsonSerializable  {

    private $nom_point_entretien;
    private $periodicie;
    private $date_derniere_verif;
    private $date_prochaine_verif;

    /**
     * Point_entretien constructor.
     * @param $nom_point_entretien
     * @param $periodicie
     */
    public function __construct($nom_point_entretien, $periodicie)
    {
        $currentDate = new \DateTime();
        $dateAfter =  new \DateTime();
        try {
            $dateAfter->add(new \DateInterval("P" . $periodicie . "M"));
        }
        catch(\Exception $e) {
            echo 'exception date et tant pis '.$e;
        }

        $this->nom_point_entretien = $nom_point_entretien;
        $this->periodicie = $periodicie;
        $this->date_derniere_verif = $currentDate->format("d/m/y");
        $this->date_prochaine_verif = $dateAfter->format("d/m/y");
    }

    /**
     * @return string
     */
    public function getNomPointEntretien(): string
    {
        return $this->nom_point_entretien;
    }
    /**
     * @param string $nom_point_entretien
     */
    public function setNomPointEntretien(string $nom_point_entretien): void
    {
        $this->nom_point_entretien = $nom_point_entretien;
    }
    /**
     * @return int
     */
    public function getPeriodicie(): int
    {
        return $this->periodicie;
    }
    /**
     * @param int $periodicie
     */
    public function setPeriodicie(int $periodicie): void
    {
        $this->periodicie = $periodicie;
    }
    /**
     * @return string
     */
    public function getDateDerniereVerif(): string
    {
        return $this->date_derniere_verif;
    }
    /**
     * @param string $date_derniere_verif
     */
    public function setDateDerniereVerif(string $date_derniere_verif): void
    {
        $this->date_derniere_verif = $date_derniere_verif;
    }
    /**
     * @return string
     */
    public function getDateProchaineVerif(): string
    {
        return $this->date_prochaine_verif;
    }
    /**
     * @param string $date_prochaine_verif
     */
    public function setDateProchaineVerif(string $date_prochaine_verif): void
    {
        $this->date_prochaine_verif = $date_prochaine_verif;
    }

    /**
     * @return string
     */
    public function __toString()
    {
        return  '*  Point_Entretien object<br/>'.
                '   nom_point_entretien= '.$this->nom_point_entretien.'<br/>'.
                '   periodicie= '.$this->periodicie.'<br/>'.
                '   date_derniere_verif= '.$this->date_derniere_verif.'<br/>'.
                '   date_prochaine_verif= '.$this->date_prochaine_verif;

    }

    /**
     *
     */
    public function checkModelExist(){

    }

    public function jsonSerialize() {
        return [
            '$periodicie' => $this->getPeriodicie(),
            '$date_derniere_verif' => $this->getDateDerniereVerif(),
            '$date_prochaine_verif' => $this->getDateProchaineVerif()
        ];
    }
}
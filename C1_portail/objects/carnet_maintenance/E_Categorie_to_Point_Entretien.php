<?php
/**
 * Enum E_Categorie_to_Point_Entretien
 * Permet de lier les catégorie au paire<nom_point d'entretien,periodicite>
 */
require_once('MyEnum.php');

abstract class E_Categorie_to_Point_Entretien extends MyEnum
{
    const __default = self::default;
    const default = array("rafistolage_paroi" => 6, "ecran" => 12, "etancheite_des_gaines" => 3);
    const acastillage = array("verif_boulons_haubans" => 2, "torsion_des_gaines" => 3);
    const coque = array("rafistolage_paroi" => 6);
    const circuit = array("torsion_des_gaines" => 3);
    const electricite = array("etancheite_des_gaines" => 3);
    const electronic = array("charge_batterie" => 1, "ecran" => 12);
    const moteur = array("niveau_huile" => 1, "charge_batterie" => 1, "clean_filtre_carburant" => 1);
    const propulsion = array("taux_pollution" => 2, "brulure_paroi" => 3);
    const supervitesse = array("niveau_sulfurite" => 2, "niveau_nos" => 3);
    const restaurant = array("usure_table" => 2, "reserve_nourriture" => 1);
    const greement = array("verif_boulons_haubans" => 2);
}
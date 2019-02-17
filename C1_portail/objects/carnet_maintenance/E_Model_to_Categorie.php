<?php
/**
 * Enum E_Modeleto_Categorie_
 * Permet de lier les noms des Point_Entretien au catégorie
 */

require_once('MyEnum.php');
require_once('E_Categorie_to_Point_Entretien.php');

abstract class E_Model_to_Categorie extends MyEnum
{
    const __default = self::default;
    const default = array(
        "acastillage" => E_Categorie_to_Point_Entretien::acastillage,
        "coque" => E_Categorie_to_Point_Entretien::coque,
        "circuit" => E_Categorie_to_Point_Entretien::circuit,
        "electricite" => E_Categorie_to_Point_Entretien::electricite,
        "electronic" => E_Categorie_to_Point_Entretien::electronic,
        "moteur" => E_Categorie_to_Point_Entretien::moteur);
    const voilier = array(
        "acastillage" => E_Categorie_to_Point_Entretien::acastillage,
        "coque" => E_Categorie_to_Point_Entretien::coque,
        "electricite" => E_Categorie_to_Point_Entretien::electricite,
        "greeement" => E_Categorie_to_Point_Entretien::greement);
    const trois_mat = array(
        "acastillage" => E_Categorie_to_Point_Entretien::acastillage, 
        "coque" => E_Categorie_to_Point_Entretien::coque, 
        "electricite" => E_Categorie_to_Point_Entretien::electricite, 
        "electronic" => E_Categorie_to_Point_Entretien::electronic);
    const moteur = array(
        "acastillage" => E_Categorie_to_Point_Entretien::acastillage, 
        "coque" => E_Categorie_to_Point_Entretien::coque, 
        "circuit" => E_Categorie_to_Point_Entretien::circuit, 
        "electricite" => E_Categorie_to_Point_Entretien::electricite, 
        "electronic" => E_Categorie_to_Point_Entretien::electronic, 
        "moteur" => E_Categorie_to_Point_Entretien::moteur, 
        "propulsion" => E_Categorie_to_Point_Entretien::propulsion);
    const sportif = array(
        "acastillage" => E_Categorie_to_Point_Entretien::acastillage, 
        "coque" => E_Categorie_to_Point_Entretien::coque, 
        "circuit" => E_Categorie_to_Point_Entretien::circuit, 
        "electricite" => E_Categorie_to_Point_Entretien::electricite, 
        "electronic" => E_Categorie_to_Point_Entretien::electronic, 
        "moteur" => E_Categorie_to_Point_Entretien::moteur, 
        "supervitesse");
    const zodiac = array(
        "acastillage" => E_Categorie_to_Point_Entretien::acastillage, 
        "coque" => E_Categorie_to_Point_Entretien::coque, 
        "electronic" => E_Categorie_to_Point_Entretien::electronic, 
        "moteur" => E_Categorie_to_Point_Entretien::moteur);
    const croisiere = array(
        "acastillage" => E_Categorie_to_Point_Entretien::acastillage, 
        "coque" => E_Categorie_to_Point_Entretien::coque, 
        "circuit" => E_Categorie_to_Point_Entretien::circuit, 
        "electricite" => E_Categorie_to_Point_Entretien::electricite, 
        "electronic" => E_Categorie_to_Point_Entretien::electronic, 
        "moteur" => E_Categorie_to_Point_Entretien::moteur, 
        "restaurant");
}
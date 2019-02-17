<?php
/**
 * Class d'objet EModele
 * - Contient tous les modèles de bateau
 */

require_once('MyEnum.php');

abstract class E_Model_to_Nom extends MyEnum
{
    const __default = self::default;
    const default = "default";
    const voilier = "voilier";
    const trois_mat = "trois_mat";
    const moteur = "moteur";
    const sportif = "sportif";
    const zodiac = "zodiac";
    const croisiere = "croisiere";
}
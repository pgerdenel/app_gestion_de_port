<?php
/**
 * Class qui définit un objet User
 */

namespace portail;

class User {

    /* Attributes */
    private $login;
    private $pass;
    private $type; // type utilisateur proprio ou admin

    /**
     * User constructor.
     */
    public function __construct() {
    }

    /**
     * @param $login
     * @param $pass
     * @return User
     */
    public static function construct_Login_And_Pass($login, $pass) {
        $instance = new self();
        $instance->login = $login;
        $instance->pass = $pass;
        return $instance;
    }

    /**
     * @param $login
     * @param $type
     * @return User
     */
    public static function construct_Session($login, $type) {
        $instance = new self();
        $instance->login = $login;
        $instance->type = $type;
        return $instance;
    }

    /* Getters & Setters */
    /**
     * @return String
     */
    public function getLogin() {
        return $this->login;
    }
    /**
     * @param String $login
     */
    public function setLogin($login) {
        $this->login = $login;
    }
    /**
     * @return String
     */
    public function getPass() {
        return $this->pass;
    }
    /**
     * @param String $pass
     */
    public function setPass($pass) {
        $this->pass = $pass;
    }
    /**
     * @return String
     */
    public function getType()
    {
        return $this->type;
    }
    /**
     * @param String $type
     */
    public function setType($type)
    {
        $this->type = $type;
    }


    /* Others Methodes/Functions */
    /**
     * @return string
     */
    public function __toString() {
        return "login= ".$this->login."\npass= ".$this->pass."\ntype= ".$this->type;
    }

    /**
     * @return false|string
     */
    public function to_json() {
        return json_encode(array(
            'login' => $this->login,
            'pass' => $this->pass
        ));
    }

    /**
     * @return false|string
     */
    public function to_json_Cookie() {
        return json_encode(array(
            'login' => $this->login,
            'type' => $this->type
        ));
    }


}

// test class
//$User = User::construct_Login_And_Pass("test1", "pass1");
//echo $User->to_json();
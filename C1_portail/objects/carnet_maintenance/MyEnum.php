<?php
/**
 * Class d'objet enum
 * SplTypes, PECL Extension non disponible sous windows
 */

abstract class MyEnum {

    private $value;

    final public function __construct($value) {
        try {
            $c = new ReflectionClass($this);
            if(!in_array($value, $c->getConstants())) {
                // throw IllegalArgumentException();
            }
            else {
                echo 'erreur';
            }
        }
        catch(ReflectionException $re) {
            echo 'my_enum ReflectionException erreur <br/>'.$re;
        }
        catch(InvalidArgumentException $iae) {
            echo 'my_enum InvalidArgumentException erreur <br/>'.$iae;
        }
        $this->value = $value;
    }

    final public function __toString() {
        return $this->value;
    }
}
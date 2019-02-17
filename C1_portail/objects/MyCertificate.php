<?php
/**
 * Created by IntelliJ IDEA.
 * User: AHC
 * Date: 03/01/2019
 * Time: 06:19
 */

namespace C1_portail;


class MyCertificate {

    private $autorityName;      // nom de l'authorité qui a validé le certificat
    private $email;             // email du propriétaire
    private $proprio;           // propritaire du certificat
    private $version;           // La version de X.509 à laquelle le certificat correspond ;
    private $serial;            // Le numéro de série du certificat ;
    private $algoChiffrement;   // L'algorithme de chiffrement utilisé pour signer le certificat ;
    private $DN;                // Le nom (DN, pour Distinguished Name) de l'autorité de certification émettrice ;
    private $dateStart;         // La date de début de validité du certificat ;
    private $dateEnd;           // La date de fin de validité du certificat ;
    private $subjectKey;        // L'objet de l'utilisation de la clé publique ;
    private $publicKey;         // La clé publique du propriétaire du certificat ;
    private $fingerPrint;

    /**
     * MyCertificate default constructor.
     */
    public function __construct() {
    }
    /**
     * MyCertificate constructor initialisation
     * @param $autorityName
     * @param $email
     * @param $proprio
     * @param $version
     * @param $serial
     * @param $algoChiffrement
     * @param $DN
     * @param $dateStart
     * @param $dateEnd
     * @param $subjectKey
     * @param $publicKey
     * @param $fingerPrint
     * @return MyCertificate
     */
    public static function __construct_init($autorityName, $email, $proprio, $version, $serial, $algoChiffrement, $DN, $dateStart, $dateEnd, $subjectKey, $publicKey, $fingerPrint){
        $c = new MyCertificate();
        $c->autorityName = $autorityName;
        $c->email = $email;
        $c->proprio = $proprio;
        $c->version = $version;
        $c->serial = $serial;
        $c->algoChiffrement = $algoChiffrement;
        $c->DN = $DN;
        $c->dateStart = $dateStart;
        $c->dateEnd = $dateEnd;
        $c->subjectKey = $subjectKey;
        $c->publicKey = $publicKey;
        $c->fingerPrint = $fingerPrint;
        return $c;
    }
    /**
     * MyCertificate constructor par JSONObject
     * @param $Certificate_json
     * @return MyCertificate
     */
    public static function __construct_JSON($Certificate_json){
        $c = new MyCertificate();
        $c->autorityName = $Certificate_json['autorityName'];
        $c->email =$Certificate_json['email'];
        $c->proprio = $Certificate_json['proprio'];
        $c->version = $Certificate_json['version'];
        $c->serial = $Certificate_json['serial'];
        $c->algoChiffrement = $Certificate_json['algoChiffrement'];
        $c->DN = $Certificate_json['DN'];
        $c->dateStart = $Certificate_json['dateStart'];
        $c->dateEnd = $Certificate_json['dateEnd'];
        $c->subjectKey = $Certificate_json['subjectKey'];
        $c->publicKey = $Certificate_json['publicKey'];
        $c->fingerPrint = $Certificate_json['fingerPrint'];
        return $c;
    }

    /**
     * @return mixed
     */
    public function getAutorityName()
    {
        return $this->autorityName;
    }
    /**
     * @param mixed $autorityName
     */
    public function setAutorityName($autorityName): void
    {
        $this->autorityName = $autorityName;
    }
    /**
     * @return mixed
     */
    public function getEmail()
    {
        return $this->email;
    }
    /**
     * @param mixed $email
     */
    public function setEmail($email): void
    {
        $this->email = $email;
    }
    /**
     * @return mixed
     */
    public function getProprio()
    {
        return $this->proprio;
    }
    /**
     * @param mixed $proprio
     */
    public function setProprio($proprio): void
    {
        $this->proprio = $proprio;
    }
    /**
     * @return mixed
     */
    public function getVersion()
    {
        return $this->version;
    }
    /**
     * @param mixed $version
     */
    public function setVersion($version): void
    {
        $this->version = $version;
    }
    /**
     * @return mixed
     */
    public function getSerial()
    {
        return $this->serial;
    }
    /**
     * @param mixed $serial
     */
    public function setSerial($serial): void
    {
        $this->serial = $serial;
    }
    /**
     * @return mixed
     */
    public function getAlgoChiffrement()
    {
        return $this->algoChiffrement;
    }
    /**
     * @param mixed $algoChiffrement
     */
    public function setAlgoChiffrement($algoChiffrement): void
    {
        $this->algoChiffrement = $algoChiffrement;
    }
    /**
     * @return mixed
     */
    public function getDN()
    {
        return $this->DN;
    }
    /**
     * @param mixed $DN
     */
    public function setDN($DN): void
    {
        $this->DN = $DN;
    }
    /**
     * @return mixed
     */
    public function getDateStart()
    {
        return $this->dateStart;
    }
    /**
     * @param mixed $dateStart
     */
    public function setDateStart($dateStart): void
    {
        $this->dateStart = $dateStart;
    }
    /**
     * @return mixed
     */
    public function getDateEnd()
    {
        return $this->dateEnd;
    }
    /**
     * @param mixed $dateEnd
     */
    public function setDateEnd($dateEnd): void
    {
        $this->dateEnd = $dateEnd;
    }
    /**
     * @return mixed
     */
    public function getSubjectKey()
    {
        return $this->subjectKey;
    }
    /**
     * @param mixed $subjectKey
     */
    public function setSubjectKey($subjectKey): void
    {
        $this->subjectKey = $subjectKey;
    }
    /**
     * @return mixed
     */
    public function getPublicKey()
    {
        return $this->publicKey;
    }
    /**
     * @param mixed $publicKey
     */
    public function setPublicKey($publicKey): void
    {
        $this->publicKey = $publicKey;
    }
    /**
     * @return mixed
     */
    public function getFingerPrint()
    {
        return $this->fingerPrint;
    }
    /**
     * @param mixed $fingerPrint
     */
    public function setFingerPrint($fingerPrint): void
    {
        $this->fingerPrint = $fingerPrint;
    }

    /**
     * @return string
     */
    public function toString() {
        return  
            "<br/>&emsp;autorityName".$this->autorityName.
            "<br/>&emsp;email".$this->email.
            "<br/>&emsp;proprio".$this->proprio.
            "<br/>&emsp;version".$this->version.
            "<br/>&emsp;serial".$this->serial.
            "<br/>&emsp;algoChiffrement".$this->algoChiffrement.
            "<br/>&emsp;DN".$this->DN.
            "<br/>&emsp;dateStart".$this->dateStart.
            "<br/>&emsp;dateEnd".$this->dateEnd.
            "<br/>&emsp;subjectKey".$this->subjectKey.
            "<br/>&emsp;publicKey".$this->publicKey.
            "<br/>&emsp;fingerPrint".$this->fingerPrint;
    }
    /**
     * @return false|string
     */
    public function toJson() {
        return json_encode(array(
            'autorityName' => $this->autorityName,
            'email' => $this->email,
            'proprio' => $this->proprio,
            'version' => $this->version,
            'serial' => $this->serial,
            'algoChiffrement' => $this->algoChiffrement,
            'DN' => $this->DN,
            'dateStart' => $this->dateStart,
            'dateEnd' => $this->dateEnd,
            'subjectKey' => $this->subjectKey,
            'publicKey' => $this->publicKey,
            'fingerPrint' => $this->fingerPrint
        ));
    }
    public function writeJSONCert($filename) {
        $json_cert = $this->toJson();
        $fp = fopen('../resources/'.$filename, 'w');
        fwrite($fp, json_encode($json_cert));
        fclose($fp);
    }

}

/*$cert = MyCertificate::__construct_init("Auth_srv_verisian", "email@admin.com", "Client1", "v1", "58:57:59:58:57:59:58:57:59:58:57:59", "RSA", "localhost", "01/01/01", "01/01/01", "O", "",  "");
$cert->writeJSONCert("cert.json");*/


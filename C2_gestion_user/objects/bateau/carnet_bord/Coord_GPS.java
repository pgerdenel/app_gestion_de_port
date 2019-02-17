package C2_gestion_user.objects.bateau.carnet_bord;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Class d'objet Coord_GPS
 * - représente les coordonnées GPS latitude, longitude de la position d'un port
 */

public class Coord_GPS {

    public static void main(String args[]) {
        /*Coord_GPS co = new Coord_GPS(0.0, 0.0);
        try {
            System.out.println(co.toJsonArray().toString(1));
        }
        catch(JSONException je) {
            System.out.println("error json "+je);
        }*/
    }

    private double latitude;
    private double longitude;

    public Coord_GPS(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Coord_GPS{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
    private JSONArray toJsonArray() {
        JSONArray ja = new JSONArray();
        try {
            ja.put(this.latitude);
            ja.put(this.longitude);
        }
        catch (JSONException e) {
            System.out.println("erreur "+e);
        }
        return ja;
    }
}
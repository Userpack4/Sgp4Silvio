package sgp4silvio.SGP4.silvio;

/**
 * Created by Silvio on 02/05/15.
 */
public class Posizione {

    private double azimuth, elevation, range, x, y, z, lat, lon, alt;


    public Posizione() {
        this.azimuth = 0;
        this.elevation = 0;
        this.range = 0;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.lat = 0;
        this.lon = 0;
        this.alt = 0;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }
}

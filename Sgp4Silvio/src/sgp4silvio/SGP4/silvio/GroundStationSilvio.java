package sgp4silvio.SGP4.silvio;

/**
 * Created by Silvio on 17/05/15.
 */
public class GroundStationSilvio {

    double degLat, degLon;  //in deg
    double altitude; //in metri
    String nomeLocalita;

    public GroundStationSilvio(double degLat, double degLon, double altitude, String nomeLocalita) {
        this.degLat = degLat;
        this.degLon = degLon;
        this.altitude = altitude;
        this.nomeLocalita = nomeLocalita;
    }

    public GroundStationSilvio(double degLat, double degLon, double altitude) {
        this.degLat = degLat;
        this.degLon = degLon;
        this.altitude = altitude;
    }

    public double getDegLat() {
        return degLat;
    }

    public void setDegLat(double degLat) {
        this.degLat = degLat;
    }

    public double getDegLon() {
        return degLon;
    }

    public void setDegLon(double degLon) {
        this.degLon = degLon;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getNomeLocalita() {
        return nomeLocalita;
    }

    public void setNomeLocalita(String nomeLocalita) {
        this.nomeLocalita = nomeLocalita;
    }
}

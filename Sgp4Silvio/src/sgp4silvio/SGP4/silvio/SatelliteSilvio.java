/*
 * 
 *  FROM Revisiting Spacetrack Report #3 AIAA 2006-6753 (http://celestrak.com/publications/AIAA/2006-6753/)
* This library use the SGP4 code to find Satellite Position in lat-lon-alt
 *  
 *  
 *  Author Silvio
 *  This is a non-professional implementation.
 *  If you use it, please cite the Author.
 *  
 *  
 */
package sgp4silvio.SGP4.silvio;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import sgp4silvio.SGP4.CSSI.SGP4SatData;
import sgp4silvio.SGP4.CSSI.SGP4unit;
import sgp4silvio.SGP4.CSSI.SGP4utils;

/**
 * Created by sil on 02/05/15.
 */
public class SatelliteSilvio implements Serializable {

    private String nome;
    private SGP4SatData data;
    private String tleline1, tleline2;
    private boolean isDeepSpace = false;  //se il periodo è maggiore di 225 minuti è DEEPSPACE
    private double period; //periodo del satellite in minuti

    private double[] pos = new double[3];
    private double[] vel = new double[3];

    private double azimuth, elevation, range, lat, lon, alt;
    private double timeOnBoard = 0.0;

    private boolean selected = false;

    private boolean errorInSatProp = false; //OGNI TANTO CI SONO SAT DECADUTI CHE HANNO ANCORA PER QUALCHE TEMPO IL TLE

    GroundStationSilvio GROUND_STATION = null;

    public SatelliteSilvio(String nameSat, String tle1, String tle2, GroundStationSilvio GROUND) {
        this.nome = nameSat.trim();
        this.tleline1 = tle1;
        this.tleline2 = tle2;
        this.data = inizializzaSatData(nameSat, tle1, tle2);
        period = 2 * Math.PI / data.no;
        if (period > 225) {
            isDeepSpace = true;
        } else {
            isDeepSpace = false;
        }
        this.GROUND_STATION = GROUND;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * INIZIALIZZA IL SATELLITE PER LA SUCCESSIVA PROPAGAZIONE
     *
     * @param nameSat - satellite's name
     * @param tleline1 first line tle
     * @param tleline2 second line tle
     * @return SGP4SatData structure
     */
    private static SGP4SatData inizializzaSatData(String nameSat, String tleline1, String tleline2) {

        // new sat data object
        SGP4SatData data = new SGP4SatData();
        // options
        char opsmode = SGP4utils.OPSMODE_IMPROVED; // OPSMODE_IMPROVED
        SGP4unit.Gravconsttype gravconsttype = SGP4unit.Gravconsttype.wgs72;
        // read in data and ini SGP4 data
        boolean result1 = SGP4utils.readTLEandIniSGP4(nameSat, tleline1, tleline2, opsmode, gravconsttype, data);
        if (!result1) {
            System.out.println("Error Reading / Ini Data, error code: " + data.error);
            return null;
        }

        return data;

    }

    public boolean isErrorInSatProp() {
        return errorInSatProp;
    }

    public void setErrorInSatProp(boolean errorInSatProp) {
        this.errorInSatProp = errorInSatProp;
    }

    /**
     * @param tempoDaPropagare jd time to propagation
     * @return
     */
    public Posizione calcolaPosizioneSatelliteSubPoint(double tempoDaPropagare) {

        double minutesSinceEpoch = (tempoDaPropagare - this.getData().jdsatepoch) * 24.0 * 60.0;

        boolean result = SGP4unit.sgp4(data, minutesSinceEpoch, pos, vel);
        if (!result) {
            System.out.println("Error in Sat Prop subpoint");
            return null;
        }
        double x, y, z;
        x = pos[0];
        y = pos[1];
        z = pos[3];
        double east_longitude = TempoSilvio.moduloInPascal(GlobalSilvio.AcTan(y, x) - TempoSilvio.ThetaG_JD(tempoDaPropagare), GlobalSilvio.twopi);
        double east_lon_deg = Math.toDegrees(east_longitude);

//        System.out.println("!!  " + east_lon_deg);
        if (east_longitude > Math.PI) {
            east_longitude = east_longitude - 2 * Math.PI;
        }

        double f = 1 / 298.26;
        double e_quadrato = 2 * f - (f * f);

        double C = 0;
        double fi = Math.atan(z / Math.sqrt((x * x) + (y * y))); //geocentric latitude (la lat se la terra fosse una sfera)

        double R = Math.sqrt((x * x) + (y * y));
        double fi2 = fi; //fi2 è la lat che per tentativi deve essere pari a geodetic lat (sullo zenith del punto a terra)

        do {
            fi2 = fi;
            C = 1 / Math.sqrt(1 - e_quadrato * Math.sin(fi2) * Math.sin(fi2));
            fi = Math.atan((z + (GlobalSilvio.re * C * e_quadrato * Math.sin(fi2))) / R);
        } while ((fi - fi2) > 0.000003);

        double altitude = R / Math.cos(fi) - GlobalSilvio.re * C;
        Posizione p = new Posizione();
        p.setX(x);
        p.setY(y);
        p.setZ(z);
        p.setLat(fi);
        p.setLon(east_longitude);
        p.setAlt(altitude);

        return p;

    }

    public Posizione calcola_Dati_Sat_From_Stazione_a_Terra(double xs, double ys, double zs,
            double tempoDaPropagare) {
        double xo, yo, zo, rx, ry, rz, theta, top_s, top_e, top_z;
        double minutesSinceEpoch = (tempoDaPropagare - this.getData().jdsatepoch) * 24.0 * 60.0;

        double lat = Math.toRadians(GROUND_STATION.getDegLat());
        double lon = Math.toRadians(GROUND_STATION.getDegLon());
        double alt = Math.toRadians(GROUND_STATION.getAltitude());

        Posizione userpos = calcola_User_Pos_OblateSpheroid(lat, lon, alt, tempoDaPropagare);
        theta = (TempoSilvio.ThetaG_JD(tempoDaPropagare) + lon) % GlobalSilvio.twopi;
        rx = xs - userpos.getX();
        ry = ys - userpos.getY();
        rz = zs - userpos.getZ();
        top_s = Math.sin(lat) * Math.cos(theta) * rx + Math.sin(lat) * Math.sin(theta) * ry - Math.cos(lat) * rz;
        top_e = -Math.sin(theta) * rx + Math.cos(theta) * ry;
        top_z = Math.cos(lat) * Math.cos(theta) * rx + Math.cos(lat) * Math.sin(theta) * ry + Math.sin(lat) * rz;
        double az = Math.atan(-top_e / top_s);

        if (top_s > 0) {
            az = az + Math.PI;
        }

        if (az < 0) {
            az = az + GlobalSilvio.twopi;
        }

        double rg = Math.sqrt(rx * rx + ry * ry + rz * rz);
        double el = Math.asin(top_z / rg);
        Posizione p = new Posizione();
        p.setAzimuth(az);
        p.setElevation(el);
        p.setRange(rg);
        return p;

    }

    public static Posizione calcola_User_Pos_OblateSpheroid(double lat, double lon, double alt, double time) {
        //siccome non è una sfera
        //we must determine the geocentric latitude from the geodetic latitude (geodetic è quella delle mappe)

        double lat_centric = Math.atan(((GlobalSilvio.earth_polar_radius * GlobalSilvio.earth_polar_radius) / (GlobalSilvio.re * GlobalSilvio.re)) * Math.tan(lat));
        double f = 1 / 298.26;
        double C = 1 / Math.sqrt(((Math.sin(lat) * Math.sin(lat) * (f - 2) * f) + 1));
        double S = ((1 - f) * (1 - f) * C);

        double theta = 0;
        theta = (TempoSilvio.ThetaG_JD(time) + lon) % GlobalSilvio.twopi;

        Posizione p = new Posizione();
        p.setX(GlobalSilvio.re * C * Math.cos(lat) * Math.cos(theta));
        p.setY(GlobalSilvio.re * C * Math.cos(lat) * Math.sin(theta));
        p.setZ(GlobalSilvio.re * S * Math.sin(lat));

        return p;
    }

    public void calcola_dati_vettoriali(double tempoDaPropagare) {
        double rx = 0;
        double ry = 0;
        double rz = 0;
        double theta = 0;
        double top_s = 0;
        double top_e = 0;
        double top_z = 0;

        double minutesSinceEpoch = (tempoDaPropagare - this.getData().jdsatepoch) * 24.0 * 60.0;

        boolean result = SGP4unit.sgp4(data, minutesSinceEpoch, pos, vel);

        if (!result) {
//            System.out.println("Error in Sat Prop from " + this.getNome());
            setErrorInSatProp(true);
        }
        double xs, ys, zs;
        xs = pos[0];
        ys = pos[1];
        zs = pos[2];
        double east_longitude = TempoSilvio.moduloInPascal(GlobalSilvio.AcTan(ys, xs) - TempoSilvio.ThetaG_JD(tempoDaPropagare), GlobalSilvio.twopi);
        double east_lon_deg = Math.toDegrees(east_longitude);

//        System.out.println("!!  " + east_lon_deg);
        if (east_longitude > Math.PI) {
            east_longitude = east_longitude - 2 * Math.PI;
        }

        double f = 1 / 298.26;
        double e_quadrato = 2 * f - (f * f);

        double C = 0;
        double fi = Math.atan(zs / Math.sqrt((xs * xs) + (ys * ys))); //geocentric latitude (la lat se la terra fosse una sfera)

        double R = Math.sqrt((xs * xs) + (ys * ys));
        double fi2 = fi; //fi2 è la lat che per tentativi deve essere pari a geodetic lat (sullo zenith del punto a terra)

        do {
            fi2 = fi;
            C = 1 / Math.sqrt(1 - e_quadrato * Math.sin(fi2) * Math.sin(fi2));
            fi = Math.atan((zs + (GlobalSilvio.re * C * e_quadrato * Math.sin(fi2))) / R);
        } while ((fi - fi2) > 0.000003);

        double altitude = R / Math.cos(fi) - GlobalSilvio.re * C;
        this.lat = fi;
        this.lon = east_longitude;
        this.alt = altitude;

        if (GROUND_STATION != null) {
            double lat = Math.toRadians(GROUND_STATION.getDegLat());
            double lon = Math.toRadians(GROUND_STATION.getDegLon());
            double alt = Math.toRadians(GROUND_STATION.getAltitude());

            Posizione userpos = calcola_User_Pos_OblateSpheroid(lat, lon, alt, tempoDaPropagare);
            theta = (TempoSilvio.ThetaG_JD(tempoDaPropagare) + lon) % GlobalSilvio.twopi;
            rx = xs - userpos.getX();
            ry = ys - userpos.getY();
            rz = zs - userpos.getZ();
            top_s = Math.sin(lat) * Math.cos(theta) * rx + Math.sin(lat) * Math.sin(theta) * ry - Math.cos(lat) * rz;
            top_e = -Math.sin(theta) * rx + Math.cos(theta) * ry;
            top_z = Math.cos(lat) * Math.cos(theta) * rx + Math.cos(lat) * Math.sin(theta) * ry + Math.sin(lat) * rz;
            double az = Math.atan(-top_e / top_s);

            if (top_s > 0) {
                az = az + Math.PI;
            }

            if (az < 0) {
                az = az + GlobalSilvio.twopi;
            }

            double rg = Math.sqrt(rx * rx + ry * ry + rz * rz);
            double el = Math.asin(top_z / rg);

            this.azimuth = az;
            this.elevation = el;
            this.range = rg;

        } //fine groundstation null

    }

    public SGP4SatData getData() {
        return data;
    }

    public boolean isDeepSpace() {
        return isDeepSpace;
    }

    /**
     * @return il periodo in minuti
     */
    public double getPeriod() {
        return period;
    }

    public double[] getPos() {
        return pos;
    }

    public double[] getVel() {
        return vel;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public double getElevation() {
        return elevation;
    }

    public double getRange() {
        return range;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getAlt() {
        return alt;
    }

    public synchronized double getTimeOnBoardInJD() {
        return timeOnBoard;
    }

    public static double getJDofDateObject(Date d) {
        Calendar tcal = Calendar.getInstance(TimeZone.getTimeZone("UTC")); //perchè i calcoli della posizione sat sono fatti in UTC
        tcal.setTime(d);
        int year = tcal.get(Calendar.YEAR);
        int month = tcal.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = tcal.get(Calendar.DAY_OF_MONTH);
        int hour = tcal.get(Calendar.HOUR_OF_DAY);
        int minute = tcal.get(Calendar.MINUTE);
        int second = tcal.get(Calendar.SECOND);

        return SGP4utils.jday(year, month, day, hour, minute, second);
    }

    public void setTimeOnBoardInJD(final double jDofDateObject) {
        timeOnBoard = jDofDateObject;
    }

}

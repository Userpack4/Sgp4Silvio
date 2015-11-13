/*
 * MAIN FUNCTION TO TEST THE IMPLEMENTATION
 *  FROM Revisiting Spacetrack Report #3 AIAA 2006-6753 (http://celestrak.com/publications/AIAA/2006-6753/)
 * This library use the SGP4 code to find Satellite Position
 *  
 *  
 *  Author Silvio
 *  This is a non-professional implementation.
 *  If you use it, please cite the Author.
 *  
 *  
 */
package sgp4silvio;

import java.util.Date;
import sgp4silvio.SGP4.CSSI.SGP4utils;
import sgp4silvio.SGP4.silvio.SatelliteSilvio;
import sgp4silvio.SGP4.silvio.GroundStationSilvio;

/**
 *
 * @author sil
 */
public class Sgp4Silvio {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        GroundStationSilvio GROUND_STATION = new GroundStationSilvio(51.508, -0.1257, 10, "London");

        // tle data
        String name = "ISS (ZARYA)";
        String line1 = "1 25544U 98067A   15316.49329135  .00011444  00000-0  17418-3 0  9990";
        String line2 = "2 25544  51.6445  63.9001 0006569 132.3028 336.8914 15.55005073971115";
        SatelliteSilvio sat = new SatelliteSilvio(name, line1, line2, GROUND_STATION);
        sat.setTimeOnBoardInJD(SatelliteSilvio.getJDofDateObject(new Date()));

        //propagation
        sat.calcola_dati_vettoriali(sat.getTimeOnBoardInJD());

        double lat = sat.getLat() * 180 / Math.PI;
        double lon = sat.getLon() * 180 / Math.PI;
        double velocity = SGP4utils.mag(sat.getVel());
        double altitude = sat.getAlt();
        double azimuth = Math.abs((double) Math.round((sat.getAzimuth() * 180 / Math.PI) * 100) / 100);
        double elevation = Math.round((sat.getElevation() * 180 / Math.PI) * 100) / 100;
        double inclination = Math.round((sat.getData().inclo * 180 / Math.PI) * 100) / 100;

        System.out.println("Lat " + lat);
        System.out.println("Lon " + lon);
        System.out.println("velocity " + velocity);
        System.out.println("altitude " + altitude);
        System.out.println("inclination " + inclination);

        System.out.println("##FROM GROUND## ");
        System.out.println("azimuth " + azimuth);
        System.out.println("elevation " + elevation);

    }

}

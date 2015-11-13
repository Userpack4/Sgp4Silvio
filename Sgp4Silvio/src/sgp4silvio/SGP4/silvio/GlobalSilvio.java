/*
 * 
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



package sgp4silvio.SGP4.silvio;


import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import sgp4silvio.SGP4.CSSI.SGP4utils;

/**
 * Created by sil on 02/05/15.
 */
public class GlobalSilvio {


    public final static double re = 6378.135; //il raggio della terra in km
    final static double earth_polar_radius = 6356.751; //il raggio della terra al polo in km
    //tiene conto della The flattening term, as defined in WGS-72, is only 1/298.26—a very small deviation from a perfect sphere.
    final static double twopi = Math.PI * 2;
    final static double DEGTORAD = Math.PI / 180.0;
    final static double RADTODEG = 180.0 / Math.PI;

    

    public static double AcTan(double sinx, double cosx) {
        double Actan;
        if (cosx == 0) {
            if (sinx > 0) {
                Actan = Math.PI / 2;
            } else {
                Actan = 3 * Math.PI / 2;
            }
        } else if (cosx > 0) {
            Actan = Math.atan(sinx / cosx);
        } else {
            Actan = Math.PI + Math.atan(sinx / cosx);
        }
        return Actan;
    }

    /**
     * @param lat1 in degree
     * @param lon1 in degree
     * @param lat2 in degree
     * @param lon2 in degree
     * @return dist in km
     * haversine formula
     */
    public static double calculate_range(double lat1, double lon1, double lat2, double lon2) {

        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double R = 6371; //mean radius
        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
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


}
































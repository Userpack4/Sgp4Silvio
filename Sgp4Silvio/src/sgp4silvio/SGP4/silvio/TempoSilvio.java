/*
 * 
 *  FROM Revisiting Spacetrack Report #3 AIAA 2006-6753 (https://celestrak.com/)
 * This library use the SGP4 code to find Satellite Position in lat-lon-alt
 *  
 *  
 *  Author Silvio
 *  This is a non-professional implementation.
 *  If you use it, please cite the Author.
 *  
 */



package sgp4silvio.SGP4.silvio;


/**
 * Created by sil on 02/05/15.
 */
public class TempoSilvio {


    /**
     * Calcola Julian Date del year inserito al giorno 0.0 year
     * From Clestrack:
     * using the approach on Page 61 of Astronomical Algorithms by Jean Meeus.
     * This latter text is an excellent reference source for many relevant calculations in orbital mechanics
     * and is highly recommended.
     * @param year
     * @return juliandate
     */

    public static double julian_Date_of_Year(double year){

        long A, B;
        year = year -1 ;
        A = (long)(year/100); //ottiene la parte intera

        B = 2 - A + (long)(A/4);
        return (long)(365.25 * year) + (long)(30.6001 * 14) + 1720994.5 + B;

    }

    public static int day_of_Year(int yr, int mo, int dy ){
        int [] days = {31,28,31,30,31,30,31,31,30,31,30,31};
        int day;
        day = 0;
        for (int i = 1; i < mo ; i++) {
            day = day + days[i];
        }
        day = day + dy;
        if ((yr % 4) == 0 && (((yr % 100) != 0) || ((yr % 400) == 0)) && (mo > 2)){
            day = day + 1;
        }
        return day;
    }

    public static double julian_Date(int yr, int mo, int dy){

        return julian_Date_of_Year(yr) +  day_of_Year(yr,mo,dy);

    }

    /*------------------------------------------------------------------------------
    |PRESA DA Sgp4Unit!!!!!!!!!!!
    |                           procedure julianday
    |
    |  this procedure finds the julian date given the year, month, day, and time.
    |    the julian date is defined by each elapsed day since noon, jan 1, 4713 bc.
    |
    |  algorithm     : calculate the answer in one step for efficiency
    |
    |  author        : david vallado                  303-344-6037    1 mar 2001
    |
    |  inputs          description                    range / units
    |    year        - year                           1900 .. 2100
    |    mon         - month                          1 .. 12
    |    day         - day                            1 .. 28,29,30,31
    |    hr          - universal time hour            0 .. 23
    |    min         - universal time min             0 .. 59
    |    sec         - universal time sec             0.0 .. 59.999
    |    whichtype   - julian or gregorian calender   'j' or 'g'
    |
    |  outputs       :
    |    jd          - julian date                    days from 4713 bc
    |
    |  locals        :
    |    b           - var to aid gregorian dates
    |
    |  coupling      :
    |    none.
    |
    |  references    :
    |    vallado       2001, 186-188, alg 14, ex 3-14
    |
    -----------------------------------------------------------------------------*/
    public static double julianday(int year, int mon, int inday, int hr, int min,
                             double sec) {
        double jd = 367.0 * year
                - (int) ((7 * (year + (int) ((mon + 9) / 12))) * 0.25)
                + (int) (275 * mon / 9) + inday + 1721013.5
                + ((sec / 60.0 + min) / 60.0 + hr) / 24.0; // ut in days
        // - 0.5*sgn(100.0*year + mon - 190002.5) + 0.5;
        return jd;
    }


    /**
     * Calcola il Greenwich Mean Sidereal Time
     * (Reference:  The 1992 Astronomical Almanac, page B6.)
     *
     * @param jd the input is the Julian Date of the time of interest
     * @return ritorna il GMST in radians
     * TESTATA SU ESEMPIO CELESTRACK
     */

    public static double ThetaG_JD(double jd){
        double UT,TU,GMST;
        UT = frac(jd + 0.5); //da la parte decimale di jd+0.5
        jd   = jd  - UT;
        TU   = (jd - 2451545.0)/36525;
        GMST = 24110.54841 + TU * (8640184.812866 + TU * (0.093104 - TU * 6.2E-6));
        GMST = moduloInPascal(GMST + 86400.0*1.00273790934*UT, 86400.0);
        return (2* Math.PI * (GMST/86400.0));
    }



    private static double frac(final double num) {
        //ritorna la parte frazionaria di un double e emula il frac Pascal
        return num - (long)num;
    }

    /**
     * Ritorna il modulo di un double
     * ATTENZIONE: il turbo pascal Delphi non si comporta come l'operatore % in java
     * Vedere exp sotto.
     *
     * @param arg1
     * @param arg2
     * @return
     */
    public static double moduloInPascal(final double arg1, final double arg2) {
        //ritorna arg1 mod arg2 stile pascal arg1 mod arg2
        if(arg2 == 0 || arg2 < 0){
            //nothing
        }
        else if (arg2 == 1){
            return 0;
        }
        else{
            return (arg1 % arg2 + arg2) % arg2;
        }
        return 0;
    }







}

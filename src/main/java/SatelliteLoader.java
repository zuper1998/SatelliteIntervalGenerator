import org.hipparchus.util.FastMath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class SatelliteLoader {
    public String Name;
    public double a; // semi major axis in meters
    public double e; // eccentricity
    public double i; // inclination
    public double omega; // perigee argument
    public double raan; // right ascension of ascending node
    public double lM; // Mean Anomaly M = sqrt(mu/a^3) time
    // gravitation coefficient
    final static double mu =  3.986004415e+14;

    /**
     * https://orbitalmechanics.info/ to see what each param does
     * @param _a Semi Major Axis of the satellite orbit
     * @param _e Eccentricity of the satellite orbit
     * @param _i Inclination of the satellite orbit
     * @param _omega Perigee argument of the satellite orbit
     * @param _raan Right ascension of the ascending node of the satellite orbit
     * @param _lM Mean anomaly of the satellite orbit
     * @param _Name Name of the satellite
     */
    SatelliteLoader(double _a, double _e, double _i, double _omega, double _raan, double _lM, String _Name){
        a=_a;
        e=_e;
        i=_i;
        omega=_omega;
        raan=_raan;
        lM=_lM;
        Name = _Name;
    }

    /**
     * @param FileName The name of the file to load the satellites from
     * @return an array of satellites
     *
     */
    public static ArrayList<SatelliteLoader> SatLoader(String FileName){
        File f = new File(FileName);
        ArrayList<SatelliteLoader> sats = new ArrayList<>();
        BufferedReader br;
        try {
             br = new BufferedReader(new FileReader(f));

        // Line Format: semi-major axis| eccentricity | inclination | longitude asc. node | argument of periapsis | time of periapsis | sat name
        String s;
        while ((s = br.readLine()) != null) {
            //s = s.replaceAll("[^\\x20-\\x7e]", "");
            StringTokenizer st = new StringTokenizer(s,"|");
            // semi major axis
            double semi = (Double.parseDouble(st.nextToken())+6371) *1000;
            // Eccentricity
            double ecc  =  Double.parseDouble(st.nextToken());
            // Inclination
            double inc = FastMath.toRadians(Double.parseDouble(st.nextToken()));
            // raan
            double raas = FastMath.toRadians(Double.parseDouble(st.nextToken()));
            // omega
            double omega = FastMath.toRadians(Double.parseDouble(st.nextToken()));
            // Mean Anomaly
            //double lM = FastMath.toRadians( Double.parseDouble(st.nextToken())); //FastMath.toDegrees(Math.sqrt(mu/(semi*semi*semi)) * Double.parseDouble(st.nextToken()) % 360);
            double lM = FastMath.toRadians( Double.parseDouble(st.nextToken()));
            //System.out.println(lM);
            sats.add(new SatelliteLoader(semi,ecc,inc,raas,omega,lM,st.nextToken().strip()));

        }

        } catch (Exception e){
            System.out.println(e);
        }
        return sats;
    }
}

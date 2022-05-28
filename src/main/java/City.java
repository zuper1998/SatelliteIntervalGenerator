import org.hipparchus.util.FastMath;

public class City {
     public double longitude = FastMath.toRadians(47.49840560);
     public double latitude = FastMath.toRadians(19.04075780);
     public double altitude = 106.46;
    String name = "Budapest";
    City(){}

    /**
     * @param _longitude The longitude of the city
     * @param _latitude The latitude of the city
     * @param _altitude The altitude of city
     * @param _name The name of the city
     */
    City(double _longitude, double _latitude, double _altitude, String _name){
        longitude=FastMath.toRadians(_longitude);latitude=FastMath.toRadians(_latitude);altitude=_altitude;name=_name;
    }
    public String getName(){
        return name;
    }
}
